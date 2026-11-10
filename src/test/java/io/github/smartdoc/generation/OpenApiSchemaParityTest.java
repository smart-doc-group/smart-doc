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
package io.github.smartdoc.generation;

import io.github.smartdoc.constants.DocLanguage;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.RevisionLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that the schema model produces the same OpenAPI document as the legacy map
 * based implementation.
 * <p>
 * The comparison is canonical: object keys are sorted before comparing, because the
 * legacy implementation used unordered maps while the model serializes fields in
 * declaration order.
 *
 * @author smart-doc
 */
public class OpenApiSchemaParityTest {

	private static final Path FIXTURE = Paths.get("src/test/resources/fixture-src").toAbsolutePath();

	private static final Path OPENAPI_BASELINE = Paths.get("src/test/resources/baseline/openapi-baseline.json")
		.toAbsolutePath();

	private static final Path SWAGGER_BASELINE = Paths.get("src/test/resources/baseline/swagger-baseline.json")
		.toAbsolutePath();

	private static final long FIXED_NOW = 1719792000000L;

	@Test
	public void openApiJsonMatchesLegacyBaseline(@TempDir Path outDir) throws IOException {
		new DocGenerator(newConfig(outDir), newProjectBuilder())
			.generate(Collections.singletonList(RenderFormat.OPENAPI), FIXED_NOW);
		JsonElement expected = readCanonical(OPENAPI_BASELINE);
		JsonElement actual = readCanonical(outDir.resolve("openapi.json"));
		assertEquals(expected, actual);
	}

	@Test
	public void swaggerJsonMatchesLegacyBaseline(@TempDir Path outDir) throws IOException {
		new DocGenerator(newConfig(outDir), newProjectBuilder())
			.generate(Collections.singletonList(RenderFormat.SWAGGER), FIXED_NOW);
		JsonElement expected = readCanonical(SWAGGER_BASELINE);
		JsonElement actual = readCanonical(outDir.resolve("openapi.json"));
		assertEquals(expected, actual);
	}

	private ApiConfig newConfig(Path outDir) {
		ApiConfig config = new ApiConfig();
		config.setProjectName("fixture");
		config.setServerUrl("http://127.0.0.1:8080");
		config.setOutPath(outDir.toString());
		config.setBaseDir(FIXTURE.toString());
		config.setCodePath(FIXTURE.toString());
		config.setFramework("spring");
		config.setAllInOne(true);
		config.setCoverOld(true);
		config.setRandomMock(false);
		config.setCreateDebugPage(false);
		config.setLanguage(DocLanguage.ENGLISH);
		config.setRevisionLogs(Collections.singletonList(RevisionLog.builder()
			.setRevisionTime("2024-01-01 00:00:00")
			.setAuthor("@fixture")
			.setVersion("v1.0.0")
			.setRemarks("fixture")
			.setStatus("auto")));
		return config;
	}

	private JavaProjectBuilder newProjectBuilder() {
		JavaProjectBuilder builder = JavaProjectBuilderHelper.create();
		builder.setEncoding("UTF-8");
		builder.addSourceTree(FIXTURE.toFile());
		return builder;
	}

	private JsonElement readCanonical(Path path) throws IOException {
		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			return canonical(JsonParser.parseReader(reader));
		}
	}

	/**
	 * Sorts object keys recursively so that the comparison is insensitive to the key
	 * order.
	 * @param element json element
	 * @return canonical element
	 */
	private JsonElement canonical(JsonElement element) {
		if (element.isJsonObject()) {
			Map<String, JsonElement> sorted = new TreeMap<>();
			element.getAsJsonObject()
				.entrySet()
				.forEach(entry -> sorted.put(entry.getKey(), canonical(entry.getValue())));
			JsonObject result = new JsonObject();
			sorted.forEach(result::add);
			return result;
		}
		if (element.isJsonArray()) {
			com.google.gson.JsonArray result = new com.google.gson.JsonArray();
			element.getAsJsonArray().forEach(item -> result.add(canonical(item)));
			return result;
		}
		return element;
	}

}
