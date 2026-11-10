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
import java.util.Map;

/**
 * Serializes a {@link Parameter} in a stable key order and inlines its extensions.
 *
 * @author smart-doc
 */
public class ParameterJsonAdapter implements JsonSerializer<Parameter> {

	/**
	 * Serializes a {@link Parameter} keeping a stable key order and inlining extensions.
	 * @param parameter parameter to serialize
	 * @param type declared type of the value
	 * @param context serialization context
	 * @return json object
	 */
	@Override
	public JsonElement serialize(Parameter parameter, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (parameter.getName() != null) {
			json.addProperty("name", parameter.getName());
		}
		if (parameter.getIn() != null) {
			json.addProperty("in", parameter.getIn());
		}
		if (parameter.getDescription() != null) {
			json.addProperty("description", parameter.getDescription());
		}
		if (parameter.getRequired() != null) {
			json.addProperty("required", parameter.getRequired());
		}
		if (parameter.getExample() != null) {
			json.add("example", context.serialize(parameter.getExample()));
		}
		if (parameter.getSchema() != null) {
			json.add("schema", context.serialize(parameter.getSchema()));
		}
		if (parameter.getType() != null) {
			json.addProperty("type", parameter.getType());
		}
		if (parameter.getFormat() != null) {
			json.addProperty("format", parameter.getFormat());
		}
		if (parameter.getEnumValues() != null && !parameter.getEnumValues().isEmpty()) {
			json.add("enum", context.serialize(parameter.getEnumValues()));
		}
		if (parameter.getItems() != null) {
			json.add("items", context.serialize(parameter.getItems()));
		}
		Map<String, Object> extensions = parameter.getExtensions();
		if (extensions != null) {
			extensions.forEach((key, value) -> json.add("x-" + key, context.serialize(value)));
		}
		return json;
	}

}
