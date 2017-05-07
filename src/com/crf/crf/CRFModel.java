package com.crf.crf;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.crf.filters.CRFFeaturesAndFilters;



/**
 * This class encapsulates the set of all possible tags, the list of features(f_i), and the list of parameters(\theta_i)
 * 
 * @author Zoe Zou
 * @param <K> token type - must implement equals() and hashCode()
 * @param <G> tag type - must implement equals() and hashCode()
 */


public class CRFModel <K, G> {   // K = token, G = tag
	
	private final CRFTags<G> crfTags;
	private final CRFFeaturesAndFilters<K, G> features;
	private final ArrayList<BigDecimal> parameters;
	
	public CRFModel(CRFTags <G> crfTags,CRFFeaturesAndFilters<K, G>features, ArrayList<BigDecimal> parameters ){
		super();
		this.features = features;
		this.crfTags = crfTags;
		this.parameters = parameters;
	}
		
	public CRFTags<G> getCrfTags()
	{
		return crfTags;
	}
	public CRFFeaturesAndFilters<K, G> getFeatures()
	{
		return features;
	}
	public ArrayList<BigDecimal> getParameters()
	{
		return parameters;
	}
	
}
