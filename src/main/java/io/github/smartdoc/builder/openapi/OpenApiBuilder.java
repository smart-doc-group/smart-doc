/*
 * smart-doc
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
import io.github.smartdoc.constants.Methods;
import io.github.smartdoc.constants.OpenApiTagNameTypeEnum;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.generation.DocGenerator;
import io.github.smartdoc.generation.RenderFormat;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiExceptionStatus;
import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.TagDoc;
import io.github.smartdoc.model.openapi.Components;
import io.github.smartdoc.model.openapi.Info;
import io.github.smartdoc.model.openapi.OpenApi;
import io.github.smartdoc.model.openapi.OpenApiTag;
import io.github.smartdoc.model.openapi.Server;
import io.github.smartdoc.model.openapi.Operation;
import io.github.smartdoc.model.openapi.Parameter;
import io.github.smartdoc.model.openapi.RequestBody;
import io.github.smartdoc.model.openapi.Response;
import io.github.smartdoc.model.openapi.Schema;
import io.github.smartdoc.utils.JsonUtil;
import io.github.smartdoc.utils.OpenApiSchemaUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * OpenApi Builder
 *
 * @author xingzi
 * @since 2.6.2
 */
public class OpenApiBuilder extends AbstractOpenApiBuilder {

	/**
	 * Instance
	 */
	private static final OpenApiBuilder INSTANCE = new OpenApiBuilder();

	/**
	 * OperationId OrderNo Map
	 */
	private static final Map<String, Integer> OPERATIONID_ORDER_NO_MAP = new HashMap<>();

	/**
	 * private constructor
	 */
	private OpenApiBuilder() {
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
	 */
	public static void buildOpenApi(ApiConfig config, JavaProjectBuilder projectBuilder) {
		new DocGenerator(config, projectBuilder).generate(Collections.singletonList(RenderFormat.OPENAPI));
	}

	/**
	 * Renders an OpenApi 3 document from an already parsed schema.
	 * @param config Configuration of smart-doc
	 * @param apiSchema parsed api schema
	 */
	public static void render(ApiConfig config, ApiSchema<ApiDoc> apiSchema) {
		INSTANCE.openApiCreate(config, apiSchema);
	}

	@Override
	public String getModuleName() {
		return DocGlobalConstants.OPENAPI_3_COMPONENT_KRY;
	}

	@Override
	public void openApiCreate(ApiConfig config, ApiSchema<ApiDoc> apiSchema) {
		OPERATIONID_ORDER_NO_MAP.clear();
		this.setComponentKey(getModuleName());
		OpenApi json = new OpenApi().setInfo(buildInfo(config)).setServers(buildServers(config));
		Set<OpenApiTag> tags = new HashSet<>();
		json.setTags(tags);
		json.setPaths(this.buildPaths(config, apiSchema, tags, false));
		json.setComponents(new Components().setSchemas(this.buildComponentData(apiSchema)));

		String filePath = config.getOutPath();
		filePath = filePath + DocGlobalConstants.OPEN_API_JSON;
		String data = JsonUtil.toPrettyJson(json);
		FileUtil.nioWriteFile(data, filePath);
	}

	/**
	 * Build openapi info
	 * @param apiConfig Configuration of smart-doc
	 * @return Map
	 */
	private static Info buildInfo(ApiConfig apiConfig) {
		return new Info()
			.setTitle(apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName())
			.setVersion("v1.0.0");
	}

	/**
	 * Build Servers
	 * @param config Configuration of smart-doc
	 * @return operation of the method
	 */
	private static List<Server> buildServers(ApiConfig config) {
		List<Server> serverList = new ArrayList<>();
		serverList.add(new Server().setUrl(config.getServerUrl() == null ? "" : config.getServerUrl()));
		return serverList;
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
		if (!Objects.equals(apiMethodDoc.getDesc(), apiMethodDoc.getDetail())) {
			// When summary and description are equal, there is only one
			operation.setDescription(apiMethodDoc.getDetail());
		}
		OpenApiTagNameTypeEnum openApiTagNameType = apiConfig.getOpenApiTagNameType();
		switch (openApiTagNameType) {
			case DESCRIPTION:
				operation.setTags(Collections.singletonList(apiDoc.getDesc()));
				break;
			case PACKAGE_NAME:
				operation.setTags(Collections.singletonList(apiDoc.getPackageName()));
				break;
			default:
				operation.setTags(apiMethodDoc.getTagRefs().stream().map(TagDoc::getTag).collect(Collectors.toList()));
		}
		operation.setRequestBody(this.buildRequestBody(apiConfig, apiMethodDoc));
		operation.setParameters(this.buildParameters(apiMethodDoc));
		operation.setResponses(this.buildResponses(apiConfig, apiMethodDoc, apiExceptionStatuses));
		operation.setDeprecated(apiMethodDoc.isDeprecated());
		// add operationId
		operation.setOperationId(
				this.buildOperationId(apiConfig.getOperationIdStrategy(), apiMethodDoc, OPERATIONID_ORDER_NO_MAP));
		// add extension attribution
		if (apiMethodDoc.getExtensions() != null) {
			apiMethodDoc.getExtensions().forEach(operation::addExtension);
		}
		return operation;
	}

	/**
	 * Build the request body of a method.
	 * @param apiConfig Configuration of smart-doc
	 * @param apiMethodDoc Data of method
	 * @return request body, null when the method has no body
	 */
	private RequestBody buildRequestBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
		boolean isPost = (apiMethodDoc.getType().equals(Methods.POST.getValue())
				|| apiMethodDoc.getType().equals(Methods.PUT.getValue())
				|| apiMethodDoc.getType().equals(Methods.PATCH.getValue()));
		// add content of post method
		if (isPost) {
			return new RequestBody().setContent(this.buildContent(apiConfig, apiMethodDoc, false));
		}
		return null;
	}

	/**
	 * Build the response body of a method.
	 * @param apiConfig Configuration of smart-doc
	 * @param apiMethodDoc Data of method
	 * @return response body
	 */
	@Override
	public Response buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
		return new Response().setDescription("OK").setContent(this.buildContent(apiConfig, apiMethodDoc, true));
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
			Parameter parameter = this.getStringParams(apiParam, apiParam.isHasItems());
			parameter.setIn("path");
			List<ApiParam> children = apiParam.getChildren();
			if (CollectionUtil.isEmpty(children)) {
				parametersList.add(parameter);
			}
		}
		for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
			if (apiParam.isHasItems()) {
				Parameter parameter = this.getStringParams(apiParam, false);
				Schema arraySchema = new Schema().setType(ParamTypeConstants.PARAM_TYPE_ARRAY)
					.setItems(this.getStringParams(apiParam, apiParam.isHasItems()));
				parameter.setSchema(arraySchema);
				parametersList.add(parameter);
			}
			else {
				Parameter parameter = this.getStringParams(apiParam, false);
				List<ApiParam> children = apiParam.getChildren();
				if (CollectionUtil.isEmpty(children)) {
					parametersList.add(parameter);
				}
			}
		}
		// with headers
		if (!CollectionUtil.isEmpty(apiMethodDoc.getRequestHeaders())) {
			for (ApiReqParam header : apiMethodDoc.getRequestHeaders()) {
				Parameter parameter = new Parameter().setName(header.getName())
					.setRequired(header.isRequired())
					.setDescription(header.getDesc())
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
		// add mock value for parameters
		if (StringUtils.isNotEmpty(apiParam.getValue())) {
			parameter.setExample(getExampleValueBasedOnTypeForApiParam(apiParam));
		}
		if (!hasItems) {
			parameter.setName(apiParam.getField());
			parameter.setDescription(apiParam.getDesc());
			parameter.setRequired(apiParam.isRequired());
			parameter.setIn("query");
			parameter.setSchema(this.buildParametersSchema(apiParam));
		}
		else {
			if (ParamTypeConstants.PARAM_TYPE_OBJECT.equals(apiParam.getType())
					|| (ParamTypeConstants.PARAM_TYPE_ARRAY.equals(apiParam.getType()) && apiParam.isHasItems())) {
				parameter.setType("object");
				parameter.setDescription("(complex POJO please use @RequestBody)");
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
			Schema schema = this.buildParametersSchema(apiParam);
			if (schema.getType() != null) {
				parameter.setType(schema.getType());
			}
			if (schema.getFormat() != null) {
				parameter.setFormat(schema.getFormat());
			}
			if (schema.getEnumValues() != null) {
				parameter.setEnumValues(schema.getEnumValues());
			}
			if (schema.getItems() != null) {
				parameter.setItems(schema.getItems());
			}
		}
		if (apiParam.getExtensions() != null && !apiParam.getExtensions().isEmpty()) {
			apiParam.getExtensions().forEach(parameter::addExtension);
		}
		return parameter;
	}

}
