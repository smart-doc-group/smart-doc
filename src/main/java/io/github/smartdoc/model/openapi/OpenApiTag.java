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

import io.github.smartdoc.constants.OpenApiTagNameTypeEnum;
import io.github.smartdoc.model.ApiDoc;

import java.util.Objects;

/**
 * open api tag
 *
 * @author <a href="mailto:cqmike0315@gmail.com">chenqi</a>
 * @version 1.0
 */
public class OpenApiTag {

	/**
	 * the tag name
	 */
	private String name;

	/**
	 * the tag description
	 */
	private String description;

	/**
	 * Creates an empty tag.
	 */
	public OpenApiTag() {
	}

	/**
	 * Creates a tag with the given name and description.
	 * @param name tag name
	 * @param description tag description
	 */
	public OpenApiTag(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * create open api tag
	 * @param name tag name
	 * @param description tag description
	 * @return OpenApiTag
	 */
	public static OpenApiTag of(String name, String description) {
		return new OpenApiTag(name, description);
	}

	/**
	 * create open api tag
	 * @param openApiTagNameType open api tag name type
	 * @param apiDoc api doc
	 * @return OpenApiTag
	 */
	public static OpenApiTag of(OpenApiTagNameTypeEnum openApiTagNameType, ApiDoc apiDoc) {
		if (Objects.isNull(openApiTagNameType)) {
			return of(apiDoc.getName(), apiDoc.getDesc());
		}
		switch (openApiTagNameType) {
			case DESCRIPTION:
				return new OpenApiTag(apiDoc.getDesc(), apiDoc.getDesc());
			case PACKAGE_NAME:
				return new OpenApiTag(apiDoc.getPackageName(), apiDoc.getDesc());
			default:
				return of(apiDoc.getName(), apiDoc.getDesc());
		}
	}

	/**
	 * Returns the tag name.
	 * @return tag name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the tag name.
	 * @param name tag name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the tag description.
	 * @return tag description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the tag description.
	 * @param description tag description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Compares this tag with another object.
	 * @param o object to compare
	 * @return true when name and description are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		OpenApiTag that = (OpenApiTag) o;
		return Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription());
	}

	/**
	 * Returns the hash code of the tag.
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getName(), getDescription());
	}

}
