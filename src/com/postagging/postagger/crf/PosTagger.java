
package com.postagging.postagger.crf;

import java.util.List;

import com.utilities.TaggedToken;

/**
 * Assigns part-of-speech tags for a given sentence.
 * @author 1001937
 *
 */
public interface PosTagger {
	
	/**
	 * Assigns tags for each token in the given sentence.
	 * @param sentence An input sentence, given as a list of tokens.
	 * @return The tagged sentence.
	 */
	public List<TaggedToken<String, String>> tagSentence(List<String> sentence);
}
