package io.github.smartdoc.function;

import org.beetl.core.Context;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlEscapeTest {

	/*
	 * Since context is not used in the function, we can set it to null.
	 */
	private static final Context context = null;

	@ParameterizedTest
	@MethodSource("provideNormalTestCases")
	void testHtmlEscape(String input, String expected) {
		HtmlEscape htmlEscape = new HtmlEscape();

		String result = htmlEscape.call(new Object[] { input }, context);

		assertEquals(expected, result);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void testHtmlEscapeNullAndEmptyString(Object[] input) {
		HtmlEscape htmlEscape = new HtmlEscape();

		String result = htmlEscape.call(input, context);

		assertEquals("", result);
	}

	/**
	 * Provides test cases for normal inputs.
	 * <p>
	 * Javadoc comments are HTML, so the function keeps the markup and the special
	 * characters as they are; only paragraph tags are stripped and line breaks are
	 * replaced by a break tag.
	 * @return a stream of arguments for testing
	 */
	static Stream<Arguments> provideNormalTestCases() {
		return Stream.of(Arguments.of("<p>", ""), Arguments.of("<p>ab</p>abc", "ab abc"), Arguments.of("</p>", ""),
				Arguments.of("&", "&"), Arguments.of("\"", "\""),
				Arguments.of("<a href=\"x\">link</a>", "<a href=\"x\">link</a>"),
				Arguments.of("<p>Hello & \"World\"</p>", "Hello & \"World\" "), Arguments.of("", ""),
				Arguments.of(null, ""));
	}

}