package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.ErrorResponse;
import com.example.demo.exceptions.InternalServerException;
import com.example.demo.exceptions.NotAuthorizedException;
import com.example.demo.exceptions.RecordNotFoundException;
import com.example.demo.model.ProjectDetails;
import com.example.demo.model.UserProjects;
import com.example.demo.service.ProjectService;

@RestController
public class MainController {

	@Autowired
	private ProjectService projectService;

	@GetMapping("/projects/{userName}")
	public @ResponseBody UserProjects getProjectsByUser(@PathVariable("userName") String userName)
			throws InternalServerException, RecordNotFoundException {
		return projectService.getProjectsByUser(userName);
	}

	@GetMapping("/projects/{userName}/{projectId}")
	public @ResponseBody ProjectDetails getProjectById(@PathVariable("userName") String userName,@PathVariable("projectId") Long projectId) throws InternalServerException, RecordNotFoundException {
		return projectService.getProjectDetails(userName, projectId);
	}

	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setStatusCode(HttpStatus.NOT_FOUND.value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InternalServerException.class)
	public ResponseEntity<ErrorResponse> internalExceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NotAuthorizedException.class)
	public ResponseEntity<ErrorResponse> unauthorizedExceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setStatusCode(HttpStatus.UNAUTHORIZED.value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}
}
