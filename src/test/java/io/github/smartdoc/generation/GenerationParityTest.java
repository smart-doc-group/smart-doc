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

import io.github.smartdoc.builder.AdocDocBuilder;
import io.github.smartdoc.builder.ApiDocBuilder;
import io.github.smartdoc.builder.HtmlApiDocBuilder;
import io.github.smartdoc.builder.JMeterBuilder;
import io.github.smartdoc.builder.openapi.OpenApiBuilder;
import io.github.smartdoc.constants.DocLanguage;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.RevisionLog;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that the aggregate generator produces the same files as the legacy
 * single-format builders.
 *
 * @author smart-doc
 */
public class GenerationParityTest {

	private static final long FIXED_NOW = 1719792000000L;

	private static final Path FIXTURE = Paths.get("src/test/resources/fixture-src").toAbsolutePath();

	private static final List<RenderFormat> FORMATS = Arrays.asList(RenderFormat.MARKDOWN, RenderFormat.ADOC,
			RenderFormat.HTML, RenderFormat.JMETER, RenderFormat.OPENAPI);

	@Test
	public void aggregateOutputMatchesLegacyBuilders(@TempDir Path tempDir) throws Exception {
		Path legacyDir = tempDir.resolve("legacy");
		Path aggregateDir = tempDir.resolve("aggregate");

		generateLegacy(legacyDir);
		generateAggregate(aggregateDir);

		assertSameOutput(legacyDir, aggregateDir);
	}

	@Test
	public void reproducibleOutputIsStable(@TempDir Path tempDir) throws Exception {
		List<RenderFormat> formats = Arrays.asList(RenderFormat.MARKDOWN, RenderFormat.ADOC, RenderFormat.HTML,
				RenderFormat.WORD, RenderFormat.JMETER, RenderFormat.OPENAPI, RenderFormat.POSTMAN);
		Path first = tempDir.resolve("first");
		Path second = tempDir.resolve("second");

		generateReproducible(first, formats);
		generateReproducible(second, formats);

		assertSameOutput(first, second);
	}

	private void generateReproducible(Path outDir, List<RenderFormat> formats) {
		ApiConfig config = newConfig(outDir);
		config.setReproducible(true);
		// random examples must be disabled by the reproducible mode
		config.setRandomMock(true);
		// exercise the generated file name suffix as well
		config.setCoverOld(false);
		new DocGenerator(config, newProjectBuilder()).generate(formats);
	}

	@Test
	public void aggregateSharesParsesForIdenticalFlags(@TempDir Path tempDir) {
		ApiConfig config = newConfig(tempDir.resolve("shared"));
		DocGenerator generator = new DocGenerator(config, newProjectBuilder());
		generator.generate(Arrays.asList(RenderFormat.MARKDOWN, RenderFormat.HTML, RenderFormat.WORD,
				RenderFormat.JMETER, RenderFormat.OPENAPI), FIXED_NOW);
		// markdown/html/word share one parse, jmeter and openapi need their own
		assertEquals(3, generator.getParsedKeys().size());
	}

	private void generateLegacy(Path outDir) throws Exception {
		for (RenderFormat format : FORMATS) {
			ApiConfig config = newConfig(outDir);
			JavaProjectBuilder javaProjectBuilder = newProjectBuilder();
			RunClock.beginRun(FIXED_NOW);
			try {
				renderLegacy(format, config, javaProjectBuilder);
			}
			finally {
				RunClock.endRun();
			}
		}
	}

	private void renderLegacy(RenderFormat format, ApiConfig config, JavaProjectBuilder javaProjectBuilder)
			throws Exception {
		switch (format) {
			case MARKDOWN:
				ApiDocBuilder.buildApiDoc(config, javaProjectBuilder);
				break;
			case ADOC:
				AdocDocBuilder.buildApiDoc(config, javaProjectBuilder);
				break;
			case HTML:
				HtmlApiDocBuilder.buildApiDoc(config, javaProjectBuilder);
				break;
			case JMETER:
				JMeterBuilder.buildApiDoc(config, javaProjectBuilder);
				break;
			case OPENAPI:
				OpenApiBuilder.buildOpenApi(config, javaProjectBuilder);
				break;
			default:
				throw new IllegalArgumentException("Unsupported format in parity test: " + format);
		}
	}

	private void generateAggregate(Path outDir) {
		ApiConfig config = newConfig(outDir);
		DocGenerator generator = new DocGenerator(config, newProjectBuilder());
		generator.generate(FORMATS, FIXED_NOW);
		assertEquals(4, generator.getParsedKeys().size());
	}

	private ApiConfig newConfig(Path outDir) {
		ApiConfig config = new ApiConfig();
		config.setProjectName("fixture");
		config.setServerUrl("http://127.0.0.1:8080");
		config.setOutPath(outDir.toAbsolutePath().toString());
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
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		javaProjectBuilder.setEncoding("UTF-8");
		javaProjectBuilder.addSourceTree(FIXTURE.toFile());
		return javaProjectBuilder;
	}

	private void assertSameOutput(Path expectedDir, Path actualDir) throws IOException {
		Map<String, byte[]> expected = readAll(expectedDir);
		Map<String, byte[]> actual = readAll(actualDir);
		assertEquals(expected.keySet(), actual.keySet(), "generated file names differ");
		for (String name : expected.keySet()) {
			assertArrayEquals(expected.get(name), actual.get(name), "generated file differs: " + name);
		}
	}

	private Map<String, byte[]> readAll(Path dir) throws IOException {
		Map<String, byte[]> files = new LinkedHashMap<>();
		try (Stream<Path> stream = Files.walk(dir)) {
			List<Path> paths = stream.filter(Files::isRegularFile)
				.sorted()
				.collect(Collectors.toCollection(ArrayList::new));
			for (Path path : paths) {
				files.put(dir.relativize(path).toString(), Files.readAllBytes(path));
			}
		}
		return files;
	}

}
