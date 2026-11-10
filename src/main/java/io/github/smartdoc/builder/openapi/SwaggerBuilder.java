/*
 *
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
package io.github.smartdoc.builder.openapi;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.MediaType;
import io.github.smartdoc.constants.OperationIdStrategyEnum;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.exception.UnsupportedGoalFrameworkException;
import io.github.smartdoc.generation.DocGenerator;
import io.github.smartdoc.generation.RenderFormat;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiExceptionStatus;
import io.github.smartdoc.model.ApiGroup;
import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.openapi.OpenApiMediaType;
import io.github.smartdoc.model.openapi.Operation;
import io.github.smartdoc.model.openapi.Parameter;
import io.github.smartdoc.model.openapi.Info;
import io.github.smartdoc.model.openapi.OpenApiTag;
import io.github.smartdoc.model.openapi.Swagger;
import io.github.smartdoc.model.openapi.SwaggerResponse;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JsonUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Swagger Builder
 *
 * @author xingzi Date 2022/9/17 15:16
 * @since 2.6.2
 */
public class SwaggerBuilder extends AbstractOpenApiBuilder {

	/**
	 * Singleton
	 */
	private static final SwaggerBuilder INSTANCE = new SwaggerBuilder();

	/**
	 * OperationId OrderNo Map
	 */
	private static final Map<String, Integer> OPERATIONID_ORDER_NO_MAP = new HashMap<>();

	/**
	 * private constructor
	 */
	private SwaggerBuilder() {
	}

	/**
	 * For unit testing
	 * @param config Configuration of smart-doc
	 */
	public static void buildOpenApi(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildOpenApi(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param config Configuration of smart-doc
	 * @param projectBuilder JavaDocBuilder of QDox
	 * @throws UnsupportedGoalFrameworkException when the configured framework cannot
	 * produce {@link ApiDoc}
	 */
	public static void buildOpenApi(ApiConfig config, JavaProjectBuilder projectBuilder) {
		new DocGenerator(config, projectBuilder).generate(Collections.singletonList(RenderFormat.SWAGGER));
	}

	/**
	 * Renders a Swagger 2 document from an already parsed schema.
	 * @param config Configuration of smart-doc
	 * @param apiSchema parsed api schema
	 */
	public static void render(ApiConfig config, ApiSchema<ApiDoc> apiSchema) {
		INSTANCE.openApiCreate(config, apiSchema);
	}

	@Override
	public String getModuleName() {
		return DocGlobalConstants.OPENAPI_2_COMPONENT_KRY;
	}

	/**
	 * Build OpenApi
	 * @param config Configuration of smart-doc
	 */
	@Override
	public void openApiCreate(ApiConfig config, ApiSchema<ApiDoc> apiSchema) {
		OPERATIONID_ORDER_NO_MAP.clear();
		this.setComponentKey(getModuleName());
		Swagger json = new Swagger().setInfo(buildInfo(config))
			.setHost(config.getServerUrl() == null ? "127.0.0.1" : config.getServerUrl())
			.setBasePath(StringUtils.isNotBlank(config.getPathPrefix()) ? config.getPathPrefix()
					: DocGlobalConstants.PATH_DELIMITER);
		Set<OpenApiTag> tags = new HashSet<>();
		json.setTags(tags);
		json.setPaths(buildPaths(config, apiSchema, tags, true));
		json.setDefinitions(this.buildComponentData(apiSchema));

		String filePath = config.getOutPath();
		filePath = filePath + DocGlobalConstants.OPEN_API_JSON;
		String data = JsonUtil.toPrettyJson(json);
		FileUtil.nioWriteFile(data, filePath);
	}

	/**
	 * Build openapi info
	 * @param apiConfig Configuration of smart-doc
	 */
	private static Info buildInfo(ApiConfig apiConfig) {
		return new Info()
			.setTitle(apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName())
			.setVersion("1.0.0");
	}

	/**
	 * Build Servers
	 * @param config Configuration of smart-doc
	 */
	@Deprecated
	private static List<Map<String, String>> buildTags(ApiConfig config) {
		List<Map<String, String>> tagList = new ArrayList<>();
		Map<String, String> tagMap;
		List<ApiGroup> groups = config.getGroups();
		for (ApiGroup group : groups) {
			tagMap = new HashMap<>(4);
			tagMap.put("name", group.getName());
			tagMap.put("description", group.getApis());
			tagList.add(tagMap);
		}
		return tagList;
	}

	/**
	 * Build the operation of a method.
	 * @param apiConfig Configuration of smart-doc
	 * @param apiMethodDoc Data of method
	 * @param apiDoc single api doc
	 * @param apiExceptionStatuses Exception status list
	 * @return operation of the method
	 */
	@Override
	public Operation buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc,
			List<ApiExceptionStatus> apiExceptionStatuses) {
		Operation operation = new Operation();
		operation.setSummary(apiMethodDoc.getDesc());
		operation.setDescription(apiMethodDoc.getDetail());
		operation.setTags(Stream.of(apiDoc.getName(), apiDoc.getDesc(), apiMethodDoc.getGroup())
			.filter(StringUtil::isNotEmpty)
			.collect(Collectors.toList()));
		List<Parameter> parameters = this.buildParameters(apiMethodDoc);
		// requestBody
		if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
			Parameter parameter = new Parameter().setIn("body");
			OpenApiMediaType mediaType = this.buildContentBody(apiConfig, apiMethodDoc, false);
			parameter.setSchema(mediaType.getSchema());
			parameter.setName(mediaType.getName());
			parameters.add(parameter);
		}
		if (this.hasFile(parameters)) {
			operation.setConsumes(Collections.singletonList(MediaType.MULTIPART_FORM_DATA));
		}
		operation.setParameters(parameters);
		operation.setResponses(this.buildResponses(apiConfig, apiMethodDoc, apiExceptionStatuses));
		operation.setDeprecated(apiMethodDoc.isDeprecated());
		OperationIdStrategyEnum operationIdStrategy = apiConfig.getOperationIdStrategy();
		if (Objects.isNull(operationIdStrategy)) {
			// Keep the historical Swagger operationId when no strategy is configured.
			operation.setOperationId(
					apiMethodDoc.getName() + "_" + apiMethodDoc.getMethodId() + "UsingOn" + apiMethodDoc.getType());
		}
		else {
			operation
				.setOperationId(this.buildOperationId(operationIdStrategy, apiMethodDoc, OPERATIONID_ORDER_NO_MAP));
		}
		return operation;
	}

	/**
	 * Check if the parameter contains a file
	 * @param parameters list of parameters
	 * @return true if it contains a file
	 */
	private boolean hasFile(List<Parameter> parameters) {
		for (Parameter param : parameters) {
			if (DocGlobalConstants.SWAGGER_FILE_TAG.equals(param.getIn())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Build the response body of a method.
	 * @param apiConfig Configuration of smart-doc
	 * @param apiMethodDoc Data of method
	 * @return response body
	 */
	@Override
	public SwaggerResponse buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
		SwaggerResponse responseBody = new SwaggerResponse().setDescription("OK");
		if (CollectionUtil.isNotEmpty(apiMethodDoc.getResponseParams())
				|| Objects.nonNull(apiMethodDoc.getReturnSchema())) {
			OpenApiMediaType mediaType = this.buildContentBody(apiConfig, apiMethodDoc, true);
			responseBody.setSchema(mediaType.getSchema());
		}
		return responseBody;
	}

	/**
	 * Build the request parameters of a method.
	 * @param apiMethodDoc API data for the method
	 * @return parameter list
	 */
	@Override
	public List<Parameter> buildParameters(ApiMethodDoc apiMethodDoc) {
		List<Parameter> parametersList = new ArrayList<>();
		// Handling path parameters
		for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
			Parameter parameter = this.getStringParams(apiParam, false);
			parameter.setType(DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType()));
			parameter.setIn("path");
			parametersList.add(parameter);
		}
		for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
			if (apiParam.getType().equals(ParamTypeConstants.PARAM_TYPE_ARRAY) || apiParam.isHasItems()) {
				Parameter parameter = this.getStringParams(apiParam, false);
				parameter.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
				parameter.setItems(this.getStringParams(apiParam, true));
				parametersList.add(parameter);
			}
			else {
				Parameter parameter = this.getStringParams(apiParam, false);
				parameter.setType(DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType()));
				parametersList.add(parameter);
			}
		}
		// with headers
		if (!CollectionUtil.isEmpty(apiMethodDoc.getRequestHeaders())) {
			for (ApiReqParam header : apiMethodDoc.getRequestHeaders()) {
				Parameter parameter = new Parameter().setName(header.getName())
					.setType(DocUtil.javaTypeToOpenApiTypeConvert(header.getType()))
					.setDescription(header.getDesc())
					.setRequired(header.isRequired())
					.setExample(getExampleValueBasedOnTypeForApiReqParam(header))
					.setSchema(buildParametersSchema(header))
					.setIn("header");
				parametersList.add(parameter);
			}
		}
		return parametersList;
	}

	/**
	 * Build one request parameter.
	 * @param apiParam ApiParam
	 * @param hasItems whether the parameter is the item definition of an array
	 * @return parameter of the api param
	 */
	@Override
	public Parameter getStringParams(ApiParam apiParam, boolean hasItems) {
		Parameter parameter = new Parameter();
		if (!hasItems) {
			if ("file".equalsIgnoreCase(apiParam.getType())) {
				parameter.setIn(DocGlobalConstants.SWAGGER_FILE_TAG);
			}
			else {
				parameter.setIn("query");
			}
			parameter.setName(apiParam.getField());
			parameter.setDescription(apiParam.getDesc());
			parameter.setRequired(apiParam.isRequired());
			parameter.setType(apiParam.getType());
		}
		else {
			if (ParamTypeConstants.PARAM_TYPE_OBJECT.equals(apiParam.getType())
					|| (ParamTypeConstants.PARAM_TYPE_ARRAY.equals(apiParam.getType()) && apiParam.isHasItems())) {
				parameter.setType("object(complex POJO please use @RequestBody)");
			}
			else {
				String desc = apiParam.getDesc();
				if (desc.contains(ParamTypeConstants.PARAM_TYPE_FILE)) {
					parameter.setType(ParamTypeConstants.PARAM_TYPE_FILE);
				}
				else if (desc.contains("string")) {
					parameter.setType("string");
				}
				else {
					parameter.setType("integer");
				}
			}
		}
		return parameter;
	}

}
