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
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the serialized form of the OpenAPI root document.
 *
 * @author smart-doc
 */
public class OpenApiDocumentTest {

	@Test
	public void serializesRootDocument() {
		Map<String, Schema> schemas = new LinkedHashMap<>();
		schemas.put("User", new Schema().setType("object"));
		OpenApi document = new OpenApi().setInfo(new Info().setTitle("demo").setVersion("v1.0.0"))
			.setServers(Collections.singletonList(new Server().setUrl("http://127.0.0.1:8080")))
			.setTags(Collections.emptySet())
			.setPaths(Collections.singletonMap("/user",
					new PathItem().setOperation("get", new Operation().setSummary("get user"))))
			.setComponents(new Components().setSchemas(schemas));
		JsonObject json = toJson(document);
		assertEquals("3.1.0", json.get("openapi").getAsString());
		assertEquals("demo", json.getAsJsonObject("info").get("title").getAsString());
		assertEquals(1, json.getAsJsonArray("servers").size());
		assertTrue(json.has("paths"));
		assertTrue(json.getAsJsonObject("components").getAsJsonObject("schemas").has("User"));
	}

	@Test
	public void serializesResponseContent() {
		Map<String, OpenApiMediaType> content = new LinkedHashMap<>();
		content.put("application/json",
				new OpenApiMediaType().setSchema(new Schema().setRef("#/components/schemas/User")));
		Response response = new Response().setDescription("OK").setContent(content);
		JsonObject json = toJson(response);
		assertEquals("OK", json.get("description").getAsString());
		assertTrue(json.getAsJsonObject("content")
			.getAsJsonObject("application/json")
			.getAsJsonObject("schema")
			.has("$ref"));
	}

	@Test
	public void omitsAbsentServerDescription() {
		JsonObject json = toJson(new Server().setUrl("http://127.0.0.1:8080"));
		assertEquals("http://127.0.0.1:8080", json.get("url").getAsString());
		assertFalse(json.has("description"));
	}

	private JsonObject toJson(Object value) {
		return JsonParser.parseString(JsonUtil.toPrettyJson(value)).getAsJsonObject();
	}

}
