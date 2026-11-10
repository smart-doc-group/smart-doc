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
package io.github.smartdoc.template;

import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.IDoc;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import io.github.smartdoc.model.grpc.GrpcApiDoc;
import io.github.smartdoc.model.javadoc.JavadocApiDoc;
import io.github.smartdoc.model.rpc.RpcApiDoc;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that every built-in template declares the document type it produces.
 * <p>
 * The declaration is the contract used by
 * {@link io.github.smartdoc.generation.TemplateResolver} to reject a framework that
 * cannot serve the selected goal family.
 *
 * @author linwumingshi
 */
public class DocumentTypeContractTest {

	@Test
	void restTemplatesDeclareApiDoc() {
		assertEquals(ApiDoc.class, new SpringBootDocBuildTemplate().documentType());
		assertEquals(ApiDoc.class, new SolonDocBuildTemplate().documentType());
		assertEquals(ApiDoc.class, new JAXRSDocBuildTemplate().documentType());
	}

	@Test
	void rpcTemplatesDeclareTheirOwnDocument() {
		assertEquals(RpcApiDoc.class, new RpcDocBuildTemplate().documentType());
		assertEquals(GrpcApiDoc.class, new GRpcDocBuildTemplate().documentType());
		assertEquals(JavadocApiDoc.class, new JavadocDocBuildTemplate().documentType());
	}

	@Test
	void undeclaredDocumentTypeFallsBackToIDoc() {
		IDocBuildBaseTemplate custom = new IDocBuildBaseTemplate() {
			@Override
			public boolean supportsFramework(String framework) {
				return true;
			}

			@Override
			public FrameworkAnnotations registeredAnnotations() {
				return null;
			}

			@Override
			public boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
				return false;
			}
		};

		assertEquals(IDoc.class, custom.documentType());
	}

	@Test
	void templatesThatServeWebSocketImplementTheWebSocketInterface() {
		Class<?>[] webSocketTemplates = { SpringBootDocBuildTemplate.class, SolonDocBuildTemplate.class,
				JAXRSDocBuildTemplate.class, RpcDocBuildTemplate.class, JavadocDocBuildTemplate.class };

		for (Class<?> template : webSocketTemplates) {
			assertTrue(IWebSocketDocBuildTemplate.class.isAssignableFrom(template),
					template.getSimpleName() + " must implement IWebSocketDocBuildTemplate");
		}
		assertFalse(IWebSocketDocBuildTemplate.class.isAssignableFrom(GRpcDocBuildTemplate.class),
				"GRpcDocBuildTemplate has no WebSocket implementation");
	}

	@Test
	void webSocketDocumentTypeIsNotDeclaredByRestTemplates() {
		// A REST template declares ApiDoc even though it also serves WebSocket goals, so
		// the WebSocket family is validated through IWebSocketDocBuildTemplate instead.
		assertFalse(WebSocketDoc.class.equals(new SpringBootDocBuildTemplate().documentType()));
	}

}
