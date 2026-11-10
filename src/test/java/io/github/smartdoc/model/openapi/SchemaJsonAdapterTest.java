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

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the serialized form of {@link Schema}.
 *
 * @author smart-doc
 */
public class SchemaJsonAdapterTest {

	@Test
	public void serializesReference() {
		JsonObject json = toJson(new Schema().setRef("#/components/schemas/User"));
		assertEquals("#/components/schemas/User", json.get("$ref").getAsString());
		assertFalse(json.has("ref"));
	}

	@Test
	public void serializesEnumAndArray() {
		Schema schema = new Schema().setType("array")
			.setEnumValues(Arrays.asList("A", "B"))
			.setItems(new Schema().setType("string"));
		JsonObject json = toJson(schema);
		assertEquals(2, json.getAsJsonArray("enum").size());
		assertEquals("string", json.getAsJsonObject("items").get("type").getAsString());
	}

	@Test
	public void inlinesExtensions() {
		Schema schema = new Schema().setType("string").addExtension("order", 1);
		JsonObject json = toJson(schema);
		assertEquals(1, json.get("x-order").getAsInt());
		assertFalse(json.has("extensions"));
	}

	@Test
	public void omitsEmptyCollections() {
		JsonObject json = toJson(new Schema().setType("string")
			.setRequired(Collections.emptyList())
			.setProperties(Collections.emptyMap()));
		assertFalse(json.has("required"));
		assertFalse(json.has("properties"));
		assertTrue(json.has("type"));
	}

	private JsonObject toJson(Schema schema) {
		return JsonParser.parseString(JsonUtil.toPrettyJson(schema)).getAsJsonObject();
	}

}
