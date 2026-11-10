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

import io.github.smartdoc.utils.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the serialized form of {@link Operation} and {@link Parameter}.
 *
 * @author smart-doc
 */
public class OperationJsonAdapterTest {

	@Test
	public void inlinesOperationExtensions() {
		Operation operation = new Operation().setSummary("create")
			.setDeprecated(Boolean.FALSE)
			.addExtension("order", 2);
		JsonObject json = toJson(operation);
		assertEquals("create", json.get("summary").getAsString());
		assertFalse(json.get("deprecated").getAsBoolean());
		assertEquals(2, json.get("x-order").getAsInt());
		assertFalse(json.has("extensions"));
		assertFalse(json.has("requestBody"));
	}

	@Test
	public void serializesOpenApiParameterSchema() {
		Parameter parameter = new Parameter().setName("id")
			.setIn("path")
			.setRequired(Boolean.TRUE)
			.setSchema(new Schema().setType("number").setFormat("int64"));
		JsonObject json = toJson(parameter);
		assertTrue(json.getAsJsonObject("schema").has("type"));
		assertFalse(json.has("type"));
		assertTrue(json.get("required").getAsBoolean());
	}

	@Test
	public void serializesSwaggerParameterType() {
		Parameter parameter = new Parameter().setName("size").setIn("query").setType("integer");
		JsonObject json = toJson(parameter);
		assertEquals("integer", json.get("type").getAsString());
		assertFalse(json.has("schema"));
		assertFalse(json.has("required"));
	}

	@Test
	public void serializesPathItemOnlyWithPresentMethods() {
		PathItem pathItem = new PathItem().setOperation("get", new Operation().setSummary("list"));
		JsonObject json = JsonParser.parseString(JsonUtil.toPrettyJson(pathItem)).getAsJsonObject();
		assertTrue(json.has("get"));
		assertFalse(json.has("post"));
		assertEquals(Collections.singleton("get"), json.keySet());
	}

	private JsonObject toJson(Object value) {
		return JsonParser.parseString(JsonUtil.toPrettyJson(value)).getAsJsonObject();
	}

}
