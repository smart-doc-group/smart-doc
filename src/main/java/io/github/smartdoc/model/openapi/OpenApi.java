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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Root document of OpenAPI 3.
 *
 * @author smart-doc
 */
public class OpenApi implements Serializable {

	private static final long serialVersionUID = 1234567890123456789L;

	private String openapi = "3.1.0";

	/**
	 * Metadata of the api.
	 */
	private Info info;

	/**
	 * Servers of the api.
	 */
	private List<Server> servers;

	/**
	 * Tags of the api.
	 */
	private Set<OpenApiTag> tags;

	/**
	 * Path items keyed by path.
	 */
	private Map<String, PathItem> paths;

	/**
	 * Reusable components of the document.
	 */
	private Components components;

	/**
	 * Returns the OpenAPI version.
	 * @return OpenAPI version
	 */
	public String getOpenapi() {
		return openapi;
	}

	/**
	 * Sets the OpenAPI version.
	 * @param openapi OpenAPI version
	 * @return this openApi
	 */
	public OpenApi setOpenapi(String openapi) {
		this.openapi = openapi;
		return this;
	}

	/**
	 * Returns the info.
	 * @return info
	 */
	public Info getInfo() {
		return info;
	}

	/**
	 * Sets the info.
	 * @param info info
	 * @return this openApi
	 */
	public OpenApi setInfo(Info info) {
		this.info = info;
		return this;
	}

	/**
	 * Returns the servers.
	 * @return servers
	 */
	public List<Server> getServers() {
		return servers;
	}

	/**
	 * Sets the servers.
	 * @param servers servers
	 * @return this openApi
	 */
	public OpenApi setServers(List<Server> servers) {
		this.servers = servers;
		return this;
	}

	/**
	 * Returns the tags.
	 * @return tags
	 */
	public Set<OpenApiTag> getTags() {
		return tags;
	}

	/**
	 * Sets the tags.
	 * @param tags tags
	 * @return this openApi
	 */
	public OpenApi setTags(Set<OpenApiTag> tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * Returns the paths.
	 * @return paths
	 */
	public Map<String, PathItem> getPaths() {
		return paths;
	}

	/**
	 * Sets the paths.
	 * @param paths paths
	 * @return this openApi
	 */
	public OpenApi setPaths(Map<String, PathItem> paths) {
		this.paths = paths;
		return this;
	}

	/**
	 * Returns the components.
	 * @return components
	 */
	public Components getComponents() {
		return components;
	}

	/**
	 * Sets the components.
	 * @param components components
	 * @return this openApi
	 */
	public OpenApi setComponents(Components components) {
		this.components = components;
		return this;
	}

}
