package com.crf.crf;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

//import static com.crf.crf.CRFUtilities.*;

/**
 * Holds, for a given sentence, the CRF formula value for each token-index and every pair of tags (for the current token and the
 * preceding token).
 * @author 1001937
 *
 * @param <K>
 * @param <G>
 */
public class CRFPsi_FormulaAllTokens<K,G> {
	
	private final CRFModel<K,G> model;
	private final K[] sentence;
	private final CRFRememberActiveFeatures<K, G> activeFeaturesForSentence;
	
	private Map<G, Map<G, BigDecimal>>[] allPsiValues;
	
	public static <K,G>  CRFPsi_FormulaAllTokens<K,G>  createAndCalculate(CRFModel<K, G> model, K[] sentence, CRFRememberActiveFeatures<K, G> activeFeaturesForSentence) {
		CRFPsi_FormulaAllTokens<K,G> ret = new CRFPsi_FormulaAllTokens<K,G>(model,sentence,activeFeaturesForSentence);
		ret.calculateFormulasForAllTokens();
		return ret;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public  CRFPsi_FormulaAllTokens (CRFModel<K,G> model, K[] sentence, CRFRememberActiveFeatures<K, G> activeFeaturesForSentence){
		super();
		this.model = model;
		this.sentence = sentence;
		this.activeFeaturesForSentence = activeFeaturesForSentence;
	}
	
	public void calculateFormulasForAllTokens(){
		for(int tokenIndex=0; tokenIndex<this.sentence.length;tokenIndex++){
			for(G currentTag : this.model.getCrfTags().getTags()){
				Set<G> possiblePreviousTags = CRFUtilities.getPreviousTags(sentence, tokenIndex, currentTag, model.getCrfTags());
				for (G previousTag : possiblePreviousTags)
				{
					Set<Integer> activeFeatures = activeFeaturesForSentence.getOneTokenActiveFeatures(tokenIndex, currentTag, previousTag);
					BigDecimal value = CRFUtilities.oneTokenFormula(model,sentence,tokenIndex,currentTag,previousTag,activeFeatures);
					put(tokenIndex,currentTag,previousTag,value);
				}
			}
		}
	}
	
	public BigDecimal getOneTokenFormula(int tokenIndex, G currentTag, G previousTag){
		return allPsiValues[tokenIndex].get(currentTag).get(previousTag);
	}
	
	private void put(int tokenIndex, G currentTag, G previousTag, BigDecimal value){
		Map<G, Map<G, BigDecimal>> mapForToken = allPsiValues[tokenIndex];
		if (null==mapForToken)
		{
			mapForToken = new LinkedHashMap<G, Map<G,BigDecimal>>();
			allPsiValues[tokenIndex] = mapForToken;
		}
		
		Map<G, BigDecimal> mapForCurrentTag = mapForToken.get(currentTag);
		if (null==mapForCurrentTag)
		{
			mapForCurrentTag = new LinkedHashMap<G, BigDecimal>();
			mapForToken.put(currentTag, mapForCurrentTag);
		}
		
		mapForCurrentTag.put(previousTag, value);
	}
	
}
