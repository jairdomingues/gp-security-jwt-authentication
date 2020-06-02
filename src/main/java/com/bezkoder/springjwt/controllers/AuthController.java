package com.bezkoder.springjwt.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.response.JwtResponse;
import com.bezkoder.springjwt.services.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	UserService userService;

	@PostMapping("/signin")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		JwtResponse jwtResponse = userService.login(loginRequest.getUsername(), loginRequest.getPassword(),
				loginRequest.getFusohorario());
		return ResponseEntity.ok(jwtResponse);
	}

	@PostMapping(produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest signupRequest) {
		String id = userService.createUser(signupRequest);
		userService.createNotificationUser();
		if (signupRequest.getAccountShare() != null) {
			userService.createAccount(signupRequest.getAccountShare());
		}
		return ResponseEntity.ok(id);
	}

	@PutMapping("/change_password/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void changePassword(@PathVariable(name = "id") Long userId, @RequestBody String newPassword) {
		userService.changePassword(userId, newPassword);
	}

	@GetMapping(produces = "application/json")
	public List<JwtResponse> findAllUsers() {
		return userService.findAllUsers();
	}

}
