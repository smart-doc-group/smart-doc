/*
 * Copyright (C) 2018-2026 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.smartdoc.model.openapi;

import com.google.gson.annotations.JsonAdapter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Operation object of OpenAPI, the value of a path item method.
 *
 * @author smart-doc
 */
@JsonAdapter(OperationJsonAdapter.class)
public class Operation implements Serializable {

	private static final long serialVersionUID = 1940853041532488696L;

	/**
	 * Summary of the operation.
	 */
	private String summary;

	/**
	 * Description of the operation.
	 */
	private String description;

	/**
	 * Tags of the operation.
	 */
	private List<String> tags;

	/**
	 * Media types the operation consumes, only used by Swagger 2.
	 */
	private List<String> consumes;

	/**
	 * Request body of OpenAPI 3, not used by Swagger 2.
	 */
	private RequestBody requestBody;

	/**
	 * Parameters of the operation.
	 */
	private List<Parameter> parameters;

	/**
	 * Responses keyed by http status code.
	 */
	private Map<String, ResponseBody> responses;

	/**
	 * Whether the operation is deprecated.
	 */
	private Boolean deprecated;

	/**
	 * Unique identifier of the operation.
	 */
	private String operationId;

	/**
	 * {@code x-} extensions, inlined into the operation object.
	 */
	private transient Map<String, Object> extensions;

	/**
	 * Returns the summary.
	 * @return summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Sets the summary.
	 * @param summary summary
	 * @return this operation
	 */
	public Operation setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	/**
	 * Returns the description.
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * @param description description
	 * @return this operation
	 */
	public Operation setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Returns the tags.
	 * @return tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * Sets the tags.
	 * @param tags tags
	 * @return this operation
	 */
	public Operation setTags(List<String> tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * Returns the consumes.
	 * @return consumes
	 */
	public List<String> getConsumes() {
		return consumes;
	}

	/**
	 * Sets the consumes.
	 * @param consumes consumes
	 * @return this operation
	 */
	public Operation setConsumes(List<String> consumes) {
		this.consumes = consumes;
		return this;
	}

	/**
	 * Returns the request body.
	 * @return request body
	 */
	public RequestBody getRequestBody() {
		return requestBody;
	}

	/**
	 * Sets the request body.
	 * @param requestBody request body
	 * @return this operation
	 */
	public Operation setRequestBody(RequestBody requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	/**
	 * Returns the parameters.
	 * @return parameters
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 * @param parameters parameters
	 * @return this operation
	 */
	public Operation setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
		return this;
	}

	/**
	 * Returns the responses.
	 * @return responses
	 */
	public Map<String, ResponseBody> getResponses() {
		return responses;
	}

	/**
	 * Sets the responses.
	 * @param responses responses
	 * @return this operation
	 */
	public Operation setResponses(Map<String, ResponseBody> responses) {
		this.responses = responses;
		return this;
	}

	/**
	 * Returns the deprecated.
	 * @return deprecated
	 */
	public Boolean getDeprecated() {
		return deprecated;
	}

	/**
	 * Sets the deprecated.
	 * @param deprecated deprecated
	 * @return this operation
	 */
	public Operation setDeprecated(Boolean deprecated) {
		this.deprecated = deprecated;
		return this;
	}

	/**
	 * Returns the operation id.
	 * @return operation id
	 */
	public String getOperationId() {
		return operationId;
	}

	/**
	 * Sets the operation id.
	 * @param operationId operation id
	 * @return this operation
	 */
	public Operation setOperationId(String operationId) {
		this.operationId = operationId;
		return this;
	}

	/**
	 * Returns the extensions.
	 * @return extensions
	 */
	public Map<String, Object> getExtensions() {
		return extensions;
	}

	/**
	 * Adds one extension, the key is used without the {@code x-} prefix.
	 * @param key extension key
	 * @param value extension value
	 * @return this operation
	 */
	public Operation addExtension(String key, Object value) {
		if (this.extensions == null) {
			this.extensions = new LinkedHashMap<>();
		}
		this.extensions.put(key, value);
		return this;
	}

}
