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
 * Serializes an {@link Operation} in a stable key order and inlines its extensions.
 *
 * @author smart-doc
 */
public class OperationJsonAdapter implements JsonSerializer<Operation> {

	/**
	 * Serializes a {@link Operation} keeping a stable key order and inlining extensions.
	 * @param operation operation to serialize
	 * @param type declared type of the value
	 * @param context serialization context
	 * @return json object
	 */
	@Override
	public JsonElement serialize(Operation operation, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (operation.getSummary() != null) {
			json.addProperty("summary", operation.getSummary());
		}
		if (operation.getDescription() != null) {
			json.addProperty("description", operation.getDescription());
		}
		if (operation.getTags() != null) {
			json.add("tags", context.serialize(operation.getTags()));
		}
		if (operation.getConsumes() != null) {
			json.add("consumes", context.serialize(operation.getConsumes()));
		}
		if (operation.getRequestBody() != null) {
			json.add("requestBody", context.serialize(operation.getRequestBody()));
		}
		if (operation.getParameters() != null) {
			json.add("parameters", context.serialize(operation.getParameters()));
		}
		if (operation.getResponses() != null) {
			json.add("responses", context.serialize(operation.getResponses()));
		}
		if (operation.getDeprecated() != null) {
			json.addProperty("deprecated", operation.getDeprecated());
		}
		if (operation.getOperationId() != null) {
			json.addProperty("operationId", operation.getOperationId());
		}
		Map<String, Object> extensions = operation.getExtensions();
		if (extensions != null) {
			extensions.forEach((key, value) -> json.add("x-" + key, context.serialize(value)));
		}
		return json;
	}

}
