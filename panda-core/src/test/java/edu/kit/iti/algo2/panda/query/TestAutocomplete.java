package edu.kit.iti.algo2.panda.query;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestAutocomplete {

	@Test
	public void test() {
		Autocomplete autocomplete = new Autocomplete();
		autocomplete.addWord("abc");
		autocomplete.addWord("abz");
		autocomplete.addWord("acc");
		List<String> actual = autocomplete.completeWord("ab");
		List<String> expected = Arrays.asList("abc", "abz");
		assertEquals(expected, actual);
	}

}
