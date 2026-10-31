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

package io.github.smartdoc.builder;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.generation.DocGenerator;
import io.github.smartdoc.generation.RenderFormat;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiSchema;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.Collections;
import java.util.List;

/**
 * used to generate jmx file for Jmeter
 *
 * @author Lansg
 */
public class JMeterBuilder {

	/**
	 * Jmeter script extension
	 */
	private static final String JMETER_SCRIPT_EXTENSION = ".jmx";

	/**
	 * private constructor
	 */
	private JMeterBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @param config ApiConfig
	 */
	public static void buildApiDoc(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildApiDoc(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param config ApiConfig
	 * @param javaProjectBuilder ProjectDocConfigBuilder
	 */
	public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		new DocGenerator(config, javaProjectBuilder).generate(Collections.singletonList(RenderFormat.JMETER));
	}

	/**
	 * Renders a JMeter script from an already parsed schema.
	 * @param schema parsed api schema
	 * @param config ApiConfig
	 * @param javaProjectBuilder qdox project builder
	 */
	public static void render(ApiSchema<ApiDoc> schema, ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		List<ApiDoc> apiDocList = schema.getApiDatas();
		String version = config.isCoverOld() ? "" : "-V"
				+ DateTimeUtil.long2Str(System.currentTimeMillis(), DocGlobalConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
		String docName;
		if (StringUtil.isNotEmpty(config.getProjectName())) {
			docName = config.getProjectName() + version + JMETER_SCRIPT_EXTENSION;
		}
		else {
			docName = "jmeter-script" + version + JMETER_SCRIPT_EXTENSION;
		}
		builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, DocGlobalConstants.JMETER_TPL, docName);
	}

}
