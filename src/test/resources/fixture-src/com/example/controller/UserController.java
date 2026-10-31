package com.example.controller;

import com.example.model.Result;
import com.example.model.UserDto;

/**
 * User api
 *
 * @author fixture
 * @since 1.0.0
 */
@org.springframework.web.bind.annotation.RestController
@org.springframework.web.bind.annotation.RequestMapping("/user")
public class UserController {

	/**
	 * Get user by id
	 * @param id user id
	 * @return user detail
	 */
	@org.springframework.web.bind.annotation.GetMapping("/{id}")
	public Result<UserDto> getUser(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
		return null;
	}

	/**
	 * Create user
	 * @param user user info
	 * @return created user
	 */
	@org.springframework.web.bind.annotation.PostMapping
	public Result<UserDto> create(@org.springframework.web.bind.annotation.RequestBody UserDto user) {
		return null;
	}

	/**
	 * List users
	 * @param size page size
	 * @return user list
	 */
	@org.springframework.web.bind.annotation.GetMapping("/list")
	public Result<java.util.List<UserDto>> list(
			@org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") Integer size) {
		return null;
	}

}
