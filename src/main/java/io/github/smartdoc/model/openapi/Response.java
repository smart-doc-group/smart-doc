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

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Response object of OpenAPI 3.
 *
 * @author smart-doc
 */
public class Response implements ResponseBody {

	private static final long serialVersionUID = -760052762240710630L;

	/**
	 * Reference to a reusable response, for example {@code #/components/responses/Error}.
	 */
	@SerializedName("$ref")
	private String ref;

	/**
	 * Description of the response.
	 */
	private String description;

	/**
	 * Media types of the response payload.
	 */
	private Map<String, OpenApiMediaType> content;

	/**
	 * Returns the reference of the response.
	 * @return reference of the response
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * Sets the reference of the response.
	 * @param ref reference of the response
	 * @return this response
	 */
	public Response setRef(String ref) {
		this.ref = ref;
		return this;
	}

	/**
	 * Returns the description of the response.
	 * @return description of the response
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the response.
	 * @param description description of the response
	 * @return this response
	 */
	public Response setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Returns the media types of the payload.
	 * @return media types keyed by media type
	 */
	public Map<String, OpenApiMediaType> getContent() {
		return content;
	}

	/**
	 * Sets the media types of the payload.
	 * @param content media types keyed by media type
	 * @return this response
	 */
	public Response setContent(Map<String, OpenApiMediaType> content) {
		this.content = content;
		return this;
	}

}
