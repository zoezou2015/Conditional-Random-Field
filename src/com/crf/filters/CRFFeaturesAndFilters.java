package com.crf.filters;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulate all features, the {@link FilterFactory}, and data-structures used for filtering features
 * @author Zoe Zou
 *
 * @param <K>
 * @param <G>
 */

public class CRFFeaturesAndFilters <K, G> implements Serializable{
	
	private final FilterFactory<K, G> _filterFactory;
	private final CRFFilteredFeature<K, G>[] _filteredFeatures;
	private final Map<Filter<K, G>, Set<Integer>> _mapActiveFeatures;
	private final Set<Integer> _indexesOfFeaturesWithNoFilter;
	
	public CRFFeaturesAndFilters (FilterFactory<K, G> filterFactory, 
			CRFFilteredFeature<K, G>[] filteredFeatures, Map<Filter<K, G>, Set<Integer>> mapActiveFeatures,
			Set<Integer> indexesOfFeaturesWithNoFilter){
		
		super();
		this._filteredFeatures = filteredFeatures;
		this._filterFactory = filterFactory;
		this._mapActiveFeatures = mapActiveFeatures;
		this._indexesOfFeaturesWithNoFilter = indexesOfFeaturesWithNoFilter;
	}
	
	
	/**
	 * 
	 * @return Returns the {@link FilterFactory} to be used with the features encapsulated here, 
	 * for efficient feature-value calculations. 
	 */
	public FilterFactory< K, G> getFilterFactory(){
		return this._filterFactory;
	}
	
	
	/**
	 * 
	 * @return An array of all the features in the CRF model.
	 */
	public CRFFilteredFeature<K, G>[] getFilteredFeatures(){
		return this._filteredFeatures;
	}
	
	
	/**
	 * 
	 * @return Returns a map from each filter to a set of indexes of features, that might be active if 
	 * their filters are equal to this filter. "Active" means that they might return non-zero. 
	 */
	public Map<Filter<K, G>, Set<Integer>> getMapActiveFeatures(){
		return this._mapActiveFeatures;
	}
	
	/**
	 * 
	 * @return A set of indexes of features that might be active for any type of input. These features have no filters.
	 */
	public Set<Integer> getIndexesOfFeaturesWithNoFilter(){
		return this._indexesOfFeaturesWithNoFilter;
	}
	
}
