package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpStatusCodeException;

import com.example.demo.model.Project;
import com.example.demo.model.ProjectDetails;
import com.example.demo.model.UserProjects;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@RestClientTest(ProjectServiceClient.class)
public class ProjectServiceClientTest {

	@Autowired
	private ProjectServiceClient client;

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private ObjectMapper objectMapper;

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void whenCallingGetUserProjects() throws Exception {
		List<Project> projects = new ArrayList<Project>();
		projects.add(new Project(1L, "Spring Project", "https://github.com/pravinmandge/spring-projects.git"));
		projects.add(new Project(2L, "Programming Ebooks", "https://github.com/pravinmandge/programming-ebooks.git"));
		projects.add(new Project(3L, "Spring Boot Sample", "https://github.com/pravinmandge/spring-boot-sample.git"));

		UserProjects userProjects = new UserProjects("pravin", projects);
		String userProjectsString = objectMapper.writeValueAsString(userProjects);

		this.server.expect(requestTo("/projects/pravin"))
				.andRespond(withSuccess(userProjectsString, MediaType.APPLICATION_JSON));

		userProjects = this.client.getProjectsByUser("pravin");

		assertThat(userProjects.getUserName()).isEqualTo("pravin");
		assertThat(userProjects.getProjects().size()).isEqualTo(3);
	}

	@Test
	public void whenNoProjectFoundForUser() throws Exception {

		this.server.expect(requestTo("/projects/mahesh")).andRespond(withStatus(HttpStatus.NOT_FOUND));

		try {
			this.client.getProjectsByUser("mahesh");
		} catch (HttpStatusCodeException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

	@Test
	public void whenCallingGetProjectDetails() throws Exception {
		ProjectDetails projectDetails = new ProjectDetails(
				"# Programming Ebooks\n\nThese are all the programming ebooks I have collected.\n",
				Collections.singletonList("pravinmandge"), 5);
		String userProjectsString = objectMapper.writeValueAsString(projectDetails);

		this.server.expect(requestTo("/projects/pravin/2"))
				.andRespond(withSuccess(userProjectsString, MediaType.APPLICATION_JSON));

		projectDetails = this.client.getProjectById("pravin", 2L);

		assertThat(projectDetails.getReadMe()).contains("Ebooks");
		assertThat(projectDetails.getNoOfCommits()).isEqualTo(5);
	}

	@Test
	public void whenNoProjectDetailsFoundForUserAndProjectId() throws Exception {

		this.server.expect(requestTo("/projects/pravin/4")).andRespond(withStatus(HttpStatus.NOT_FOUND));

		try {
			this.client.getProjectById("pravin", 4L);
		} catch (HttpStatusCodeException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}
}
