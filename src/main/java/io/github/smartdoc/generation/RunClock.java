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

/**
 * Clock shared by all renderers of one documentation generation run.
 * <p>
 * The clock is captured once per run so that every generated file uses the same
 * timestamp, and so that long-lived JVMs (Maven sessions, Gradle daemons) do not reuse
 * the timestamp of a previous run.
 *
 * @author smart-doc
 */
public final class RunClock {

	/**
	 * Fixed timestamp used by reproducible generation (2024-01-01T00:00:00Z).
	 */
	public static final long REPRODUCIBLE_TIMESTAMP = 1704067200000L;

	private static final ThreadLocal<State> STATE = new ThreadLocal<>();

	private RunClock() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Starts an explicit run with the current time. An explicit run is not reset by
	 * {@link #beginLegacyRunIfNeeded()}.
	 */
	public static void beginRun() {
		STATE.set(new State(System.currentTimeMillis(), true));
	}

	/**
	 * Starts an explicit run with a fixed time. Intended for reproducible generation and
	 * tests.
	 * @param now timestamp in milliseconds
	 */
	public static void beginRun(long now) {
		STATE.set(new State(now, true));
	}

	/**
	 * Ends the current run and removes all timestamp state from the current thread.
	 */
	public static void endRun() {
		STATE.remove();
	}

	/**
	 * Starts a legacy single-format run if no explicit run is active. Each legacy entry
	 * point calls this so that repeated builds in the same JVM get a fresh timestamp.
	 */
	public static void beginLegacyRunIfNeeded() {
		beginLegacyRunIfNeeded(System.currentTimeMillis());
	}

	/**
	 * Starts a legacy single-format run with the given time if no explicit run is active.
	 * @param now timestamp in milliseconds
	 */
	public static void beginLegacyRunIfNeeded(long now) {
		State state = STATE.get();
		if (state == null || !state.explicit) {
			STATE.set(new State(now, false));
		}
	}

	/**
	 * Returns whether a generation run is active on the current thread.
	 * @return true when a run is active
	 */
	public static boolean isRunActive() {
		return STATE.get() != null;
	}

	/**
	 * Returns the timestamp of the current run, or the current time when no run is
	 * active.
	 * @return timestamp in milliseconds
	 */
	public static long now() {
		State state = STATE.get();
		return state == null ? System.currentTimeMillis() : state.now;
	}

	/**
	 * Run state.
	 */
	private static final class State {

		private final long now;

		private final boolean explicit;

		private State(long now, boolean explicit) {
			this.now = now;
			this.explicit = explicit;
		}

	}

}
