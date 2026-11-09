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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Goal names declared by the smart-doc Maven plugin.
 * <p>
 * The Maven plugin declares one goal per name defined here, and
 * {@link io.github.smartdoc.generation.GoalFamily} groups the same names by the document
 * model each goal produces, so the names only live in one place. The plugin assigns these
 * constants to its own {@code MojoConstants} fields, keeping its {@code @Mojo}
 * declarations unchanged.
 *
 * @author linwumingshi
 */
public final class DocGoal {

	/**
	 * Aggregate goal that renders several formats from one parse.
	 */
	public static final String GENERATE = "generate";

	/**
	 * REST markdown.
	 */
	public static final String MARKDOWN = "markdown";

	/**
	 * REST Asciidoc.
	 */
	public static final String ADOC = "adoc";

	/**
	 * REST html.
	 */
	public static final String HTML = "html";

	/**
	 * REST Word.
	 */
	public static final String WORD = "word";

	/**
	 * OpenAPI 3.
	 */
	public static final String OPENAPI = "openapi";

	/**
	 * Swagger 2.
	 */
	public static final String SWAGGER = "swagger";

	/**
	 * JMeter script.
	 */
	public static final String JMETER = "jmeter";

	/**
	 * Postman collection.
	 */
	public static final String POSTMAN = "postman";

	/**
	 * Push REST documents to the Torna platform.
	 */
	public static final String TORNA_REST = "torna-rest";

	/**
	 * RPC markdown.
	 */
	public static final String RPC_MARKDOWN = "rpc-markdown";

	/**
	 * RPC Asciidoc.
	 */
	public static final String RPC_ADOC = "rpc-adoc";

	/**
	 * RPC html.
	 */
	public static final String RPC_HTML = "rpc-html";

	/**
	 * RPC Word.
	 */
	public static final String RPC_WORD = "rpc-word";

	/**
	 * Push RPC documents to the Torna platform.
	 */
	public static final String TORNA_RPC = "torna-rpc";

	/**
	 * gRPC markdown.
	 */
	public static final String GRPC_MARKDOWN = "grpc-markdown";

	/**
	 * gRPC Asciidoc.
	 */
	public static final String GRPC_ADOC = "grpc-adoc";

	/**
	 * gRPC html.
	 */
	public static final String GRPC_HTML = "grpc-html";

	/**
	 * Javadoc markdown.
	 */
	public static final String JAVADOC_MARKDOWN = "javadoc-markdown";

	/**
	 * Javadoc Asciidoc.
	 */
	public static final String JAVADOC_ADOC = "javadoc-adoc";

	/**
	 * Javadoc html.
	 */
	public static final String JAVADOC_HTML = "javadoc-html";

	/**
	 * WebSocket markdown.
	 */
	public static final String WEBSOCKET_MARKDOWN = "websocket-markdown";

	/**
	 * WebSocket Asciidoc.
	 */
	public static final String WEBSOCKET_ADOC = "websocket-adoc";

	/**
	 * WebSocket html.
	 */
	public static final String WEBSOCKET_HTML = "websocket-html";

	/**
	 * Goals producing {@link io.github.smartdoc.model.ApiDoc}.
	 */
	public static final List<String> REST_GOALS = Collections.unmodifiableList(
			Arrays.asList(MARKDOWN, ADOC, HTML, WORD, OPENAPI, SWAGGER, JMETER, POSTMAN, TORNA_REST, GENERATE));

	/**
	 * Goals producing {@link io.github.smartdoc.model.rpc.RpcApiDoc}.
	 */
	public static final List<String> RPC_GOALS = Collections
		.unmodifiableList(Arrays.asList(RPC_MARKDOWN, RPC_ADOC, RPC_HTML, RPC_WORD, TORNA_RPC));

	/**
	 * Goals producing {@link io.github.smartdoc.model.grpc.GrpcApiDoc}.
	 */
	public static final List<String> GRPC_GOALS = Collections
		.unmodifiableList(Arrays.asList(GRPC_MARKDOWN, GRPC_ADOC, GRPC_HTML));

	/**
	 * Goals producing {@link io.github.smartdoc.model.javadoc.JavadocApiDoc}.
	 */
	public static final List<String> JAVADOC_GOALS = Collections
		.unmodifiableList(Arrays.asList(JAVADOC_MARKDOWN, JAVADOC_ADOC, JAVADOC_HTML));

	/**
	 * Goals producing {@link io.github.smartdoc.model.WebSocketDoc}.
	 */
	public static final List<String> WEBSOCKET_GOALS = Collections
		.unmodifiableList(Arrays.asList(WEBSOCKET_MARKDOWN, WEBSOCKET_ADOC, WEBSOCKET_HTML));

	private DocGoal() {
	}

}
