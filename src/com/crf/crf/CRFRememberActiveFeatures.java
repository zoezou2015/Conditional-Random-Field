package com.crf.crf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.crf.filters.CRFFeaturesAndFilters;

/**
 * Holds set of features for every token, and every pair of tags (for this token and preceding token) 
 * in the given input.
 * @author 1001937
 *
 * @param <K>
 * @param <G>
 */
public class CRFRememberActiveFeatures <K,G>{
	
	private final CRFTags<G> crfTags;
	private final CRFFeaturesAndFilters<K, G> features;
	private final K[] sentence;

	private Map<G, Map<G, Set<Integer> >>[] allTokensAndTagsActiveFeatures;
	
	/**
	 * Creates an instance of {@link CRFRememberActiveFeatures} for the given sentence, calls its {@link #findActiveFeaturesForAllTokens()}
	 * method, and returns it. With the returned object, the method {@link #getOneTokenActiveFeatures(int, Object, Object)} can be
	 * used to retrieve active features for any token/tag/tag-of-previous in the sentence.
	 * 
	 * @param features
	 * @param crfTags
	 * @param sentence
	 * @return
	 */
	public static <K, G> CRFRememberActiveFeatures<K, G> findForSentence(CRFFeaturesAndFilters<K, G> features, CRFTags<G> crfTags, K[] sentence){
		CRFRememberActiveFeatures<K, G> ret = new CRFRememberActiveFeatures<K,G>(features, crfTags, sentence);
		ret.findActiveFeaturesForAllTokens();
		return ret;
	}
	
	
	/**
	 * Constructor for a given sentence.
	 * 
	 * @param features
	 * @param crfTags
	 * @param sentence
	 */
	public CRFRememberActiveFeatures(CRFFeaturesAndFilters<K, G> features, CRFTags<G> crfTags, K[] sentence) {
		super();
		this.features = features;
		this.crfTags = crfTags;
		this.sentence = sentence;
		
		allTokensAndTagsActiveFeatures = (Map<G, Map<G, Set<Integer>>>[]) new Map[sentence.length];
	}
	
	/**
	 * Finds all the active features for every triple of token/tag/tag-of-previous. Then, the method
	 * {@link #getOneTokenActiveFeatures(int, Object, Object)} can be used.
	 */
	public void findActiveFeaturesForAllTokens(){
		for(int tokenIndex=0; tokenIndex<this.sentence.length;tokenIndex++){
			for(G currentTag : this.crfTags.getTags()){
				Set<G> possiblePreviousTag = CRFUtilities.getPreviousTags(sentence, tokenIndex, currentTag, crfTags);
				for(G previousTag : possiblePreviousTag){
					Set<Integer> activeFeatures = CRFUtilities.getActiveFeatureIndexes(features, sentence, tokenIndex, currentTag, previousTag);
					putActiveFeatures(tokenIndex, currentTag, previousTag, activeFeatures);
				}
			}
		}
	}
	
	
	public Set<Integer> getOneTokenActiveFeatures(int tokenIndex, G currentTag, G previousTag){
		return allTokensAndTagsActiveFeatures[tokenIndex].get(currentTag).get(previousTag);
	}
	
	private void putActiveFeatures(int tokenIndex, G currentTag, G previousTag, Set<Integer> activeFeatures){
		Map<G, Map<G, Set<Integer>>> mapForToken = allTokensAndTagsActiveFeatures[tokenIndex];
		if(mapForToken == null){
			mapForToken = new LinkedHashMap<G, Map<G, Set<Integer>>>();
			allTokensAndTagsActiveFeatures[tokenIndex] = mapForToken;
		}
		
		Map<G, Set<Integer>> mapForCurrentTag = mapForToken.get(currentTag);
		if(mapForCurrentTag == null){
			mapForCurrentTag = new LinkedHashMap<G, Set<Integer>>();
			mapForToken.put(currentTag, mapForCurrentTag);
		}
		
		mapForCurrentTag.put(previousTag, activeFeatures);
	}
	
	
	
	
}
