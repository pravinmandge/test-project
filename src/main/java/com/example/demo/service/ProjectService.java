package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.demo.exceptions.InternalServerException;
import com.example.demo.exceptions.RecordNotFoundException;
import com.example.demo.model.Project;
import com.example.demo.model.ProjectDetails;
import com.example.demo.model.UserProjects;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

	@Value("${basic.auth.token}")
	private String accessToken;

	@Value("${github.repo.url}")
	private String repoURL;

	public UserProjects getProjectsByUser(String userName) throws InternalServerException, RecordNotFoundException {
		logger.info("Fetching projects by user name");
		List<UserProjects> userProjects = null;
		ClassLoader classLoader = getClass().getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream("user-projects.json");) {
			ObjectMapper objectMapper = new ObjectMapper();

			// convert json string to object
			userProjects = objectMapper.readValue(inputStream, new TypeReference<List<UserProjects>>() {
			});
		} catch (Exception e) {
			logger.error("Error while fetching projects by user name");
			throw new InternalServerException(e.getMessage());
		}

		UserProjects projects = userProjects.stream().filter(p -> p.getUserName().equals(userName)).findAny()
				.orElse(null);

		if (projects == null || CollectionUtils.isEmpty(projects.getProjects())) {
			logger.info("No projects found for user:", userName);
			throw new RecordNotFoundException("No projects found for User: " + userName);
		}
		return projects;
	}

	public ProjectDetails getProjectDetails(String userName, Long projectId)
			throws InternalServerException, RecordNotFoundException {
		logger.info("Fetching project details by user name and project id...");

		UserProjects userProjects = getProjectsByUser(userName);
		Project project = userProjects.getProjects().stream().filter(p -> p.getId().equals(projectId)).findAny()
				.orElse(null);

		if (project != null) {

			try {
				String gitUrl = project.getUrl();
				URL aURL = new URL(gitUrl);
				String[] path = aURL.getPath().split("/");
				String owner = path[1];
				String repo = path[2].replace(".git", "");

				ProjectDetails projectDetails = new ProjectDetails();

				projectDetails.setReadMe(getProjectReadme(owner, repo));
				projectDetails.setContributors(getProjectContributors(owner, repo));
				projectDetails.setNoOfCommits(getProjectCommits(owner, repo));
				return projectDetails;
			} catch (MalformedURLException e) {
				logger.error("Error while parding GIT URL: ", e);
				throw new InternalServerException("Error while parsing GIT URL: " + e.getMessage());
			}
		} else {
			logger.info("No project found for User: " + userName + ", Project Id: " + projectId);
			throw new RecordNotFoundException("No project found for User: " + userName + ", Project Id: " + projectId);
		}
	}

	private String getProjectReadme(String owner, String repo) throws RecordNotFoundException, InternalServerException {

		HttpGet request = new HttpGet(repoURL + "/" + owner + "/" + repo + "/readme");

		populateHeaders(request);

		try (CloseableHttpClient newHttpClient = HttpClientBuilder.create().build()) {
			HttpResponse response = newHttpClient.execute(request);

			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			ObjectMapper objectMapper = new ObjectMapper();

			// convert json string to object
			HashMap readmeData = objectMapper.readValue(response.getEntity().getContent(),
					new TypeReference<HashMap>() {
					});

			if (readmeData != null) {
				String encodedContent = (String) readmeData.get("content");

				return new String(Base64.decodeBase64(encodedContent.getBytes()));
			}

		} catch (Exception e) {
			throw new InternalServerException();
		}
		throw new RecordNotFoundException();
	}

	private List<String> getProjectContributors(String owner, String repo) throws InternalServerException {
		List<String> contributors = new ArrayList<String>();

		HttpGet request = new HttpGet(repoURL + "/" + owner + "/" + repo + "/collaborators");

		populateHeaders(request);

		try (CloseableHttpClient newHttpClient = HttpClientBuilder.create().build()) {
			HttpResponse response = newHttpClient.execute(request);

			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			ObjectMapper objectMapper = new ObjectMapper();

			// convert json string to object
			List<HashMap> contributorsData = objectMapper.readValue(response.getEntity().getContent(),
					new TypeReference<List<HashMap>>() {
					});

			if (!CollectionUtils.isEmpty(contributorsData)) {
				contributorsData.forEach(c -> {
					if (c.containsKey("login")) {
						contributors.add((String) c.get("login"));
					}
				});
			}
			return contributors;
		} catch (Exception e) {
			throw new InternalServerException("Error while fetching contributors data." + e.getMessage());
		}
	}

	private int getProjectCommits(String owner, String repo) throws InternalServerException {

		HttpGet request = new HttpGet(repoURL + "/" + owner + "/" + repo + "/commits");

		populateHeaders(request);

		try (CloseableHttpClient newHttpClient = HttpClientBuilder.create().build()) {
			HttpResponse response = newHttpClient.execute(request);

			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			ObjectMapper objectMapper = new ObjectMapper();

			// convert json string to object
			List commitsData = objectMapper.readValue(response.getEntity().getContent(), new TypeReference<List>() {
			});

			return CollectionUtils.isEmpty(commitsData) ? 0 : commitsData.size();

		} catch (IOException e) {
			throw new InternalServerException("Error while fetching commit data." + e.getMessage());
		}
	}

	private void populateHeaders(HttpGet getMethod) {
		// preparing header
		String encodedAuth = "Basic " + accessToken;

		getMethod.addHeader("Authorization", encodedAuth);
		getMethod.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
	}
}
