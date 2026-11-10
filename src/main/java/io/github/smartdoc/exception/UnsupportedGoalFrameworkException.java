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
package io.github.smartdoc.exception;

import io.github.smartdoc.generation.GoalFamily;

/**
 * Thrown when the configured framework cannot produce the document type required by the
 * selected goal family.
 * <p>
 * The exception is raised before any source is scanned, so a wrong
 * {@code framework}/{@code goal} pair fails fast with an actionable message instead of a
 * {@link ClassCastException}, an empty document or an empty shell.
 *
 * @author linwumingshi
 */
public class UnsupportedGoalFrameworkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Goal family that could not be served.
	 */
	private final GoalFamily goalFamily;

	/**
	 * Framework resolved for the run, including the family default.
	 */
	private final String framework;

	/**
	 * Document type required by the goal family.
	 */
	private final Class<?> expectedType;

	/**
	 * Document type actually produced by the resolved template, {@code null} when no
	 * template is registered for the framework.
	 */
	private final Class<?> actualType;

	/**
	 * Creates the exception and renders the diagnostic message.
	 * @param goalFamily goal family being generated
	 * @param framework framework resolved for the run
	 * @param expectedType document type required by the goal family
	 * @param actualType document type produced by the resolved template, {@code null}
	 * when no template is registered for the framework
	 */
	public UnsupportedGoalFrameworkException(GoalFamily goalFamily, String framework, Class<?> expectedType,
			Class<?> actualType) {
		super(buildMessage(goalFamily, framework, expectedType, actualType));
		this.goalFamily = goalFamily;
		this.framework = framework;
		this.expectedType = expectedType;
		this.actualType = actualType;
	}

	/**
	 * Returns the goal family that could not be served.
	 * @return goal family
	 */
	public GoalFamily getGoalFamily() {
		return goalFamily;
	}

	/**
	 * Returns the framework resolved for the run.
	 * @return framework name
	 */
	public String getFramework() {
		return framework;
	}

	/**
	 * Returns the document type required by the goal family.
	 * @return required document type
	 */
	public Class<?> getExpectedType() {
		return expectedType;
	}

	/**
	 * Returns the document type actually produced by the resolved template.
	 * @return produced document type, {@code null} when no template is registered for the
	 * framework
	 */
	public Class<?> getActualType() {
		return actualType;
	}

	/**
	 * Builds the diagnostic message.
	 * <p>
	 * The message names both the required and the produced document type, the frameworks
	 * that can serve the selected goal family, and the goals of the family the resolved
	 * template actually belongs to, so that the user can fix the configuration without
	 * reading the code. When no template is registered for the framework at all, the
	 * message says so instead of naming a produced type.
	 * @param goalFamily goal family being generated
	 * @param framework framework resolved for the run
	 * @param expectedType document type required by the goal family
	 * @param actualType document type produced by the resolved template, {@code null}
	 * when no template is registered for the framework
	 * @return diagnostic message
	 */
	private static String buildMessage(GoalFamily goalFamily, String framework, Class<?> expectedType,
			Class<?> actualType) {
		GoalFamily actualFamily = actualType == null ? null : GoalFamily.ofDocumentType(actualType);
		StringBuilder message = new StringBuilder(512);
		message.append("Unsupported framework/goal combination: a ")
			.append(goalFamily.displayName())
			.append(" goal requires documents of type ")
			.append(expectedType.getName())
			.append(", but ");
		if (actualType == null) {
			message.append("framework '").append(framework).append("' is not registered.");
		}
		else {
			message.append("framework '")
				.append(framework)
				.append("' resolves to ")
				.append(actualType.getName())
				.append('.');
		}
		if (goalFamily == GoalFamily.WEBSOCKET) {
			message.append("\n\n");
			if (actualType == null) {
				message.append("The WebSocket goals cannot be generated for framework '")
					.append(framework)
					.append("', because the framework is not registered and has no WebSocket template.");
			}
			else {
				message.append("No WebSocket build template is registered for framework '")
					.append(framework)
					.append("', so the WebSocket goals cannot be generated for this project.");
			}
		}
		else if (actualFamily != null) {
			message.append("\n\nThe resolved template belongs to the ")
				.append(actualFamily.displayName())
				.append(" family and produces ")
				.append(actualFamily.displayName())
				.append(" documents, which carry no ")
				.append(goalFamily == GoalFamily.REST ? "URL path, HTTP method or content type"
						: "document structure required by this goal")
				.append('.');
		}
		message.append("\n\n")
			.append(goalFamily.displayName())
			.append(" goals are: ")
			.append(goalFamily.goals())
			.append('.');
		if (actualFamily != null && actualFamily != goalFamily) {
			message.append("\n")
				.append(actualFamily.displayName())
				.append(" goals are: ")
				.append(actualFamily.goals())
				.append('.');
		}
		if (goalFamily != GoalFamily.WEBSOCKET) {
			message.append("\n\nTo generate ")
				.append(goalFamily.displayName())
				.append(" documents, set \"framework\" to one of [")
				.append(goalFamily.frameworks())
				.append("] and scan the classes that expose the corresponding API.");
			if (actualFamily != null && actualFamily != goalFamily) {
				message.append(" To document this project as it is, run a goal from the ")
					.append(actualFamily.displayName())
					.append(" family instead.");
			}
		}
		return message.toString();
	}

}
