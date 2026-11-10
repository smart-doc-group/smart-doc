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

import java.io.Serializable;
import java.util.Map;

/**
 * Request body object of OpenAPI.
 *
 * @author smart-doc
 */
public class RequestBody implements Serializable {

	private static final long serialVersionUID = -8082140993525462816L;

	/**
	 * Description of the request body.
	 */
	private String description;

	/**
	 * Media types of the request body.
	 */
	private Map<String, OpenApiMediaType> content;

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
	 * @return this requestBody
	 */
	public RequestBody setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Returns the content.
	 * @return content
	 */
	public Map<String, OpenApiMediaType> getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 * @param content content
	 * @return this requestBody
	 */
	public RequestBody setContent(Map<String, OpenApiMediaType> content) {
		this.content = content;
		return this;
	}

}
