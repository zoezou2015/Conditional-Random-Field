package com.crf.filters;

import java.io.Serializable;

/**
 * A filter determines whether it is known <B>a priori</B> that the feature value is zero
 * for a give input. "Input" is the sequence of tokens, the token-index, its tag, the tag of its preceding token.
 * <P>
 * The CRF formula e^{weight-vector*feature-vector} might be quite expensive in run-time complexity notion.
 * However, in most cases most of features return zero for the given input. Thus, there is no need to sum 
 * over all the features,
 * <BR>
 * Concretely, assume we have a {@link CRFFilteredFeature} and a {@link Filter}: if the filter {@link #equals(Object)}
 * to {@link CRFFilteredFeature#getFilter()}, then the feature <B>might</B> return non-zero value. Otherwise, it is known
 * for sure that it returns zero. 
 * <P>
 * 
 * @see CRFFilteredFeature
 * @see FilterFactory
 * @see CRFUtilities#getActiveFeatureIndexes(CRFFeaturesAndFilters, Object[], int, Object, Object)
 * 
 * 
 * @author zoe
 *
 * @param <K>
 * @param <G>
 */


public abstract class Filter<K, G> implements Serializable{
	
	
	public abstract int hashCode();
	
	public abstract boolean equals(Object obj);
}
