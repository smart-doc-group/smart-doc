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

/**
 * Response object of Swagger 2.
 * <p>
 * Swagger 2 keeps the schema on the response itself, OpenAPI 3 wraps it into a content
 * map, so the two specifications need different response models.
 *
 * @author smart-doc
 */
public class SwaggerResponse implements ResponseBody {

	private static final long serialVersionUID = -760052762240710629L;

	/**
	 * Description of the response.
	 */
	private String description;

	/**
	 * Schema of the response payload.
	 */
	private Object schema;

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
	 * @return this swaggerResponse
	 */
	public SwaggerResponse setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Returns the schema of the response payload.
	 * @return schema of the response payload
	 */
	public Object getSchema() {
		return schema;
	}

	/**
	 * Sets the schema of the response payload.
	 * @param schema schema of the response payload
	 * @return this swaggerResponse
	 */
	public SwaggerResponse setSchema(Object schema) {
		this.schema = schema;
		return this;
	}

}
