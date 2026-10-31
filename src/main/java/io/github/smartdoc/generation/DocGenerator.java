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
import io.github.smartdoc.builder.DocBuilderTemplate;
import io.github.smartdoc.builder.HtmlApiDocBuilder;
import io.github.smartdoc.builder.JMeterBuilder;
import io.github.smartdoc.builder.PostmanJsonBuilder;
import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.builder.WordDocBuilder;
import io.github.smartdoc.builder.openapi.OpenApiBuilder;
import io.github.smartdoc.builder.openapi.SwaggerBuilder;
import io.github.smartdoc.factory.BuildTemplateFactory;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.template.IDocBuildTemplate;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates several documentation formats from one parse per distinct {@link ParseKey}.
 * <p>
 * Formats that have the same parse flags reuse one parse result, which avoids scanning
 * and resolving the same sources once per output format. Each renderer receives a forked
 * schema so that ordering and grouping changes made by one renderer do not leak into
 * another.
 *
 * @author smart-doc
 */
public class DocGenerator {

	private final ApiConfig config;

	private final JavaProjectBuilder javaProjectBuilder;

	private final boolean baseAdoc;

	private final boolean baseParamsDataToTree;

	private final Boolean baseShowJavaType;

	private final String baseServerUrl;

	private final Map<ParseKey, ParseSnapshot> snapshots = new LinkedHashMap<>();

	/**
	 * Creates a generator.
	 * @param config api config
	 * @param javaProjectBuilder qdox project builder
	 */
	public DocGenerator(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		this.config = config;
		this.javaProjectBuilder = javaProjectBuilder;
		this.baseAdoc = config.isAdoc();
		this.baseParamsDataToTree = config.isParamsDataToTree();
		this.baseShowJavaType = config.getShowJavaType();
		this.baseServerUrl = config.getServerUrl();
	}

	/**
	 * Generates the given formats using the current time.
	 * @param formats output formats
	 */
	public void generate(Collection<RenderFormat> formats) {
		generate(formats, RunClock.isRunActive() ? RunClock.now() : System.currentTimeMillis());
	}

	/**
	 * Generates the given formats with a fixed timestamp so that the output is
	 * reproducible.
	 * @param formats output formats
	 * @param now timestamp in milliseconds
	 */
	public void generate(Collection<RenderFormat> formats, long now) {
		snapshots.clear();
		RunClock.beginRun(now);
		try {
			Map<ParseKey, List<RenderFormat>> groups = groupByParseKey(formats);
			for (Map.Entry<ParseKey, List<RenderFormat>> entry : groups.entrySet()) {
				ParseKey key = entry.getKey();
				key.applyTo(config);
				ParseSnapshot snapshot = parse(key);
				for (RenderFormat format : entry.getValue()) {
					render(format, snapshot.fork());
				}
			}
		}
		finally {
			restoreBaseFlags();
			ApiConfig.clearInstance();
			RunClock.endRun();
		}
	}

	/**
	 * Generates the formats named in a comma separated list.
	 * @param config api config
	 * @param javaProjectBuilder qdox project builder
	 * @param formats comma separated format names
	 */
	public static void generate(ApiConfig config, JavaProjectBuilder javaProjectBuilder, String formats) {
		new DocGenerator(config, javaProjectBuilder).generate(RenderFormat.parseFormats(formats));
	}

	private Map<ParseKey, List<RenderFormat>> groupByParseKey(Collection<RenderFormat> formats) {
		Map<ParseKey, List<RenderFormat>> groups = new LinkedHashMap<>();
		for (RenderFormat format : formats) {
			ParseKey key = format.keyOf(config);
			groups.computeIfAbsent(key, k -> new ArrayList<>()).add(format);
		}
		return groups;
	}

	private ParseSnapshot parse(ParseKey key) {
		ParseSnapshot cached = snapshots.get(key);
		if (cached != null) {
			return cached;
		}
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.TRUE);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework(),
				config.getClassLoader());
		ParseSnapshot snapshot = new ParseSnapshot(key, docBuildTemplate.getApiData(configBuilder));
		snapshots.put(key, snapshot);
		return snapshot;
	}

	private void render(RenderFormat format, ApiSchema<ApiDoc> schema) {
		switch (format) {
			case MARKDOWN:
				ApiDocBuilder.render(schema, config, javaProjectBuilder);
				break;
			case ADOC:
				AdocDocBuilder.render(schema, config, javaProjectBuilder);
				break;
			case HTML:
				HtmlApiDocBuilder.render(schema, config, javaProjectBuilder);
				break;
			case WORD:
				WordDocBuilder.render(schema, config, javaProjectBuilder);
				break;
			case JMETER:
				JMeterBuilder.render(schema, config, javaProjectBuilder);
				break;
			case POSTMAN:
				PostmanJsonBuilder.render(schema, config, javaProjectBuilder);
				break;
			case OPENAPI:
				OpenApiBuilder.render(config, schema);
				break;
			case SWAGGER:
				SwaggerBuilder.render(config, schema);
				break;
			default:
				throw new IllegalArgumentException("Unsupported doc format: " + format);
		}
	}

	private void restoreBaseFlags() {
		config.setAdoc(baseAdoc);
		config.setParamsDataToTree(baseParamsDataToTree);
		config.setShowJavaType(baseShowJavaType);
		config.setServerUrl(baseServerUrl);
	}

	/**
	 * Returns formats whose parse result is already cached. Intended for diagnostics.
	 * @return cached parse keys
	 */
	public Set<ParseKey> getParsedKeys() {
		return snapshots.keySet();
	}

}
