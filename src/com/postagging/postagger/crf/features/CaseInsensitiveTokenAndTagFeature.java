package com.postagging.postagger.crf.features;

import static com.utilities.MiscellaneousUtilities.*;

import com.crf.crf.CRFFeature;

/**
 * A CRF feature the models that a certain token is assigned a certain tag.
 * The token is considered in a case-insensitivity manner, i.e., "AbC" is considered equal to "abc".
 * @author 1001937
 *
 */
public class CaseInsensitiveTokenAndTagFeature extends CRFFeature<String, String> {
	
	private final String tokenLowerCase;
	private final String tag;
	
	public CaseInsensitiveTokenAndTagFeature(String token, String tag){
		super();
		this.tokenLowerCase = ((token==null)? null: token.toLowerCase());
		this.tag = tag;
	}
	
	@Override
	public double value(String[] sequence, int indexInSequence, String currentTag, String previousTag) {
		String tokenInSentence_lowCase = sequence[indexInSequence];
		if(tokenInSentence_lowCase != null){
			tokenInSentence_lowCase = tokenInSentence_lowCase.toLowerCase();
		}
		double ret = 0.0;
		if(equalObj(tokenInSentence_lowCase, this.tokenLowerCase) && equalObj(currentTag, this.tag)){
			ret = 1.0;
		}
		return ret;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result
				+ ((tokenLowerCase == null) ? 0 : tokenLowerCase.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaseInsensitiveTokenAndTagFeature other = (CaseInsensitiveTokenAndTagFeature) obj;
		if (tag == null)
		{
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (tokenLowerCase == null)
		{
			if (other.tokenLowerCase != null)
				return false;
		} else if (!tokenLowerCase.equals(other.tokenLowerCase))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "CaseInsensitiveTokenAndTagFeature [tokenLowerCase="
				+ tokenLowerCase + ", tag=" + tag + "]";
	}
	

}
