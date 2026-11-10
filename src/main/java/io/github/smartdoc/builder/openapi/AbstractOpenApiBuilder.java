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

import io.github.smartdoc.builder.DocBuilderTemplate;
import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.ComponentTypeEnum;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.MediaType;
import io.github.smartdoc.constants.Methods;
import io.github.smartdoc.constants.OperationIdStrategyEnum;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.exception.UnsupportedGoalFrameworkException;
import io.github.smartdoc.generation.GoalFamily;
import io.github.smartdoc.generation.TemplateResolver;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiExceptionStatus;
import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.DocMapping;
import io.github.smartdoc.model.TagDoc;
import io.github.smartdoc.model.openapi.Example;
import io.github.smartdoc.model.openapi.OpenApiMediaType;
import io.github.smartdoc.model.openapi.OpenApiTag;
import io.github.smartdoc.model.openapi.Response;
import io.github.smartdoc.model.openapi.ResponseBody;
import io.github.smartdoc.model.openapi.Operation;
import io.github.smartdoc.model.openapi.Parameter;
import io.github.smartdoc.model.openapi.PathItem;
import io.github.smartdoc.model.openapi.Schema;
import io.github.smartdoc.template.IDocBuildTemplate;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.OpenApiSchemaUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static io.github.smartdoc.constants.DocGlobalConstants.OPENAPI_2_COMPONENT_KRY;
import static io.github.smartdoc.constants.DocGlobalConstants.OPENAPI_3_COMPONENT_KRY;

/**
 * abstract openapi builder
 *
 * @author xingzi Date 2022/10/12 18:49
 * @since 2.6.2
 */
public abstract class AbstractOpenApiBuilder {

	/**
	 * Creates a builder.
	 */
	protected AbstractOpenApiBuilder() {
	}

	/**
	 * Logger for the class.
	 */
	private static final Logger logger = Logger.getLogger(AbstractOpenApiBuilder.class.getName());

	/**
	 * Component key
	 */
	private String componentKey;

	/**
	 * Get component key
	 * @return component key
	 */
	public String getComponentKey() {
		return componentKey;
	}

	/**
	 * Set component key
	 * @param componentKey component key
	 */
	public void setComponentKey(String componentKey) {
		this.componentKey = componentKey;
	}

	/**
	 * Get module name
	 * @return module name
	 */
	abstract String getModuleName();

	/**
	 * Create OpenAPI definition
	 * @param apiConfig Configuration of smart-doc
	 * @param apiSchema Project API schema
	 */
	abstract void openApiCreate(ApiConfig apiConfig, ApiSchema<ApiDoc> apiSchema);

	/**
	 * Build request
	 * @param apiConfig Configuration of smart-doc
	 * @param apiMethodDoc Data of method
	 * @param apiDoc singe api doc
	 * @param apiExceptionStatuses Exception status list
	 * @return Map of request urls
	 */
	abstract Operation buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc,
			List<ApiExceptionStatus> apiExceptionStatuses);

	/**
	 * response body
	 * @param apiMethodDoc ApiMethodDoc
	 * @param apiConfig ApiConfig
	 * @return Map of response body
	 */
	abstract ResponseBody buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc);

	/**
	 * Build request parameters
	 * @param apiMethodDoc API data for the method
	 * @return List of parameters
	 */
	abstract List<Parameter> buildParameters(ApiMethodDoc apiMethodDoc);

	/**
	 * Build request parameters
	 * @param apiParam ApiParam
	 * @param hasItems has items
	 * @return Map of request parameters
	 */
	abstract Parameter getStringParams(ApiParam apiParam, boolean hasItems);

	/**
	 * String component
	 */
	protected static final Schema STRING_COMPONENT = Schema.string();

	/**
	 * Build openapi paths
	 * @param apiConfig Configuration of smart-doc
	 * @param apiSchema Project API schema
	 * @param tags tags
	 * @param isSwagger is swagger
	 * @return path items keyed by path
	 */
	public Map<String, PathItem> buildPaths(ApiConfig apiConfig, ApiSchema<ApiDoc> apiSchema, Set<OpenApiTag> tags,
			boolean isSwagger) {
		Map<String, PathItem> pathMap = new LinkedHashMap<>(500);

		List<ApiDoc> apiDocs = apiSchema.getApiDatas();
		for (ApiDoc apiDoc : apiDocs) {
			if (CollectionUtil.isEmpty(apiDoc.getList())) {
				continue;
			}
			for (ApiMethodDoc methodDoc : apiDoc.getList()) {
				String[] paths = methodDoc.getPath().split(";");
				for (String path : paths) {
					path = path.trim();
					if (StringUtil.isNotEmpty(apiConfig.getPathPrefix()) && isSwagger) {
						path = path.replace(apiConfig.getPathPrefix(), "");
					}
					Operation operation = this.buildPathUrlsRequest(apiConfig, methodDoc, methodDoc.getClazzDoc(),
							apiSchema.getApiExceptionStatuses());
					pathMap.computeIfAbsent(path, key -> new PathItem()).setOperation(methodDoc.getType(), operation);
				}
			}
		}
		for (Map.Entry<String, TagDoc> docEntry : DocMapping.getTagDocMap().entrySet()) {
			tags.addAll(docEntry.getValue()
				.getClazzDocs()
				.stream()
				// optimize tag content for compatible to swagger
				.map(doc -> OpenApiTag.of(apiConfig.getOpenApiTagNameType(), doc))
				.collect(Collectors.toSet()));
		}
		return pathMap;
	}

	/**
	 * Build content for responses and requestBody
	 * @param apiConfig ApiConfig
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return content keyed by media type
	 */
	public Map<String, OpenApiMediaType> buildContent(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep) {
		Map<String, OpenApiMediaType> content = new HashMap<>(16);
		String contentType = apiMethodDoc.getContentType();
		if (isRep) {
			contentType = "*/*";
		}
		content.put(contentType, this.buildContentBody(apiConfig, apiMethodDoc, isRep));
		return content;
	}

	/**
	 * Build data of content
	 * @param apiConfig ApiConfig
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return media type of the content
	 */
	public OpenApiMediaType buildContentBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep) {
		OpenApiMediaType mediaType = new OpenApiMediaType();
		if (Objects.nonNull(apiMethodDoc.getReturnSchema()) && isRep) {
			mediaType.setSchema(apiMethodDoc.getReturnSchema());
		}
		else if (!isRep && Objects.nonNull(apiMethodDoc.getRequestSchema())) {
			mediaType.setSchema(apiMethodDoc.getRequestSchema());
		}
		else {
			mediaType.setSchema(this.buildBodySchema(apiMethodDoc, isRep));
		}

		if (OPENAPI_2_COMPONENT_KRY.equals(componentKey) && !isRep) {
			mediaType.setName(apiMethodDoc.getName());
		}
		if (OPENAPI_3_COMPONENT_KRY.equals(componentKey)
				&& (!isRep && apiConfig.isRequestExample() || (isRep && apiConfig.isResponseExample()))) {
			mediaType.setExamples(buildBodyExample(apiMethodDoc, isRep));
		}
		return mediaType;
	}

	/**
	 * Build schema of Body
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return schema of the body
	 */
	public Schema buildBodySchema(ApiMethodDoc apiMethodDoc, boolean isRep) {
		Schema schema = new Schema();
		// For response
		if (isRep) {
			String responseRef = componentKey
					+ OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getResponseParams());
			if (apiMethodDoc.getIsResponseArray() == 1) {
				schema.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
				schema.setItems(new Schema().setRef(responseRef));
			}
			else if (CollectionUtil.isNotEmpty(apiMethodDoc.getResponseParams())) {
				schema.setRef(responseRef);
			}
			return schema;
		}

		// for request
		String requestRef;
		String randomName = ComponentTypeEnum.getRandomName(ApiConfig.getInstance().getComponentType(), apiMethodDoc);
		if (Methods.POST.getValue().equals(apiMethodDoc.getType())
				&& (apiMethodDoc.getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						|| apiMethodDoc.getContentType().equals(MediaType.MULTIPART_FORM_DATA_VALUE))) {
			schema.setType(ParamTypeConstants.PARAM_TYPE_OBJECT);
			Schema requestSchema = this.buildProperties(apiMethodDoc.getRequestParams(), new HashMap<>(),
					Boolean.FALSE);
			schema.setProperties(requestSchema.getProperties());
			schema.setRequired(requestSchema.getRequired());
			return schema;
		}
		else if (apiMethodDoc.getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
			requestRef = componentKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getQueryParams());
		}
		else {
			requestRef = componentKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getRequestParams());
		}
		// remove special characters in url
		if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
			if (apiMethodDoc.getIsRequestArray() == 1) {
				schema.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
				schema.setItems(new Schema().setRef(requestRef));
			}
			else {
				schema.setRef(requestRef);
			}
		}
		return schema;
	}

	/**
	 * Build body example
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return examples keyed by example name
	 */
	public static Map<String, Example> buildBodyExample(ApiMethodDoc apiMethodDoc, boolean isRep) {
		Map<String, Example> content = new HashMap<>(8);
		content.put("json", buildExampleData(apiMethodDoc, isRep));
		return content;
	}

	/**
	 * Build example data
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return example of the request or response
	 */
	public static Example buildExampleData(ApiMethodDoc apiMethodDoc, boolean isRep) {
		Example example = new Example().setSummary("test data");
		if (!isRep) {
			example.setValue(StringUtil.isEmpty(apiMethodDoc.getRequestExample().getJsonBody())
					? apiMethodDoc.getRequestExample().getExampleBody()
					: apiMethodDoc.getRequestExample().getJsonBody());
		}
		else {
			example.setValue(apiMethodDoc.getResponseUsage());
		}
		return example;
	}

	/**
	 * If it is a get request or @PathVariable set the request parameters
	 * @param apiParam Parameter information
	 * @return parameters schema
	 */
	public Schema buildParametersSchema(ApiParam apiParam) {
		Schema schema = new Schema();
		String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
		// The values of openApiType are "file", "object", "array","string",
		// "integer","number"
		schema.setType(openApiType);
		switch (openApiType) {
			case ParamTypeConstants.PARAM_TYPE_FILE:
				schema.setFormat("binary");
				schema.setType("string");
				break;
			case ParamTypeConstants.PARAM_TYPE_OBJECT:
				if ("enum".equals(apiParam.getType())) {
					schema.setEnumValues(apiParam.getEnumValues());
				}
				break;
			case ParamTypeConstants.PARAM_TYPE_ARRAY:
				if (CollectionUtil.isNotEmpty(apiParam.getEnumValues())) {
					schema.setType("string");
					schema.setItems(apiParam.getEnumValues());
				}
				else {
					schema.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
					schema.setItems(Schema.string());
				}
				break;
			default:
				// "string", "integer", "number"
				schema.setFormat(apiParam.getType());
				if ("enum".equals(apiParam.getType())) {
					schema.setEnumValues(apiParam.getEnumValues());
				}
				break;
		}
		return schema;
	}

	/**
	 * If the header is included, set the request parameters
	 * @param header header
	 * @return parameters schema
	 */
	public static Schema buildParametersSchema(ApiReqParam header) {
		Schema schema = new Schema();
		String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(header.getType());
		schema.setType(openApiType);
		schema.setFormat(openApiType);
		return schema;
	}

	/**
	 * build response
	 * @param apiMethodDoc ApiMethodDoc
	 * @param apiConfig ApiConfig
	 * @param apiExceptionStatuses apiExceptionStatuses
	 * @return responses keyed by http status code
	 */
	public Map<String, ResponseBody> buildResponses(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc,
			List<ApiExceptionStatus> apiExceptionStatuses) {
		Map<String, ResponseBody> response = new LinkedHashMap<>(8);
		response.put("200", this.buildResponsesBody(apiConfig, apiMethodDoc));
		if (CollectionUtil.isNotEmpty(apiExceptionStatuses)) {
			for (ApiExceptionStatus apiExceptionStatus : apiExceptionStatuses) {
				response.put(apiExceptionStatus.getStatus(),
						this.buildExceptionResponsesBody(apiConfig, apiExceptionStatus));
			}
		}
		return response;
	}

	/**
	 * Build exception response body
	 * @param apiConfig ApiConfig
	 * @param apiExceptionStatus ApiExceptionStatus
	 * @return response body of the exception
	 */
	public Response buildExceptionResponsesBody(ApiConfig apiConfig, ApiExceptionStatus apiExceptionStatus) {
		Response response = new Response().setDescription(apiExceptionStatus.getDesc());
		OpenApiMediaType mediaType = new OpenApiMediaType();
		String responseRef = componentKey
				+ OpenApiSchemaUtil.getClassNameFromParams(apiExceptionStatus.getExceptionResponseParams());
		if (CollectionUtil.isNotEmpty(apiExceptionStatus.getExceptionResponseParams())) {
			mediaType.setSchema(new Schema().setRef(responseRef));
		}
		if (OPENAPI_3_COMPONENT_KRY.equals(componentKey) && apiConfig.isResponseExample()) {
			Map<String, Example> examples = new HashMap<>(8);
			examples.put("json",
					new Example().setSummary("response example").setValue(apiExceptionStatus.getResponseUsage()));
			mediaType.setExamples(examples);
		}
		Map<String, OpenApiMediaType> content = new HashMap<>(8);
		content.put("*/*", mediaType);
		response.setContent(content);
		return response;
	}

	/**
	 * component schema properties
	 * @param apiParam list of ApiParam
	 * @param component component
	 * @param isResp is response
	 * @return object schema holding the properties
	 */
	public Schema buildProperties(List<ApiParam> apiParam, Map<String, Schema> component, boolean isResp) {
		Schema schema = new Schema();
		Map<String, Schema> propertiesData = new LinkedHashMap<>();
		List<String> requiredList = new ArrayList<>();
		if (apiParam != null) {
			int paramsSize = apiParam.size();
			for (ApiParam param : apiParam) {
				if (param.isRequired()) {
					requiredList.add(param.getField());
				}
				if (ParamTypeConstants.PARAM_TYPE_MAP.equals(param.getType())
						&& StringUtil.isEmpty(param.getClassName())) {
					continue;
				}
				if (param.isQueryParam() || param.isPathParam()) {
					continue;
				}
				String field = param.getField();
				propertiesData.put(field, this.buildPropertiesData(param, component, isResp));
			}
			if (!propertiesData.isEmpty()) {
				schema.setType("object");
				schema.setProperties(propertiesData);
			}
			if (!CollectionUtil.isEmpty(requiredList)) {
				schema.setRequired(requiredList);
			}
		}
		return schema;
	}

	/**
	 * Build the schema of one property.
	 * @param apiParam ApiParam of the property
	 * @param component component schemas collected so far
	 * @param isResp whether the property belongs to a response
	 * @return schema of the property
	 */
	private Schema buildPropertiesData(ApiParam apiParam, Map<String, Schema> component, boolean isResp) {
		Schema schema = new Schema();
		String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
		// array object file map
		schema.setDescription(apiParam.getDesc());
		if (StringUtil.isNotEmpty(apiParam.getValue())) {
			schema.setExample(this.getExampleValueBasedOnTypeForApiParam(apiParam));
		}

		if (!"object".equals(openApiType)) {
			schema.setType(openApiType);
			schema.setFormat("int16".equals(apiParam.getType()) ? "int32" : apiParam.getType());
			if ("enum".equals(apiParam.getType())) {
				schema.setEnumValues(apiParam.getEnumValues());
			}
		}
		if ("map".equals(apiParam.getType())) {
			schema.setType("object");
			schema.setDescription(apiParam.getDesc() + "(map data)");
		}
		if ("array".equals(apiParam.getType())) {
			schema.setType("array");
			if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
				if (!apiParam.isSelfReferenceLoop()) {
					String childSchemaName = OpenApiSchemaUtil.getClassNameFromParams(apiParam.getChildren());
					if (childSchemaName.contains(OpenApiSchemaUtil.NO_BODY_PARAM)) {
						schema.setType("object");
						schema.setDescription(apiParam.getDesc() + "(object)");
					}
					else {
						component.put(childSchemaName, this.buildProperties(apiParam.getChildren(), component, isResp));
						schema.setItems(new Schema().setRef(componentKey + childSchemaName));
					}
				}
			}
			else {
				schema.setItems(new Schema().setType("string"));
			}
		}
		if ("file".equals(apiParam.getType())) {
			schema.setType("string");
			schema.setFormat("binary");
		}
		if ("object".equals(apiParam.getType())) {
			schema.setDescription(apiParam.getDesc() + "(object)");
			if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
				if (!apiParam.isSelfReferenceLoop()) {
					String childSchemaName = OpenApiSchemaUtil.getClassNameFromParams(apiParam.getChildren());
					if (childSchemaName.contains(OpenApiSchemaUtil.NO_BODY_PARAM)) {
						schema.setType("object");
					}
					else {
						component.put(childSchemaName, this.buildProperties(apiParam.getChildren(), component, isResp));
						schema.setRef(componentKey + childSchemaName);
					}
				}
			}
			else {
				schema.setType("object");
			}
		}
		if (apiParam.getExtensions() != null && !apiParam.getExtensions().isEmpty()) {
			apiParam.getExtensions().forEach(schema::addExtension);
		}

		return schema;
	}

	/**
	 * Builds component data for API documentation. This method iterates through all API
	 * documentation entries to extract request and response parameter information, and
	 * organizes them into OpenAPI component schemas.
	 * @param apiSchema The API documentation schema.
	 * @return component schemas keyed by schema name
	 */
	public Map<String, Schema> buildComponentData(ApiSchema<ApiDoc> apiSchema) {
		Map<String, Schema> component = new HashMap<>(16);
		component.put(DocGlobalConstants.DEFAULT_PRIMITIVE, STRING_COMPONENT);
		apiSchema.getApiDatas().forEach(entrypoint -> {
			List<ApiMethodDoc> apiMethodDocs = entrypoint.getList();
			apiMethodDocs.forEach(method -> {
				// request components
				String requestSchema = OpenApiSchemaUtil.getClassNameFromParams(method.getRequestParams());
				List<ApiParam> requestParams = method.getRequestParams();
				Schema prop = this.buildProperties(requestParams, component, false);
				component.put(requestSchema, prop);
				// response components
				List<ApiParam> responseParams = method.getResponseParams();
				String responseSchemaName = OpenApiSchemaUtil.getClassNameFromParams(method.getResponseParams());
				component.put(responseSchemaName, this.buildProperties(responseParams, component, true));
			});
		});
		// Exception response components
		if (Objects.nonNull(apiSchema.getApiExceptionStatuses())) {
			apiSchema.getApiExceptionStatuses().forEach(e -> {
				List<ApiParam> responseParams = e.getExceptionResponseParams();
				String responseSchemaName = OpenApiSchemaUtil.getClassNameFromParams(e.getExceptionResponseParams());
				component.put(responseSchemaName, this.buildProperties(responseParams, component, true));
			});
		}
		component.remove(OpenApiSchemaUtil.NO_BODY_PARAM);
		return component;
	}

	/**
	 * Get a list of OpenAPI's document data
	 * @param config Configuration of smart-doc
	 * @param projectBuilder JavaDocBuilder of QDox
	 * @return List of OpenAPI's document data
	 * @throws UnsupportedGoalFrameworkException when the configured framework cannot
	 * produce {@link ApiDoc}
	 */
	public ApiSchema<ApiDoc> getOpenApiDocs(ApiConfig config, JavaProjectBuilder projectBuilder) {
		config.setShowJavaType(false);
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.TRUE);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
		config.setParamsDataToTree(true);
		IDocBuildTemplate<ApiDoc> docBuildTemplate = TemplateResolver.<ApiDoc>resolve(config, GoalFamily.REST)
			.template();
		return docBuildTemplate.getApiData(configBuilder);
	}

	/**
	 * Get example value based on type for ApiParam
	 * @param apiParam ApiParam
	 * @return example value
	 */
	protected Object getExampleValueBasedOnTypeForApiParam(ApiParam apiParam) {
		return getValueByType(apiParam.getValue(), apiParam.getType());
	}

	/**
	 * Get example value based on type for ApiReqParam
	 * @param apiReqParam ApiReqParam
	 * @return example value
	 */
	protected Object getExampleValueBasedOnTypeForApiReqParam(ApiReqParam apiReqParam) {
		return getValueByType(apiReqParam.getValue(), apiReqParam.getType());
	}

	/**
	 * Get value by type
	 * @param originalValue original value
	 * @param type type
	 * @return value
	 */
	private Object getValueByType(String originalValue, String type) {
		if (StringUtil.isEmpty(originalValue) || StringUtil.isEmpty(type)) {
			return originalValue;
		}
		try {
			String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(type);
			switch (openApiType) {
				case "boolean":
					return Boolean.parseBoolean(originalValue);
				case "integer":
					return Integer.parseInt(originalValue);
				case "number":
					String javaTypeName = type.toLowerCase();
					switch (javaTypeName) {
						case "long":
						case "java.lang.long":
						case "int64":
							return Long.parseLong(originalValue);
						case "double":
						case "java.lang.double":
							return Double.parseDouble(originalValue);
						case "float":
						case "java.lang.float":
							return Float.parseFloat(originalValue);
						case "java.math.biginteger":
							return new BigInteger(originalValue);
						case "java.math.bigdecimal":
							return new BigDecimal(originalValue);
					}
					return originalValue;
			}
			return originalValue;
		}
		catch (Exception e) {
			// if there is an exception, return the original value
			logger
				.warning("Error parsing value: " + originalValue + " for type: " + type + ", error: " + e.getMessage());
			return originalValue;
		}
	}

	/**
	 * Build the operation id according to the configured strategy.
	 * @param strategy the operation id strategy, {@code null} means
	 * {@link OperationIdStrategyEnum#METHOD_NAME}
	 * @param apiMethodDoc the api method
	 * @param operationIdOrderNoMap counter used to keep method name based operation ids
	 * unique
	 * @return the operation id
	 */
	protected String buildOperationId(OperationIdStrategyEnum strategy, ApiMethodDoc apiMethodDoc,
			Map<String, Integer> operationIdOrderNoMap) {
		if (Objects.isNull(strategy)) {
			strategy = OperationIdStrategyEnum.METHOD_NAME;
		}
		switch (strategy) {
			case METHOD_ID:
				return apiMethodDoc.getMethodId();
			case PATH_METHOD_HTTP:
				List<String> pathSegments = new ArrayList<>(
						OpenApiSchemaUtil.getPatternResult("[A-Za-z0-9_]+", apiMethodDoc.getPath()));
				pathSegments.add(apiMethodDoc.getMethodName());
				pathSegments.add(apiMethodDoc.getType());
				return pathSegments.stream().filter(StringUtil::isNotEmpty).collect(Collectors.joining("-"));
			case METHOD_NAME:
			default:
				String methodName = apiMethodDoc.getMethodName();
				if (operationIdOrderNoMap.containsKey(methodName)) {
					int order = operationIdOrderNoMap.get(methodName);
					operationIdOrderNoMap.put(methodName, order + 1);
					return methodName + "_" + order;
				}
				operationIdOrderNoMap.put(methodName, 1);
				return methodName;
		}
	}

}
