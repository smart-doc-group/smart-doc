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
import java.util.Locale;

/**
 * Path item object of OpenAPI, it holds one operation per http method.
 *
 * @author smart-doc
 */
public class PathItem implements Serializable {

	private static final long serialVersionUID = -2234632922211811077L;

	/**
	 * Description of the path item.
	 */
	private String description;

	/**
	 * GET operation.
	 */
	private Operation get;

	/**
	 * POST operation.
	 */
	private Operation post;

	/**
	 * PUT operation.
	 */
	private Operation put;

	/**
	 * DELETE operation.
	 */
	private Operation delete;

	/**
	 * OPTIONS operation.
	 */
	private Operation options;

	/**
	 * HEAD operation.
	 */
	private Operation head;

	/**
	 * PATCH operation.
	 */
	private Operation patch;

	/**
	 * TRACE operation.
	 */
	private Operation trace;

	/**
	 * Returns the description.
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * @param description description
	 * @return this pathItem
	 */
	public PathItem setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Returns the get.
	 * @return get
	 */
	public Operation getGet() {
		return get;
	}

	/**
	 * Returns the post.
	 * @return post
	 */
	public Operation getPost() {
		return post;
	}

	/**
	 * Returns the put.
	 * @return put
	 */
	public Operation getPut() {
		return put;
	}

	/**
	 * Returns the delete.
	 * @return delete
	 */
	public Operation getDelete() {
		return delete;
	}

	/**
	 * Returns the options.
	 * @return options
	 */
	public Operation getOptions() {
		return options;
	}

	/**
	 * Returns the head.
	 * @return head
	 */
	public Operation getHead() {
		return head;
	}

	/**
	 * Returns the patch.
	 * @return patch
	 */
	public Operation getPatch() {
		return patch;
	}

	/**
	 * Returns the trace.
	 * @return trace
	 */
	public Operation getTrace() {
		return trace;
	}

	/**
	 * Sets the operation of the given http method.
	 * @param method http method, case insensitive
	 * @param operation operation of the method
	 * @return this path item
	 */
	public PathItem setOperation(String method, Operation operation) {
		switch (method.toLowerCase(Locale.ROOT)) {
			case "get":
				this.get = operation;
				break;
			case "post":
				this.post = operation;
				break;
			case "put":
				this.put = operation;
				break;
			case "delete":
				this.delete = operation;
				break;
			case "options":
				this.options = operation;
				break;
			case "head":
				this.head = operation;
				break;
			case "patch":
				this.patch = operation;
				break;
			case "trace":
				this.trace = operation;
				break;
			default:
				throw new IllegalArgumentException("Unsupported http method: " + method);
		}
		return this;
	}

}
