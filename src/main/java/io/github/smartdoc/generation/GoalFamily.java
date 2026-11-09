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

import io.github.smartdoc.constants.DocGoal;
import io.github.smartdoc.constants.FrameworkEnum;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.IDoc;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.model.grpc.GrpcApiDoc;
import io.github.smartdoc.model.javadoc.JavadocApiDoc;
import io.github.smartdoc.model.rpc.RpcApiDoc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Family of generation goals that share one document model.
 * <p>
 * A goal can only produce documents of the type its family declares. OpenAPI, Swagger,
 * Markdown and the other REST goals describe HTTP endpoints, so they require
 * {@link ApiDoc}; RPC goals require {@link RpcApiDoc}, and so on. Comparing the declared
 * type with the type actually produced by the resolved template replaces the previous
 * hand-maintained goal/framework matrix: a mismatch is reported by
 * {@link TemplateResolver} instead of surfacing later as a {@code ClassCastException}, an
 * empty document or an empty shell.
 *
 * @author linwumingshi
 */
public enum GoalFamily {

	/**
	 * HTTP endpoint goals, see {@link DocGoal#REST_GOALS}.
	 */
	REST("REST", ApiDoc.class, DocGoal.REST_GOALS, FrameworkEnum.SPRING, FrameworkEnum.SOLON, FrameworkEnum.JAX_RS),

	/**
	 * RPC goals, see {@link DocGoal#RPC_GOALS}.
	 */
	RPC("RPC", RpcApiDoc.class, DocGoal.RPC_GOALS, FrameworkEnum.DUBBO),

	/**
	 * gRPC goals, see {@link DocGoal#GRPC_GOALS}.
	 */
	GRPC("gRPC", GrpcApiDoc.class, DocGoal.GRPC_GOALS, FrameworkEnum.GRPC),

	/**
	 * Javadoc goals, see {@link DocGoal#JAVADOC_GOALS}.
	 */
	JAVADOC("Javadoc", JavadocApiDoc.class, DocGoal.JAVADOC_GOALS, FrameworkEnum.JAVADOC),

	/**
	 * WebSocket goals, see {@link DocGoal#WEBSOCKET_GOALS}.
	 */
	WEBSOCKET("WebSocket", WebSocketDoc.class, DocGoal.WEBSOCKET_GOALS, FrameworkEnum.SPRING, FrameworkEnum.SOLON,
			FrameworkEnum.JAX_RS, FrameworkEnum.DUBBO, FrameworkEnum.JAVADOC);

	/**
	 * Human readable family name, used in diagnostics and error messages.
	 */
	private final String displayName;

	/**
	 * Document type every goal of this family must produce.
	 */
	private final Class<? extends IDoc> requiredDocType;

	/**
	 * Goal names of this family, taken from {@link DocGoal} and used in diagnostics.
	 */
	private final List<String> goals;

	/**
	 * Frameworks able to serve this family. The first entry is the default applied when
	 * the configuration does not set a framework.
	 */
	private final FrameworkEnum[] frameworks;

	/**
	 * Creates a family declaration.
	 * @param displayName human-readable family name
	 * @param requiredDocType document type every goal of this family must produce
	 * @param goals goal names of this family
	 * @param frameworks frameworks able to serve this family, the first one is the
	 * default framework of the family
	 */
	GoalFamily(String displayName, Class<? extends IDoc> requiredDocType, List<String> goals,
			FrameworkEnum... frameworks) {
		this.displayName = displayName;
		this.requiredDocType = requiredDocType;
		this.goals = goals;
		this.frameworks = frameworks;
	}

	/**
	 * Returns the family that owns the given document type.
	 * @param docType document type produced by a build template
	 * @return matching family, or {@code null} when no family declares the type
	 */
	public static GoalFamily ofDocumentType(Class<?> docType) {
		for (GoalFamily family : values()) {
			if (family.requiredDocType.equals(docType)) {
				return family;
			}
		}
		return null;
	}

	/**
	 * Returns the document type every goal of this family must produce.
	 * @return required document type
	 */
	public Class<? extends IDoc> requiredDocType() {
		return requiredDocType;
	}

	/**
	 * Returns the framework applied when the configuration does not set one.
	 * @return default framework name
	 */
	public String defaultFramework() {
		return frameworks[0].getFramework();
	}

	/**
	 * Returns the framework names known to be able to serve this family.
	 * @return comma separated framework names
	 */
	public String frameworks() {
		return Arrays.stream(frameworks).map(FrameworkEnum::getFramework).collect(Collectors.joining(", "));
	}

	/**
	 * Returns the goal names of this family, used in diagnostics.
	 * @return comma separated goal names
	 */
	public String goals() {
		return String.join(", ", goals);
	}

	/**
	 * Returns the human readable family name.
	 * @return family name
	 */
	public String displayName() {
		return displayName;
	}

}
