package com.postagging.postagger.crf.features;

import com.crf.crf.CRFFeature;

import static com.utilities.MiscellaneousUtilities.*;
/**
 * A feature that models that the given token is assigned the given tag. Tokens are considered case-sensitive, i.e.,
 * "AbC" is <B>NOT</B> equal to "abc".
 * <BR>
 * In practice, this feature is not used. Rather {@link CaseInsensitiveTokenAndTagFeature} is used. 
 * @author 1001937
 *
 */
public class TokenAndTagFeature extends CRFFeature<String, String> {
	
	private final String forToken;
	private final String forTag;
	
	public TokenAndTagFeature(String forToken, String forTag) {
		super();
		this.forTag = forTag;
		this.forToken = forToken;
	}
	
	@Override
	public double value(String[] sequence, int indexInSequence, String currentTag, String previousTag) {
		double ret = 0.0;
		if (equalObj(sequence[indexInSequence],forToken) && equalObj(currentTag,forTag))
		{
			ret = 1.0;
		}
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokenAndTagFeature other = (TokenAndTagFeature) obj;
		if (forTag == null)
		{
			if (other.forTag != null)
				return false;
		} else if (!forTag.equals(other.forTag))
			return false;
		if (forToken == null)
		{
			if (other.forToken != null)
				return false;
		} else if (!forToken.equals(other.forToken))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((forTag == null) ? 0 : forTag.hashCode());
		result = prime * result
				+ ((forToken == null) ? 0 : forToken.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return "TokenAndTagFeature [forToken=" + forToken + ", forTag="
				+ forTag + "]";
	}
	
	
	
	
}
