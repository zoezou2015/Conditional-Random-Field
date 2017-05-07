package com.crf.filters;

import java.io.Serializable;
import java.util.Set;

/**
 * Create a set of features for the given input. 
 * "Input" is the sequence of tokens, the token-index, its tag, and the tag of the preceding token.
 * @author ZOE  
 *
 * @param <K> type of tokens
 * @param <G> type of tags
 */

public interface FilterFactory <K, G> extends Serializable{
	/**
	 * Creates a set of filters for the given token, tag, and tag of previous token. 
	 * The convention is as follows:
	 * Let each feature f be a feature that <B>might</B> return non-zero for the given token, tag, previous-tag
	 * That feature is encapsulated with a {@link Filter} in a {@link CRFFilteredFeature}. Let's call this filter "t"
	 * For that filter there exist one filter in the set returned by this function, name it "t'", such that "t'" equals to "t".
	 * 
	 * 
	 * @param sequence A sequence of tokens
	 * @param tokenIndex An index of a token in the sequence
	 * @param currentTag A tag for that token
	 * @param previousTag A tag for the token which immediately precedes that token
	 * @return A set of filters as described above
	 */
	public Set<Filter<K, G>> createFilters(K[] sequence, int tokenIndex, G currentTag, G previousTag);
}
