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
 * Media type object of OpenAPI, the value of a {@code content} map.
 *
 * @author smart-doc
 */
public class OpenApiMediaType implements Serializable {

	private static final long serialVersionUID = -2695899837049594891L;

	/**
	 * Schema of the payload, a {@link Schema} or a legacy map supplied by the config.
	 */
	private Object schema;

	/**
	 * Form parameter name, only used by Swagger 2.
	 */
	private String name;

	/**
	 * Examples keyed by name.
	 */
	private Map<String, Example> examples;

	/**
	 * Returns the schema.
	 * @return schema
	 */
	public Object getSchema() {
		return schema;
	}

	/**
	 * Sets the schema.
	 * @param schema schema
	 * @return this openApiMediaType
	 */
	public OpenApiMediaType setSchema(Object schema) {
		this.schema = schema;
		return this;
	}

	/**
	 * Returns the name.
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * @param name name
	 * @return this openApiMediaType
	 */
	public OpenApiMediaType setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Returns the examples.
	 * @return examples
	 */
	public Map<String, Example> getExamples() {
		return examples;
	}

	/**
	 * Sets the examples.
	 * @param examples examples
	 * @return this openApiMediaType
	 */
	public OpenApiMediaType setExamples(Map<String, Example> examples) {
		this.examples = examples;
		return this;
	}

}
