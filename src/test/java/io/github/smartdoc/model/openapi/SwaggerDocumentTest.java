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
 * Verifies the serialized form of the Swagger root document and response.
 *
 * @author smart-doc
 */
public class SwaggerDocumentTest {

	@Test
	public void serializesRootDocument() {
		Map<String, Schema> definitions = new LinkedHashMap<>();
		definitions.put("User", new Schema().setType("object"));
		Swagger document = new Swagger().setInfo(new Info().setTitle("demo").setVersion("1.0.0"))
			.setHost("127.0.0.1:8080")
			.setBasePath("/api")
			.setTags(Collections.emptySet())
			.setPaths(Collections.singletonMap("/user",
					new PathItem().setOperation("get", new Operation().setSummary("get user"))))
			.setDefinitions(definitions);
		JsonObject json = JsonParser.parseString(JsonUtil.toPrettyJson(document)).getAsJsonObject();
		assertEquals("2.0", json.get("swagger").getAsString());
		assertEquals("127.0.0.1:8080", json.get("host").getAsString());
		assertEquals("/api", json.get("basePath").getAsString());
		assertTrue(json.getAsJsonObject("definitions").has("User"));
	}

	@Test
	public void omitsAbsentSchema() {
		JsonObject json = JsonParser.parseString(JsonUtil.toPrettyJson(new SwaggerResponse().setDescription("OK")))
			.getAsJsonObject();
		assertEquals("OK", json.get("description").getAsString());
		assertFalse(json.has("schema"));
	}

	@Test
	public void keepsSchemaOnResponse() {
		JsonObject json = JsonParser
			.parseString(JsonUtil.toPrettyJson(
					new SwaggerResponse().setDescription("OK").setSchema(new Schema().setRef("#/definitions/User"))))
			.getAsJsonObject();
		assertEquals("#/definitions/User", json.getAsJsonObject("schema").get("$ref").getAsString());
	}

}
