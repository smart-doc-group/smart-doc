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

import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import io.github.smartdoc.template.IDocBuildTemplate;
import io.github.smartdoc.template.IWebSocketDocBuildTemplate;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * WebSocket build template that only implements the dedicated WebSocket interface.
 * <p>
 * A third-party integration declares its provider against
 * {@link IWebSocketDocBuildTemplate} without implementing {@link IDocBuildTemplate},
 * which is the extension point that existed before the goal family refactoring. The class
 * is deliberately not registered in {@code META-INF/services}: it is only visible to the
 * test that builds an isolated class loader for it, so the built-in providers are not
 * affected.
 *
 * @author linwumingshi
 */
public class ThirdPartyWebSocketTemplate implements IWebSocketDocBuildTemplate<WebSocketDoc> {

	/**
	 * Framework served by this template. gRPC has no built-in WebSocket template, so the
	 * class never shadows one of the built-in providers.
	 */
	static final String SUPPORTED_FRAMEWORK = "grpc";

	@Override
	public boolean supportsFramework(String framework) {
		return SUPPORTED_FRAMEWORK.equalsIgnoreCase(framework);
	}

	@Override
	public FrameworkAnnotations registeredAnnotations() {
		return null;
	}

	@Override
	public boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
		return false;
	}

	@Override
	public List<WebSocketDoc> renderWebSocketApi(ProjectDocConfigBuilder projectBuilder,
			Collection<JavaClass> candidateClasses) {
		return Collections.emptyList();
	}

}
