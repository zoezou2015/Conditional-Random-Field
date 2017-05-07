package com.crf.run;

import java.util.List;
import java.util.Set;

import com.utilities.TaggedToken;

/**
 *  A factory which creates a {@link CRFFeatureGenerator}.
 * @author 1001937
 *
 * @param <K>
 * @param <G>
 */
public interface CRFFeatureGeneratorFactory <K, G> {
	/**
	 * Create the {@link CRFFeatureGenerator}.
	 * @param corpus
	 * @param tags
	 * @return
	 */
	public CRFFeatureGeneratorFactory<K, G> create (Iterable<? extends List<? extends TaggedToken<K, G>>> corpus, Set<G>tags);
	
}
