package com.example.controller;

import com.example.model.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.model.UserDto;

/**
 * User api
 *
 * @author fixture
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

	/**
	 * Get user by id
	 * @param id user id
	 * @return user detail
	 */
	@GetMapping("/{id}")
	public Result<UserDto> getUser(@PathVariable("id") Long id) {
		return null;
	}

	/**
	 * Create user
	 * @param user user info
	 * @return created user
	 */
	@PostMapping
	public Result<UserDto> create(@RequestBody UserDto user) {
		return null;
	}

	/**
	 * List users
	 * @param size page size
	 * @return user list
	 */
	@GetMapping("/list")
	public Result<java.util.List<UserDto>> list(
			@RequestParam(defaultValue = "10") Integer size) {
		return null;
	}

}
