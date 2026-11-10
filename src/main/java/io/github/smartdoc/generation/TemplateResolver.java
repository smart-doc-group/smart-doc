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
import io.github.smartdoc.factory.BuildTemplateFactory;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.IDoc;
import io.github.smartdoc.template.IDocBuildTemplate;
import io.github.smartdoc.template.IWebSocketDocBuildTemplate;
import com.power.common.util.StringUtil;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Resolves the build template for a goal family.
 * <p>
 * This is the single place where the framework default is applied and where the produced
 * document type is compared with the type required by the goal family. Configuration is
 * read only: the resolved framework is kept in the returned {@link DocContext} instead of
 * being written back to {@link ApiConfig}, so one goal can no longer change the meaning
 * of a later one.
 *
 * @author linwumingshi
 */
public final class TemplateResolver {

	/**
	 * Logger used to report templates that do not declare their document type.
	 */
	private static final Logger LOGGER = Logger.getLogger(TemplateResolver.class.getName());

	/**
	 * Private constructor, this is a utility class.
	 */
	private TemplateResolver() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Resolves the template that produces the document type required by the goal family.
	 * @param config api config, never modified
	 * @param goalFamily goal family being generated
	 * @param <T> document type of the goal family
	 * @return immutable context holding the resolved framework and template
	 * @throws UnsupportedGoalFrameworkException when the framework produces another
	 * document type
	 */
	public static <T extends IDoc> DocContext<T> resolve(ApiConfig config, GoalFamily goalFamily) {
		Objects.requireNonNull(config, "ApiConfig can't be null");
		Objects.requireNonNull(goalFamily, "GoalFamily can't be null");
		String framework = effectiveFramework(config, goalFamily);
		IDocBuildTemplate<T> template = BuildTemplateFactory.getDocBuildTemplate(framework, config.getClassLoader());
		Class<?> actualType = template.documentType();
		verify(goalFamily, framework, actualType);
		@SuppressWarnings("unchecked")
		Class<T> documentType = (Class<T>) actualType;
		return DocContext.of(goalFamily, framework, documentType, template);
	}

	/**
	 * Resolves the WebSocket template for the configured framework.
	 * <p>
	 * The WebSocket goals use a dedicated service interface, so the template is looked up
	 * through {@link IWebSocketDocBuildTemplate} rather than {@link IDocBuildTemplate}. A
	 * third-party template that only implements the dedicated interface is therefore
	 * still discovered, which is the extension point that existed before the goal family
	 * refactoring. The framework is resolved with the same rules as the other families,
	 * so a framework without a WebSocket implementation is reported with the same
	 * diagnostic.
	 * @param config api config, never modified
	 * @return websocket build template
	 * @throws UnsupportedGoalFrameworkException when the framework has no WebSocket
	 * template
	 */
	public static IWebSocketDocBuildTemplate<?> resolveWebSocket(ApiConfig config) {
		Objects.requireNonNull(config, "ApiConfig can't be null");
		String framework = effectiveFramework(config, GoalFamily.WEBSOCKET);
		try {
			return BuildTemplateFactory.getWebSocketDocBuildTemplate(framework, config.getClassLoader());
		}
		catch (RuntimeException ex) {
			Class<?> actualType = producedDocumentType(framework, config.getClassLoader());
			throw new UnsupportedGoalFrameworkException(GoalFamily.WEBSOCKET, framework,
					GoalFamily.WEBSOCKET.requiredDocType(), actualType);
		}
	}

	/**
	 * Returns the document type produced by the regular template of the given framework.
	 * <p>
	 * Only used to build the diagnostic of a WebSocket goal that has no WebSocket
	 * template, so the message can name the type the framework would produce instead.
	 * @param framework framework resolved for the run
	 * @param classLoader class loader used to look up the template
	 * @return produced document type, {@code null} when no regular template is registered
	 * for the framework
	 */
	private static Class<?> producedDocumentType(String framework, ClassLoader classLoader) {
		try {
			return BuildTemplateFactory.getDocBuildTemplate(framework, classLoader).documentType();
		}
		catch (RuntimeException ex) {
			return null;
		}
	}

	/**
	 * Verifies that the resolved template can produce the document type required by the
	 * goal family.
	 * <p>
	 * A template that returns {@link IDoc} does not declare its type, which happens for
	 * third-party templates built before the contract existed. Such a template is
	 * accepted with a warning so that custom templates keep working.
	 * @param goalFamily goal family being generated
	 * @param framework framework resolved for the run
	 * @param actualType document type declared by the resolved template
	 * @throws UnsupportedGoalFrameworkException when the declared type is not compatible
	 * with the goal family
	 */
	private static void verify(GoalFamily goalFamily, String framework, Class<?> actualType) {
		if (actualType == IDoc.class) {
			LOGGER.warning(() -> "The build template for framework '" + framework
					+ "' does not declare its document type, the " + goalFamily.displayName()
					+ " goal family contract is not verified.");
			return;
		}
		if (!goalFamily.requiredDocType().isAssignableFrom(actualType)) {
			throw new UnsupportedGoalFrameworkException(goalFamily, framework, goalFamily.requiredDocType(),
					actualType);
		}
	}

	/**
	 * Returns the framework configured by the user, falling back to the family default
	 * when the configuration leaves it empty. The configuration is never modified.
	 * @param config api config
	 * @param goalFamily goal family being generated
	 * @return framework name to resolve the template for
	 */
	private static String effectiveFramework(ApiConfig config, GoalFamily goalFamily) {
		String framework = config.getFramework();
		return StringUtil.isEmpty(framework) ? goalFamily.defaultFramework() : framework;
	}

}
