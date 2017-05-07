package com.crf.run;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.crf.crf.CRFTags;
import com.crf.crf.CRFUtilities;
import com.crf.filters.CRFFeaturesAndFilters;
import com.crf.filters.CRFFilteredFeature;
import com.crf.filters.Filter;
import com.crf.filters.FilterFactory;
import com.utilities.CRFException;
import com.utilities.TaggedToken;

/**
 * A factory which generates a new CRF trainer.
 * @author 1001937
 *
 * @param <K>
 * @param <G>
 */
public class CRFTrainerFactory<K,G>
{
	private Double sigmaSquare_inverseRegularizationFactor = null;

	private static final Logger logger = Logger.getLogger(CRFTrainerFactory.class);
	
	/**
	 * Optional setter for regularization factor. If this method was not called, the default regularization factor is used.
	 * <br>
	 * The regularization factor is the denominator of the regularization part of the likelihood function. When regularization
	 * is used, an element (\Sum_{i=0}^{number-of-features}\theta_i)/(\sigma^2) is added to the likelihood function (where
	 * \theta_i is parameter number i).
	 * See {@link CrfLogLikelihoodFunction}.
	 * 
	 * @param sigmaSquare_inverseRegularizationFactor regularization factor. 
	 */
	public void setRegularizationSigmaSquareFactor(double sigmaSquare_inverseRegularizationFactor)
	{
		this.sigmaSquare_inverseRegularizationFactor = sigmaSquare_inverseRegularizationFactor;
	}
	
	/**
	 * Creates a CRF trainer.<BR>
	 * <B>The given corpus must reside completely in the internal memory. Not in disk/data-base etc.</B>
	 * 
	 * @param corpus The corpus: a list a tagged sequences. Must reside completely in memory.
	 * @param featureGeneratorFactory A factory which creates a feature-generator (the feature-generator creates a set of features)
	 * @param filterFactory The {@link FilterFactory} <B>that corresponds to the feature-generator.</B>
	 * 
	 * @return a CRF trainer.
	 */
	public CRFTrainer<K,G> createTrainer(List<List<? extends TaggedToken<K, G> >> corpus, CRFFeatureGeneratorFactory<K,G> featureGeneratorFactory, FilterFactory<K, G> filterFactory)
	{
		logger.info("Extracting tags.");
		CRFTagsBuilder<G> tagsBuilder = new CRFTagsBuilder<G>(corpus);
		tagsBuilder.build();
		CRFTags<G> crfTags = tagsBuilder.getCrfTags();
		
		logger.info("Generating features.");
		CRFFeatureGenerator<K,G> featureGenerator = featureGeneratorFactory.create(corpus, crfTags.getTags());
		featureGenerator.generateFeatures();
		Set<CRFFilteredFeature<K, G>> setFilteredFeatures = featureGenerator.getFeatures();
		CRFFeaturesAndFilters<K, G> features = createFeaturesAndFiltersObjectFromSetOfFeatures(setFilteredFeatures, filterFactory);
		
		logger.info("CrfPosTaggerTrainer has been created.");
		if (null == this.sigmaSquare_inverseRegularizationFactor) // use default
		{
			return new CRFTrainer<K,G>(features,crfTags);
		}
		else // use the value set by setRegularizationSigmaSquareFactor().
		{
			return new CRFTrainer<K,G>(features,crfTags, (this.sigmaSquare_inverseRegularizationFactor!=null)?true:false, this.sigmaSquare_inverseRegularizationFactor);
		}
	}
	
	
	
	
	private static <K,G> CRFFeaturesAndFilters<K, G> createFeaturesAndFiltersObjectFromSetOfFeatures(Set<CRFFilteredFeature<K, G>> setFilteredFeatures, FilterFactory<K, G> filterFactory)
	{
		if (setFilteredFeatures.size()<=0) throw new CRFException("No features have been generated.");
		@SuppressWarnings("unchecked")
		CRFFilteredFeature<K,G>[] featuresAsArray = (CRFFilteredFeature<K,G>[]) Array.newInstance(setFilteredFeatures.iterator().next().getClass(), setFilteredFeatures.size()); // new CrfFilteredFeature<String,String>[setFilteredFeatures.size()];
		Iterator<CRFFilteredFeature<K,G>> filteredFeatureIterator = setFilteredFeatures.iterator();
		for (int index=0;index<featuresAsArray.length;++index)
		{
			if (!filteredFeatureIterator.hasNext()) {throw new CRFException("BUG");}
			CRFFilteredFeature<K,G> filteredFeature = filteredFeatureIterator.next();
			featuresAsArray[index] = filteredFeature;
		}
		if (filteredFeatureIterator.hasNext()) {throw new CRFException("BUG");}
		
		
		Set<Integer> indexesOfFeaturesWithNoFilter = new LinkedHashSet<Integer>();
		Map<Filter<K, G>, Set<Integer>> mapActiveFeatures = new LinkedHashMap<Filter<K, G>, Set<Integer>>();
		for (int index=0;index<featuresAsArray.length;++index)
		{
			CRFFilteredFeature<K, G> filteredFeature = featuresAsArray[index];
			Filter<K, G> filter = filteredFeature.getFilter();
			if (filter!=null)
			{
				CRFUtilities.putInMapSet(mapActiveFeatures, filter, index);
			}
			else
			{
				indexesOfFeaturesWithNoFilter.add(index);
			}
		}
		
		CRFFeaturesAndFilters<K, G> allFeatures = new CRFFeaturesAndFilters<K, G>(
				filterFactory,
				featuresAsArray,
				mapActiveFeatures,
				indexesOfFeaturesWithNoFilter
				);
		
		return allFeatures;
	}


	
	
}
