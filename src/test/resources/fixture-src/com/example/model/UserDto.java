package com.example.model;

import java.util.List;

/**
 * User info
 *
 * @author fixture
 */
public class UserDto {

	/**
	 * user id
	 */
	@javax.validation.constraints.NotNull
	private Long id;

	/**
	 * user name
	 */
	@javax.validation.constraints.Size(min = 1, max = 32)
	private String name;

	/**
	 * user status
	 */
	private UserStatus status;

	/**
	 * user tags
	 */
	private List<String> tags;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
