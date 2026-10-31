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

import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable parse result that can be forked for each renderer.
 *
 * @author smart-doc
 */
public final class ParseSnapshot {

	private final ParseKey key;

	private final ApiSchema<ApiDoc> schema;

	/**
	 * Creates a snapshot.
	 * @param key parse key
	 * @param schema parsed api schema
	 */
	public ParseSnapshot(ParseKey key, ApiSchema<ApiDoc> schema) {
		this.key = key;
		this.schema = schema;
	}

	/**
	 * Returns the parse key this snapshot was created with.
	 * @return parse key
	 */
	public ParseKey getKey() {
		return key;
	}

	/**
	 * Creates a schema whose api docs are independent copies, so that one renderer cannot
	 * affect another. Method lists and tag references are shared because renderers only
	 * replace references and never mutate methods or tags.
	 * @return schema for a single renderer
	 */
	public ApiSchema<ApiDoc> fork() {
		List<ApiDoc> docs = new ArrayList<>(schema.getApiDatas().size());
		for (ApiDoc doc : schema.getApiDatas()) {
			docs.add(doc.fork());
		}
		ApiSchema<ApiDoc> copy = new ApiSchema<>();
		copy.setApiDatas(docs);
		copy.setApiExceptionStatuses(schema.getApiExceptionStatuses());
		return copy;
	}

}
