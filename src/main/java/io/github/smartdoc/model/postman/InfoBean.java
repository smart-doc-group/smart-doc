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
package io.github.smartdoc.model.postman;

import io.github.smartdoc.generation.RunClock;
import com.power.common.util.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author xingzi
 */
public class InfoBean {

	String schema;

	private String _postman_id;

	private String name;

	public InfoBean(String name) {
		this(name, false);
	}

	/**
	 * Creates a postman info bean.
	 * @param name collection name
	 * @param reproducible whether the collection id and default name must be reproducible
	 */
	public InfoBean(String name, boolean reproducible) {
		if (StringUtils.isBlank(name)) {
			this.name = "smart-doc    " + DateTimeUtil.long2Str(RunClock.now(), DateTimeUtil.DATE_FORMAT_SECOND);
		}
		else {
			this.name = name;
		}
		this.schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";
		if (reproducible) {
			this._postman_id = UUID.nameUUIDFromBytes((this.name + this.schema).getBytes(StandardCharsets.UTF_8))
				.toString();
		}
		else {
			this._postman_id = UUID.randomUUID().toString();
		}
	}

}
