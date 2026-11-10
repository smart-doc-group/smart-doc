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

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that the goal names declared by {@link DocGoal} and the family lists stay
 * consistent, so that a goal added for the plugin cannot be forgotten in the diagnostics.
 *
 * @author linwumingshi
 */
class DocGoalTest {

	/**
	 * Every family list, the union of them must cover exactly the declared constants.
	 */
	private static final List<List<String>> FAMILY_GOALS = Arrays.asList(DocGoal.REST_GOALS, DocGoal.RPC_GOALS,
			DocGoal.GRPC_GOALS, DocGoal.JAVADOC_GOALS, DocGoal.WEBSOCKET_GOALS);

	@Test
	void everyGoalConstantBelongsToExactlyOneFamily() throws IllegalAccessException {
		Map<String, Integer> occurrences = new HashMap<>();
		for (List<String> family : FAMILY_GOALS) {
			for (String goal : family) {
				occurrences.merge(goal, 1, Integer::sum);
			}
		}
		List<String> declared = declaredGoals();
		for (String goal : declared) {
			assertEquals(1, occurrences.getOrDefault(goal, 0), "goal must be listed by exactly one family: " + goal);
		}
		Set<String> declaredSet = new HashSet<>(declared);
		for (String goal : occurrences.keySet()) {
			assertTrue(declaredSet.contains(goal), "family lists an undeclared goal: " + goal);
		}
	}

	@Test
	void familyListsContainNoDuplicates() {
		for (List<String> family : FAMILY_GOALS) {
			assertEquals(family.size(), new HashSet<>(family).size(), "family list contains duplicates: " + family);
		}
	}

	@Test
	void goalNamesAreLowerCaseKebabCase() throws IllegalAccessException {
		for (String goal : declaredGoals()) {
			assertTrue(goal.matches("[a-z]+(-[a-z]+)*"), "unexpected goal name style: " + goal);
		}
	}

	@Test
	void familyListsAreImmutable() {
		for (List<String> family : FAMILY_GOALS) {
			assertThrows(UnsupportedOperationException.class, () -> family.add("unexpected"));
		}
	}

	/**
	 * Collects the values of all string constants declared by {@link DocGoal}.
	 * @return declared goal names
	 * @throws IllegalAccessException when a constant cannot be read
	 */
	private static List<String> declaredGoals() throws IllegalAccessException {
		List<String> goals = new ArrayList<>();
		for (Field field : DocGoal.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
				goals.add((String) field.get(null));
			}
		}
		return goals;
	}

}
