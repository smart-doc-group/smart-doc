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
 * OpenAPI schema object.
 * <p>
 * The custom serializer keeps the historical key order and inlines the {@code x-}
 * extensions into the schema object, which Gson cannot do for a map field.
 *
 * @author smart-doc
 */
@JsonAdapter(SchemaJsonAdapter.class)
public class Schema implements Serializable {

	private static final long serialVersionUID = 4635674230123176123L;

	/**
	 * Reference to a component schema, for example {@code #/components/schemas/UserDto}.
	 */
	private String ref;

	/**
	 * JSON Schema type.
	 */
	private String type;

	/**
	 * JSON Schema format.
	 */
	private String format;

	/**
	 * Field description.
	 */
	private String description;

	/**
	 * Names of the required properties, only used by object schemas.
	 */
	private List<String> required;

	/**
	 * Allowed values of an enum schema, serialized as {@code enum}.
	 */
	private List<?> enumValues;

	/**
	 * Example value.
	 */
	private Object example;

	/**
	 * Property schemas, keyed by property name.
	 */
	private Map<String, Schema> properties;

	/**
	 * Item schema of an array, or the allowed values when the array is an enum array.
	 */
	private Object items;

	/**
	 * {@code x-} extensions, inlined into the schema object by the serializer.
	 */
	private transient Map<String, Object> extensions;

	/**
	 * Creates an empty schema.
	 */
	public Schema() {
	}

	/**
	 * Creates a schema with the given type and format.
	 * @param type schema type
	 * @param format schema format
	 */
	public Schema(String type, String format) {
		this.type = type;
		this.format = format;
	}

	/**
	 * Creates a string schema.
	 * @return string schema
	 */
	public static Schema string() {
		return new Schema("string", "string");
	}

	/**
	 * Returns the component reference.
	 * @return component reference
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * Sets the component reference.
	 * @param ref component reference
	 * @return this schema
	 */
	public Schema setRef(String ref) {
		this.ref = ref;
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
	 * @return this schema
	 */
	public Schema setType(String type) {
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
	 * @return this schema
	 */
	public Schema setFormat(String format) {
		this.format = format;
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
	 * @return this schema
	 */
	public Schema setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Returns the required.
	 * @return required
	 */
	public List<String> getRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 * @param required required
	 * @return this schema
	 */
	public Schema setRequired(List<String> required) {
		this.required = required;
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
	 * @return this schema
	 */
	public Schema setEnumValues(List<?> enumValues) {
		this.enumValues = enumValues;
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
	 * @return this schema
	 */
	public Schema setExample(Object example) {
		this.example = example;
		return this;
	}

	/**
	 * Returns the properties.
	 * @return properties
	 */
	public Map<String, Schema> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 * @param properties properties
	 * @return this schema
	 */
	public Schema setProperties(Map<String, Schema> properties) {
		this.properties = properties;
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
	 * @return this schema
	 */
	public Schema setItems(Object items) {
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
	 * Sets the extensions.
	 * @param extensions extensions
	 * @return this schema
	 */
	public Schema setExtensions(Map<String, Object> extensions) {
		this.extensions = extensions;
		return this;
	}

	/**
	 * Adds one extension, the key is used without the {@code x-} prefix.
	 * @param key extension key
	 * @param value extension value
	 * @return this schema
	 */
	public Schema addExtension(String key, Object value) {
		if (this.extensions == null) {
			this.extensions = new LinkedHashMap<>();
		}
		this.extensions.put(key, value);
		return this;
	}

}
