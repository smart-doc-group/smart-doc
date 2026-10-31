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

import io.github.smartdoc.model.ApiConfig;

import java.util.Objects;

/**
 * Configuration flags that change the parse result.
 * <p>
 * Formats that share the same key can reuse one parse result.
 *
 * @author smart-doc
 */
public final class ParseKey {

	private final boolean adoc;

	private final boolean paramsDataToTree;

	private final boolean showJavaType;

	private final String serverUrl;

	/**
	 * Creates a parse key.
	 * @param adoc whether asciidoc rendering is enabled
	 * @param paramsDataToTree whether parameters are converted to a tree
	 * @param showJavaType whether java types are shown
	 * @param serverUrl server url used to build examples
	 */
	public ParseKey(boolean adoc, boolean paramsDataToTree, boolean showJavaType, String serverUrl) {
		this.adoc = adoc;
		this.paramsDataToTree = paramsDataToTree;
		this.showJavaType = showJavaType;
		this.serverUrl = serverUrl;
	}

	/**
	 * Applies this key to the given config. Applying a key is idempotent.
	 * @param config api config
	 */
	public void applyTo(ApiConfig config) {
		config.setAdoc(adoc);
		config.setParamsDataToTree(paramsDataToTree);
		config.setShowJavaType(showJavaType);
		config.setServerUrl(serverUrl);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ParseKey)) {
			return false;
		}
		ParseKey parseKey = (ParseKey) o;
		return adoc == parseKey.adoc && paramsDataToTree == parseKey.paramsDataToTree
				&& showJavaType == parseKey.showJavaType && Objects.equals(serverUrl, parseKey.serverUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(adoc, paramsDataToTree, showJavaType, serverUrl);
	}

	@Override
	public String toString() {
		return "ParseKey{" + "adoc=" + adoc + ", paramsDataToTree=" + paramsDataToTree + ", showJavaType="
				+ showJavaType + ", serverUrl='" + serverUrl + '\'' + '}';
	}

}
