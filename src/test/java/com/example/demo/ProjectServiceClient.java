package com.example.demo;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.ProjectDetails;
import com.example.demo.model.UserProjects;

@Service
public class ProjectServiceClient {

	private final RestTemplate restTemplate;

	public ProjectServiceClient(RestTemplateBuilder restTemplateBuilder) {
		restTemplate = restTemplateBuilder.build();
	}

	public UserProjects getProjectsByUser(String userName) {
		return restTemplate.getForObject("/projects/{userName}", UserProjects.class, userName);
	}

	public ProjectDetails getProjectById(String userName, Long projectId) {
		return restTemplate.getForObject("/projects/{userName}/{projectId}", ProjectDetails.class, userName, projectId);
	}
}
