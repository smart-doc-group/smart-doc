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

import io.github.smartdoc.exception.UnsupportedGoalFrameworkException;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.model.grpc.GrpcApiDoc;
import io.github.smartdoc.template.IDocBuildTemplate;
import io.github.smartdoc.template.IWebSocketDocBuildTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that a third-party WebSocket template stays discoverable.
 * <p>
 * The WebSocket goals resolve through {@link IWebSocketDocBuildTemplate}, a sibling of
 * {@link IDocBuildTemplate}. A template that only implements the dedicated interface is
 * not registered in the {@code IDocBuildTemplate} service loader, so resolving it through
 * the regular factory fails with "framework not found". These tests lock the dedicated
 * lookup in place and keep the diagnostic for frameworks that really have no WebSocket
 * template.
 *
 * @author linwumingshi
 */
public class WebSocketTemplateDiscoveryTest {

	/**
	 * A template that only implements {@link IWebSocketDocBuildTemplate} must still be
	 * resolved by {@link TemplateResolver#resolveWebSocket}.
	 * @param tempDir temporary directory holding the isolated service file
	 */
	@Test
	void templateThatOnlyImplementsTheWebSocketInterfaceIsDiscovered(@TempDir Path tempDir) {
		ApiConfig config = new ApiConfig();
		config.setFramework(ThirdPartyWebSocketTemplate.SUPPORTED_FRAMEWORK);
		config.setClassLoader(isolatedClassLoader(tempDir, ThirdPartyWebSocketTemplate.class));

		IWebSocketDocBuildTemplate<?> template = TemplateResolver.resolveWebSocket(config);

		assertEquals(ThirdPartyWebSocketTemplate.class, template.getClass());
	}

	/**
	 * A framework without any WebSocket template keeps the actionable diagnostic,
	 * including the document type the framework would produce instead.
	 */
	@Test
	void frameworkWithoutWebSocketTemplateStillReportsTheDiagnostic() {
		ApiConfig config = new ApiConfig();
		config.setFramework("grpc");

		UnsupportedGoalFrameworkException error = assertThrows(UnsupportedGoalFrameworkException.class,
				() -> TemplateResolver.resolveWebSocket(config));

		assertEquals(GoalFamily.WEBSOCKET, error.getGoalFamily());
		assertEquals("grpc", error.getFramework());
		assertEquals(WebSocketDoc.class, error.getExpectedType());
		assertEquals(GrpcApiDoc.class, error.getActualType());
	}

	/**
	 * A framework that is not registered at all reports that no template exists, instead
	 * of naming a produced document type it cannot know.
	 */
	@Test
	void unregisteredFrameworkReportsThatNoTemplateExists() {
		ApiConfig config = new ApiConfig();
		config.setFramework("spirng");

		UnsupportedGoalFrameworkException error = assertThrows(UnsupportedGoalFrameworkException.class,
				() -> TemplateResolver.resolveWebSocket(config));

		assertEquals(GoalFamily.WEBSOCKET, error.getGoalFamily());
		assertEquals("spirng", error.getFramework());
		assertNull(error.getActualType());
		assertTrue(error.getMessage().contains("framework 'spirng' is not registered"), error.getMessage());
		assertTrue(error.getMessage().contains("because the framework is not registered and has no WebSocket template"),
				error.getMessage());
	}

	/**
	 * Builds a class loader that exposes only the given provider for
	 * {@link IWebSocketDocBuildTemplate}, so the built-in providers on the test classpath
	 * do not take part in the lookup.
	 * @param tempDir directory holding the generated service file
	 * @param provider class to register
	 * @return isolated class loader
	 */
	private static ClassLoader isolatedClassLoader(Path tempDir, Class<?> provider) {
		Path services = tempDir.resolve("META-INF/services");
		try {
			Files.createDirectories(services);
			Files.write(services.resolve(IWebSocketDocBuildTemplate.class.getName()),
					provider.getName().getBytes(StandardCharsets.UTF_8));
			URL url = tempDir.toUri().toURL();
			return new URLClassLoader(new URL[] { url }, WebSocketTemplateDiscoveryTest.class.getClassLoader()) {
				@Override
				public Enumeration<URL> getResources(String name) throws IOException {
					// Expose only the providers registered in the temporary directory, so
					// the built-in providers on the test classpath stay invisible.
					if (name.startsWith("META-INF/services/")) {
						return findResources(name);
					}
					return super.getResources(name);
				}
			};
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

}
