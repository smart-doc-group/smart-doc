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

/**
 * Components object of OpenAPI.
 *
 * @author smart-doc
 */
public class Components implements Serializable {

	private static final long serialVersionUID = 998877665544332211L;

	/**
	 * Schemas keyed by schema name.
	 */
	private Map<String, Schema> schemas;

	/**
	 * Returns the schemas.
	 * @return schemas
	 */
	public Map<String, Schema> getSchemas() {
		return schemas;
	}

	/**
	 * Sets the schemas.
	 * @param schemas schemas
	 * @return this component container
	 */
	public Components setSchemas(Map<String, Schema> schemas) {
		this.schemas = schemas;
		return this;
	}

}
