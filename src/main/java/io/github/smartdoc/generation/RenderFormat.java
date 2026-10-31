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

import io.github.smartdoc.model.ApiConfig;
import com.power.common.util.StringUtil;

import java.util.Locale;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Output formats supported by the aggregate generator.
 * <p>
 * Each format declares the parse flags its legacy builder applies, so that formats with
 * the same flags can share one parse.
 *
 * @author smart-doc
 */
public enum RenderFormat {

	/**
	 * Markdown.
	 */
	MARKDOWN(Boolean.FALSE, false, null, false),

	/**
	 * Asciidoc.
	 */
	ADOC(Boolean.TRUE, false, null, false),

	/**
	 * Html.
	 */
	HTML(null, false, null, false),

	/**
	 * Word.
	 */
	WORD(null, false, null, false),

	/**
	 * OpenApi 3.
	 */
	OPENAPI(null, true, Boolean.FALSE, false),

	/**
	 * Swagger 2.
	 */
	SWAGGER(null, true, Boolean.FALSE, false),

	/**
	 * JMeter script.
	 */
	JMETER(Boolean.FALSE, false, Boolean.TRUE, false),

	/**
	 * Postman collection.
	 */
	POSTMAN(null, false, Boolean.TRUE, true);

	private final Boolean adoc;

	private final boolean paramsDataToTree;

	private final Boolean showJavaType;

	private final boolean useServerEnv;

	RenderFormat(Boolean adoc, boolean paramsDataToTree, Boolean showJavaType, boolean useServerEnv) {
		this.adoc = adoc;
		this.paramsDataToTree = paramsDataToTree;
		this.showJavaType = showJavaType;
		this.useServerEnv = useServerEnv;
	}

	/**
	 * Computes the effective parse key for this format.
	 * @param base config with the user supplied values, not the values mutated by a
	 * previous format
	 * @return parse key
	 */
	public ParseKey keyOf(ApiConfig base) {
		boolean effectiveAdoc = adoc != null ? adoc : base.isAdoc();
		boolean effectiveShowJavaType = showJavaType != null ? showJavaType
				: Boolean.TRUE.equals(base.getShowJavaType());
		String serverUrl = base.getServerUrl();
		if (useServerEnv && StringUtil.isNotEmpty(base.getServerEnv())) {
			serverUrl = base.getServerEnv();
		}
		return new ParseKey(effectiveAdoc, paramsDataToTree, effectiveShowJavaType, serverUrl);
	}

	/**
	 * Parses a comma separated format list.
	 * @param formats format names, for example {@code markdown,html,adoc}
	 * @return formats in the given order
	 */
	public static Set<RenderFormat> parseFormats(String formats) {
		Set<RenderFormat> result = new LinkedHashSet<>();
		if (StringUtil.isEmpty(formats)) {
			result.add(MARKDOWN);
			return result;
		}
		for (String format : formats.split(",")) {
			String name = format.trim().toLowerCase(Locale.ROOT);
			if (name.isEmpty()) {
				continue;
			}
			result.add(parseFormat(name));
		}
		if (result.isEmpty()) {
			result.add(MARKDOWN);
		}
		return result;
	}

	private static RenderFormat parseFormat(String name) {
		switch (name) {
			case "md":
			case "markdown":
				return MARKDOWN;
			case "adoc":
			case "asciidoc":
				return ADOC;
			case "html":
				return HTML;
			case "word":
			case "docx":
				return WORD;
			case "openapi":
			case "openapi3":
				return OPENAPI;
			case "swagger":
			case "openapi2":
				return SWAGGER;
			case "jmeter":
				return JMETER;
			case "postman":
				return POSTMAN;
			default:
				throw new IllegalArgumentException("Unsupported doc format: " + name + ", supported formats are "
						+ "markdown, adoc, html, word, openapi, swagger, jmeter, postman.");
		}
	}

}
