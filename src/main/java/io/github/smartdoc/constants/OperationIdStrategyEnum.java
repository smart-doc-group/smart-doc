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
package io.github.smartdoc.constants;

/**
 * Strategy used to generate the {@code operationId} of OpenAPI and Swagger documents.
 *
 * @author smart-doc
 * @since 4.0.0
 */
public enum OperationIdStrategyEnum {

	/**
	 * Use the Java method name as the operation id. A numeric suffix is appended when the
	 * same method name occurs more than once.
	 * <p>
	 * Example: {@code getUserInfo} or {@code getUserInfo_1} for duplicates.
	 * </p>
	 */
	METHOD_NAME,

	/**
	 * Use the generated method id, which is an MD5 hash of the controller, method name
	 * and order.
	 * <p>
	 * Example: {@code a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4}.
	 * </p>
	 */
	METHOD_ID,

	/**
	 * Use the path segments, the method name and the HTTP method type.
	 * <p>
	 * Example: for a POST request to {@code /api/users/profile} handled by
	 * {@code updateProfile}, the operation id is
	 * {@code api-users-profile-updateProfile-POST}.
	 * </p>
	 */
	PATH_METHOD_HTTP;

}
