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
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.IDoc;
import io.github.smartdoc.template.IDocBuildTemplate;

import java.util.Objects;

/**
 * Immutable result of resolving a goal family against the configured framework.
 * <p>
 * Instances are created by {@link TemplateResolver} only, and never write back to
 * {@link io.github.smartdoc.model.ApiConfig}. The resolved framework and template are
 * therefore stable for the whole goal run, no matter how many builders or formats the run
 * executes.
 *
 * @param <T> document type produced by the resolved template
 * @author linwumingshi
 */
public final class DocContext<T extends IDoc> {

	/**
	 * Goal family this context was resolved for.
	 */
	private final GoalFamily goalFamily;

	/**
	 * Framework resolved for the run, including the family default when the configuration
	 * leaves it empty.
	 */
	private final String framework;

	/**
	 * Document type produced by the resolved template.
	 */
	private final Class<T> documentType;

	/**
	 * Build template selected for the goal family.
	 */
	private final IDocBuildTemplate<T> template;

	/**
	 * Creates a context. Use {@link #of(GoalFamily, String, Class, IDocBuildTemplate)} so
	 * that the arguments are validated.
	 * @param goalFamily goal family being generated
	 * @param framework framework resolved for the run
	 * @param documentType document type produced by the template
	 * @param template resolved build template
	 */
	private DocContext(GoalFamily goalFamily, String framework, Class<T> documentType, IDocBuildTemplate<T> template) {
		this.goalFamily = goalFamily;
		this.framework = framework;
		this.documentType = documentType;
		this.template = template;
	}

	/**
	 * Creates a context. Intended for {@link TemplateResolver} and tests.
	 * @param goalFamily goal family being generated
	 * @param framework framework resolved for the run
	 * @param documentType document type produced by the template
	 * @param template resolved build template
	 * @param <T> document type
	 * @return immutable context
	 */
	static <T extends IDoc> DocContext<T> of(GoalFamily goalFamily, String framework, Class<T> documentType,
			IDocBuildTemplate<T> template) {
		return new DocContext<>(Objects.requireNonNull(goalFamily), Objects.requireNonNull(framework),
				Objects.requireNonNull(documentType), Objects.requireNonNull(template));
	}

	/**
	 * Parses the project with the resolved template.
	 * @param projectBuilder project builder
	 * @return parsed schema
	 */
	public ApiSchema<T> parse(ProjectDocConfigBuilder projectBuilder) {
		return template.getApiData(projectBuilder);
	}

	/**
	 * Returns the goal family being generated.
	 * @return goal family
	 */
	public GoalFamily goalFamily() {
		return goalFamily;
	}

	/**
	 * Returns the framework resolved for this run, including the family default when the
	 * configuration leaves it empty.
	 * @return resolved framework name
	 */
	public String framework() {
		return framework;
	}

	/**
	 * Returns the document type produced by the resolved template.
	 * @return document type
	 */
	public Class<T> documentType() {
		return documentType;
	}

	/**
	 * Returns the resolved build template.
	 * @return build template
	 */
	public IDocBuildTemplate<T> template() {
		return template;
	}

}
