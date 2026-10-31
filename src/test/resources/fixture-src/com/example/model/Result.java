package com.example.model;

/**
 * Common result
 *
 * @param <T> data type
 * @author fixture
 */
public class Result<T> {

	/**
	 * is successful
	 */
	private boolean success;

	/**
	 * error code
	 */
	private String code;

	/**
	 * response data
	 */
	private T data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
