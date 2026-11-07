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
package io.github.smartdoc.model.openapi;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Root document of Swagger 2.
 *
 * @author smart-doc
 */
public class Swagger implements Serializable {

	private static final long serialVersionUID = 5566778899001122334L;

	/**
	 * Version of the Swagger specification.
	 */
	private String swagger = "2.0";

	/**
	 * Metadata of the api.
	 */
	private Info info;

	/**
	 * Host of the api, including the port.
	 */
	private String host;

	/**
	 * Base path of all the paths.
	 */
	private String basePath;

	/**
	 * Tags of the api.
	 */
	private Set<OpenApiTag> tags;

	/**
	 * Path items keyed by path.
	 */
	private Map<String, PathItem> paths;

	/**
	 * Schema definitions keyed by schema name.
	 */
	private Map<String, Schema> definitions;

	/**
	 * Returns the Swagger version.
	 * @return Swagger version
	 */
	public String getSwagger() {
		return swagger;
	}

	/**
	 * Sets the Swagger version.
	 * @param swagger Swagger version
	 * @return this swagger
	 */
	public Swagger setSwagger(String swagger) {
		this.swagger = swagger;
		return this;
	}

	/**
	 * Returns the metadata of the api.
	 * @return metadata of the api
	 */
	public Info getInfo() {
		return info;
	}

	/**
	 * Sets the metadata of the api.
	 * @param info metadata of the api
	 * @return this swagger
	 */
	public Swagger setInfo(Info info) {
		this.info = info;
		return this;
	}

	/**
	 * Returns the host of the api.
	 * @return host of the api
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host of the api.
	 * @param host host of the api
	 * @return this swagger
	 */
	public Swagger setHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * Returns the base path.
	 * @return base path
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * Sets the base path.
	 * @param basePath base path
	 * @return this swagger
	 */
	public Swagger setBasePath(String basePath) {
		this.basePath = basePath;
		return this;
	}

	/**
	 * Returns the tags of the api.
	 * @return tags of the api
	 */
	public Set<OpenApiTag> getTags() {
		return tags;
	}

	/**
	 * Sets the tags of the api.
	 * @param tags tags of the api
	 * @return this swagger
	 */
	public Swagger setTags(Set<OpenApiTag> tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * Returns the path items.
	 * @return path items keyed by path
	 */
	public Map<String, PathItem> getPaths() {
		return paths;
	}

	/**
	 * Sets the path items.
	 * @param paths path items keyed by path
	 * @return this swagger
	 */
	public Swagger setPaths(Map<String, PathItem> paths) {
		this.paths = paths;
		return this;
	}

	/**
	 * Returns the schema definitions.
	 * @return schema definitions keyed by schema name
	 */
	public Map<String, Schema> getDefinitions() {
		return definitions;
	}

	/**
	 * Sets the schema definitions.
	 * @param definitions schema definitions keyed by schema name
	 * @return this swagger
	 */
	public Swagger setDefinitions(Map<String, Schema> definitions) {
		this.definitions = definitions;
		return this;
	}

}
