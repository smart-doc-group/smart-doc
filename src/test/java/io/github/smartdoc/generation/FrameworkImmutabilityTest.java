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

import io.github.smartdoc.builder.DocBuilderTemplate;
import io.github.smartdoc.builder.grpc.GrpcDocBuilderTemplate;
import io.github.smartdoc.builder.javadoc.JavadocDocBuilderTemplate;
import io.github.smartdoc.builder.rpc.RpcDocBuilderTemplate;
import io.github.smartdoc.builder.websocket.WebSocketDocBuilderTemplate;
import io.github.smartdoc.exception.UnsupportedGoalFrameworkException;
import io.github.smartdoc.model.ApiConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies that resolving a goal family never rewrites
 * {@link io.github.smartdoc.model.ApiConfig}.
 * <p>
 * Before the resolver existed, several builders wrote a default framework back into the
 * shared configuration. A {@code javadoc-*} goal could therefore turn a {@code spring}
 * configuration into {@code javadoc}, making a later REST format silently produce an
 * empty document. These tests lock the read-only contract in place.
 *
 * @author linwumingshi
 */
public class FrameworkImmutabilityTest {

	@Test
	void oneConfigCanDriveSeveralFamiliesInSequence() {
		ApiConfig config = new ApiConfig();

		assertEquals("spring", TemplateResolver.resolve(config, GoalFamily.REST).framework());
		assertEquals("dubbo", TemplateResolver.resolve(config, GoalFamily.RPC).framework());
		assertEquals("grpc", TemplateResolver.resolve(config, GoalFamily.GRPC).framework());
		assertEquals("javadoc", TemplateResolver.resolve(config, GoalFamily.JAVADOC).framework());

		assertNull(config.getFramework(), "resolving a goal family must not change the configuration");
	}

	@Test
	void aFailedResolutionKeepsTheConfiguredFramework() {
		ApiConfig config = configWithFramework("spring");

		assertThrows(UnsupportedGoalFrameworkException.class, () -> TemplateResolver.resolve(config, GoalFamily.RPC));

		assertEquals("spring", config.getFramework());
	}

	@Test
	void checkAndInitNoLongerDefaultsTheFramework() {
		ApiConfig config = new ApiConfig();

		new DocBuilderTemplate().checkAndInitForGetApiData(config);

		assertNull(config.getFramework());
	}

	@Test
	void rpcCheckAndInitNoLongerDefaultsToDubbo() {
		ApiConfig config = configWithOutPath("target/doc");

		new RpcDocBuilderTemplate().checkAndInit(config, false);

		assertNull(config.getFramework());
	}

	@Test
	void grpcCheckAndInitNoLongerDefaultsToGrpc() {
		ApiConfig config = configWithOutPath("target/doc");

		new GrpcDocBuilderTemplate().checkAndInit(config, false);

		assertNull(config.getFramework());
	}

	@Test
	void javadocCheckAndInitNoLongerOverridesTheFramework() {
		ApiConfig config = configWithFramework("dubbo");
		config.setOutPath("target/doc");

		new JavadocDocBuilderTemplate().checkAndInit(config, false);

		assertEquals("dubbo", config.getFramework());
	}

	private static ApiConfig configWithFramework(String framework) {
		ApiConfig config = new ApiConfig();
		config.setFramework(framework);
		return config;
	}

	private static ApiConfig configWithOutPath(String outPath) {
		ApiConfig config = new ApiConfig();
		config.setOutPath(outPath);
		return config;
	}

	@Test
	void buildersDeclareTheGoalFamilyTheyServe() {
		assertEquals(GoalFamily.REST, new DocBuilderTemplate().goalFamily());
		assertEquals(GoalFamily.RPC, new RpcDocBuilderTemplate().goalFamily());
		assertEquals(GoalFamily.GRPC, new GrpcDocBuilderTemplate().goalFamily());
		assertEquals(GoalFamily.JAVADOC, new JavadocDocBuilderTemplate().goalFamily());
		assertEquals(GoalFamily.WEBSOCKET, new WebSocketDocBuilderTemplate().goalFamily());
	}

}
