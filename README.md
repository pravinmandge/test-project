# test-project
Teletronics test project

This is the spring boot test project which contains two endpoints as below

# 1. GET /projects/{userName}
     which respond with a JSON document containing the user’s name and a list of projects. Each project contains the GIT URL and title of the project and unique project id asscociate with it.
     
Two test cases covers the positive and negative scenario for testing this end point:
  # a. whenCallingGetUserProjects(com.example.demo.ProjectServiceClientTest) 
      returns list of projects asscociated with the userName
  # b. whenNoProjectFoundForUser(com.example.demo.ProjectServiceClientTest)
      return no records as no project associated with the userName


# 2. GET /projects/{userName}/{projectId}
    which respond with a JSON document with content from the project’s readme.md, list of contributors and number of total
commits.

Two test cases covers the positive and negative scenario for testing this end point:
  # a. whenCallingGetProjectDetails(com.example.demo.ProjectServiceClientTest)
      returns project details asscociated with the userName and project id
  # b. whenNoProjectDetailsFoundForUserAndProjectId(com.example.demo.ProjectServiceClientTest)
      return no records as no project associated with the userName and project id

Following is the CI with Docker Hub in order to build docker image:
https://cloud.docker.com/repository/docker/pravinmandge/test-project/general
