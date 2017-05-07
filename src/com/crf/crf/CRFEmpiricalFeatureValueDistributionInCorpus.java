package com.crf.crf;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.crf.filters.CRFFeaturesAndFilters;
import com.crf.filters.CRFFilteredFeature;
import com.utilities.CRFException;
import com.utilities.TaggedToken;
import static com.utilities.ArithmeticUtilities.*;

/**
 * Calculates the sum of all feature-values over the whole corpus.
 * @author 1001937
 *
 * @param <K>
 * @param <G>
 */
public class CRFEmpiricalFeatureValueDistributionInCorpus<K,G> {
	
	private final Iterator<? extends List<? extends TaggedToken<K, G>>> corpusIterator;
	private final CRFFeaturesAndFilters<K, G> features;

	private BigDecimal[] empiricalFeatureValue = null;
	
	public CRFEmpiricalFeatureValueDistributionInCorpus(
			Iterator<? extends List<? extends TaggedToken<K, G>>> corpusIterator,
					CRFFeaturesAndFilters<K, G> features)
	{
		super();
		this.corpusIterator = corpusIterator;
		this.features = features;
	}



	public void calculate()
	{
		empiricalFeatureValue = new BigDecimal[features.getFilteredFeatures().length];
		for (int i=0;i<empiricalFeatureValue.length;++i) {empiricalFeatureValue[i]=BigDecimal.ZERO;}
		
		while (corpusIterator.hasNext())
		{
			List<? extends TaggedToken<K, G>> sentence = corpusIterator.next();
			K[] sentenceAsArray = CRFUtilities.extractSentence(sentence);
			int tokenIndex=0;
			G previousTag = null;
			for (TaggedToken<K, G> token : sentence)
			{
				Set<Integer> activeFeatureIndexes = CRFUtilities.getActiveFeatureIndexes(features,sentenceAsArray,tokenIndex,token.getTag(),previousTag);
				for (int index : activeFeatureIndexes)
				{
					CRFFilteredFeature<K, G> filteredFeature = features.getFilteredFeatures()[index];
					double featureValue = 0.0;
					if (filteredFeature.isWhenNotFilteredIsAlwaysOne())
					{
						featureValue = 1.0;
					}
					else
					{
						featureValue = filteredFeature.getFeature().value(sentenceAsArray,tokenIndex,token.getTag(),previousTag);
					}
					empiricalFeatureValue[index] = safeAdd(empiricalFeatureValue[index], big(featureValue));
				}
				
				++tokenIndex;
				previousTag = token.getTag();
			}
			if (tokenIndex!=sentence.size()) {throw new CRFException("BUG");}
		}
	}
	
	

	public BigDecimal[] getEmpiricalFeatureValue()
	{
		if (null==empiricalFeatureValue) {throw new CRFException("Not calculated.");}
		return empiricalFeatureValue;
	}
	
}
