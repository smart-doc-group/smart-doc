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

import io.github.smartdoc.constants.FrameworkEnum;
import io.github.smartdoc.exception.UnsupportedGoalFrameworkException;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.grpc.GrpcApiDoc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the framework/goal capability check performed by {@link TemplateResolver}.
 * <p>
 * The acceptance matrix is derived from {@link GoalFamily#frameworks()} and
 * {@link FrameworkEnum}, so a framework added to the enum or a family that gains or loses
 * a framework is covered without editing this test.
 *
 * @author linwumingshi
 */
public class TemplateResolverTest {

	/**
	 * Frameworks declared by {@link FrameworkEnum}, the columns of the matrix.
	 */
	private static final List<String> FRAMEWORKS = Arrays.stream(FrameworkEnum.values())
		.map(FrameworkEnum::getFramework)
		.collect(Collectors.toList());

	/**
	 * Resolves every family that goes through {@link TemplateResolver#resolve} against
	 * every declared framework and expects the outcome declared by
	 * {@link GoalFamily#frameworks()}.
	 * <p>
	 * The WebSocket family is excluded on purpose: its goals resolve through
	 * {@link TemplateResolver#resolveWebSocket} and share one template across several
	 * scanning frameworks, so that family is covered by the dedicated cases below.
	 * @param family goal family under test
	 * @param framework configured framework name
	 */
	@ParameterizedTest(name = "{0} family + {1}")
	@MethodSource("familyAndFrameworkPairs")
	void resolveAcceptsExactlyTheDeclaredFrameworks(GoalFamily family, String framework) {
		ApiConfig config = configWithFramework(framework);
		List<String> declaredFrameworks = Arrays.asList(family.frameworks().split(", "));

		if (declaredFrameworks.contains(framework)) {
			DocContext<?> context = TemplateResolver.resolve(config, family);
			assertEquals(family.requiredDocType(), context.documentType());
			assertEquals(framework, context.framework());
			return;
		}
		UnsupportedGoalFrameworkException error = assertThrows(UnsupportedGoalFrameworkException.class,
				() -> TemplateResolver.resolve(config, family));
		assertEquals(family, error.getGoalFamily());
		assertEquals(framework, error.getFramework());
		assertEquals(family.requiredDocType(), error.getExpectedType());
		assertNotEquals(family.requiredDocType(), error.getActualType());
	}

	/**
	 * Builds the family/framework pairs consumed by
	 * {@link #resolveAcceptsExactlyTheDeclaredFrameworks}.
	 * @return every family that uses {@code resolve} combined with every framework
	 */
	static Stream<Arguments> familyAndFrameworkPairs() {
		return Stream.of(GoalFamily.REST, GoalFamily.RPC, GoalFamily.GRPC, GoalFamily.JAVADOC)
			.flatMap(family -> FRAMEWORKS.stream().map(framework -> Arguments.of(family, framework)));
	}

	@Test
	void grpcFamilyRejectsSpring() {
		ApiConfig config = configWithFramework("spring");

		UnsupportedGoalFrameworkException error = assertThrows(UnsupportedGoalFrameworkException.class,
				() -> TemplateResolver.resolve(config, GoalFamily.GRPC));

		assertEquals(GrpcApiDoc.class, error.getExpectedType());
		assertEquals(ApiDoc.class, error.getActualType());
	}

	@Test
	void familyDefaultsAreTheFirstDeclaredFramework() {
		assertEquals("spring", GoalFamily.REST.defaultFramework());
		assertEquals("dubbo", GoalFamily.RPC.defaultFramework());
		assertEquals("grpc", GoalFamily.GRPC.defaultFramework());
		assertEquals("javadoc", GoalFamily.JAVADOC.defaultFramework());
		assertEquals("spring", GoalFamily.WEBSOCKET.defaultFramework());
	}

	@Test
	void emptyFrameworkFallsBackToTheFamilyDefault() {
		assertEquals("spring", TemplateResolver.resolve(new ApiConfig(), GoalFamily.REST).framework());
		assertEquals("dubbo", TemplateResolver.resolve(new ApiConfig(), GoalFamily.RPC).framework());
		assertEquals("grpc", TemplateResolver.resolve(new ApiConfig(), GoalFamily.GRPC).framework());
		assertEquals("javadoc", TemplateResolver.resolve(new ApiConfig(), GoalFamily.JAVADOC).framework());
	}

	@Test
	void resolverDoesNotWriteTheDefaultBack() {
		ApiConfig config = new ApiConfig();

		TemplateResolver.resolve(config, GoalFamily.RPC);

		assertNull(config.getFramework(), "the resolved default must not be written back to the configuration");
	}

	/**
	 * Covers the WebSocket family, which resolves through
	 * {@link TemplateResolver#resolveWebSocket} instead of one template per framework.
	 * @param framework configured framework name
	 */
	@ParameterizedTest
	@EnumSource(FrameworkEnum.class)
	void webSocketFamilyAcceptsEveryFrameworkExceptGrpc(FrameworkEnum frameworkEnum) {
		String framework = frameworkEnum.getFramework();
		ApiConfig config = configWithFramework(framework);
		boolean declared = Arrays.asList(GoalFamily.WEBSOCKET.frameworks().split(", ")).contains(framework);

		if (declared) {
			assertNotNull(TemplateResolver.resolveWebSocket(config));
			return;
		}
		UnsupportedGoalFrameworkException error = assertThrows(UnsupportedGoalFrameworkException.class,
				() -> TemplateResolver.resolveWebSocket(config));
		assertEquals(GoalFamily.WEBSOCKET, error.getGoalFamily());
		assertEquals(framework, error.getFramework());
	}

	@Test
	void unsupportedCombinationMessageNamesBothFamilies() {
		ApiConfig config = configWithFramework("dubbo");

		UnsupportedGoalFrameworkException error = assertThrows(UnsupportedGoalFrameworkException.class,
				() -> TemplateResolver.resolve(config, GoalFamily.REST));

		String message = error.getMessage();
		assertTrue(message.contains("Unsupported framework/goal combination"), message);
		assertTrue(message.contains("ApiDoc"), message);
		assertTrue(message.contains("RpcApiDoc"), message);
		assertTrue(message.contains("rpc-markdown"), message);
	}

	private static ApiConfig configWithFramework(String framework) {
		ApiConfig config = new ApiConfig();
		config.setFramework(framework);
		return config;
	}

}
