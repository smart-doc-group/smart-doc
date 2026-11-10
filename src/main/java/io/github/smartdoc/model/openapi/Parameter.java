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
 * Parameter object of OpenAPI and Swagger.
 * <p>
 * OpenAPI 3 describes the type in a nested {@code schema}, Swagger 2 puts {@code type}
 * and {@code items} on the parameter itself. One model carries both shapes, null fields
 * are not serialized.
 *
 * @author smart-doc
 */
@JsonAdapter(ParameterJsonAdapter.class)
public class Parameter implements Serializable {

	private static final long serialVersionUID = -7316409146319460515L;

	/**
	 * Name of the parameter.
	 */
	private String name;

	/**
	 * Location of the parameter, for example query, path or header.
	 */
	private String in;

	/**
	 * Description of the parameter.
	 */
	private String description;

	/**
	 * Whether the parameter is required.
	 */
	private Boolean required;

	/**
	 * Example value of the parameter.
	 */
	private Object example;

	/**
	 * Schema of the parameter, only used by OpenAPI 3. A {@link Schema}, or a map
	 * supplied by the config for a Swagger body parameter.
	 */
	private Object schema;

	/**
	 * Type of the parameter, only used by Swagger 2.
	 */
	private String type;

	/**
	 * Format of the parameter, only used by OpenAPI 3 for array parameters.
	 */
	private String format;

	/**
	 * Allowed values, serialized as {@code enum}.
	 */
	private List<?> enumValues;

	/**
	 * Item definition of an array parameter, a {@link Parameter} or a map for Swagger 2.
	 */
	private Object items;

	/**
	 * {@code x-} extensions, inlined into the parameter object.
	 */
	private transient Map<String, Object> extensions;

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
	 * @return this parameter
	 */
	public Parameter setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Returns the location.
	 * @return location
	 */
	public String getIn() {
		return in;
	}

	/**
	 * Sets the location.
	 * @param in location
	 * @return this parameter
	 */
	public Parameter setIn(String in) {
		this.in = in;
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
	 * @return this parameter
	 */
	public Parameter setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Returns the required.
	 * @return required
	 */
	public Boolean getRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 * @param required required
	 * @return this parameter
	 */
	public Parameter setRequired(Boolean required) {
		this.required = required;
		return this;
	}

	/**
	 * Returns the example.
	 * @return example
	 */
	public Object getExample() {
		return example;
	}

	/**
	 * Sets the example.
	 * @param example example
	 * @return this parameter
	 */
	public Parameter setExample(Object example) {
		this.example = example;
		return this;
	}

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
	 * @return this parameter
	 */
	public Parameter setSchema(Object schema) {
		this.schema = schema;
		return this;
	}

	/**
	 * Returns the type.
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * @param type type
	 * @return this parameter
	 */
	public Parameter setType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * Returns the format.
	 * @return format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the format.
	 * @param format format
	 * @return this parameter
	 */
	public Parameter setFormat(String format) {
		this.format = format;
		return this;
	}

	/**
	 * Returns the enum values.
	 * @return enum values
	 */
	public List<?> getEnumValues() {
		return enumValues;
	}

	/**
	 * Sets the enum values.
	 * @param enumValues enum values
	 * @return this parameter
	 */
	public Parameter setEnumValues(List<?> enumValues) {
		this.enumValues = enumValues;
		return this;
	}

	/**
	 * Returns the items.
	 * @return items
	 */
	public Object getItems() {
		return items;
	}

	/**
	 * Sets the items.
	 * @param items items
	 * @return this parameter
	 */
	public Parameter setItems(Object items) {
		this.items = items;
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
	 * @return this parameter
	 */
	public Parameter addExtension(String key, Object value) {
		if (this.extensions == null) {
			this.extensions = new LinkedHashMap<>();
		}
		this.extensions.put(key, value);
		return this;
	}

}
