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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Serializes a {@link Schema} keeping the historical key order and inlining the
 * {@code x-} extensions into the schema object.
 *
 * @author smart-doc
 */
public class SchemaJsonAdapter implements JsonSerializer<Schema> {

	/**
	 * Serializes a {@link Schema} keeping a stable key order and inlining extensions.
	 * @param schema schema to serialize
	 * @param type declared type of the value
	 * @param context serialization context
	 * @return json object
	 */
	@Override
	public JsonElement serialize(Schema schema, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		addString(json, "$ref", schema.getRef());
		addString(json, "type", schema.getType());
		addString(json, "format", schema.getFormat());
		addString(json, "description", schema.getDescription());
		addList(json, "required", schema.getRequired(), context);
		addList(json, "enum", schema.getEnumValues(), context);
		if (schema.getExample() != null) {
			json.add("example", context.serialize(schema.getExample()));
		}
		Map<String, Schema> properties = schema.getProperties();
		if (properties != null && !properties.isEmpty()) {
			json.add("properties", context.serialize(properties));
		}
		if (schema.getItems() != null) {
			json.add("items", context.serialize(schema.getItems()));
		}
		Map<String, Object> extensions = schema.getExtensions();
		if (extensions != null) {
			extensions.forEach((key, value) -> json.add("x-" + key, context.serialize(value)));
		}
		return json;
	}

	private void addString(JsonObject json, String name, String value) {
		if (value != null) {
			json.addProperty(name, value);
		}
	}

	private void addList(JsonObject json, String name, List<?> values, JsonSerializationContext context) {
		if (values != null && !values.isEmpty()) {
			json.add(name, context.serialize(values));
		}
	}

}
