package com.crf.crf;

import java.io.Serializable;

/**
 * A CRF feature.
 * In CRF, a feature is a function that has the input(x, j, s, s') where: x is the sentence(sequence),
 * j is a position in that sequence, s is the tag of the token j, and s' is the tag of the token j-1.
 * <P>
 * {@link CRFFeature} must implement {@link #equals(Object)} and {@link #hasCode()}.
 * 
 * @author Zoe
 *
 * @param <K> type of tokens
 * @param <G> type of tags
 */

public abstract class CRFFeature<K, G> implements Serializable {
	
	public abstract double value(K[] sequence, int indexInSequence, G currentTag, G previousTag);
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	
}
