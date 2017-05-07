package com.postagging.postagger.crf.features;

import com.crf.run.CRFFeatureGenerator;
import com.utilities.TaggedToken;

import java.awt.event.MouseWheelEvent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.crf.filters.CRFFilteredFeature;
import com.crf.filters.Filter;
import com.crf.filters.TwoTagsFilter;

/**
 * Generates the standard set of CRF features for part-of-speech tagging.
 * This standard set is:
 * <OL>
 * <LI>For each token and tag - a feature that models that token is assigned that tag.</LI>
 * <LI>For each tag that follows a preceding tag - a feature that models this tag transition.</LI>
 * </OL>
 * @author 1001937
 *
 */
public class StandardFeatureGenerator extends CRFFeatureGenerator<String, String>{

	protected Set<CRFFilteredFeature<String, String>> setFilteredFeatures = null;
	
	public StandardFeatureGenerator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus, Set<String> tags) {
		super(corpus, tags);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void generateFeatures() {
		setFilteredFeatures = new LinkedHashSet<CRFFilteredFeature<String, String>>();
		
	}
	
	@Override
	public Set<CRFFilteredFeature<String, String>> getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addTokenAndTagFeatures(){
		for(List<? extends TaggedToken<String, String>> sentence : this.corpus){
			for(TaggedToken<String, String> taggedToken: sentence){
				setFilteredFeatures.add(new CRFFilteredFeature<String, String>(
						new CaseInsensitiveTokenAndTagFeature(taggedToken.getToken(), taggedToken.getTag()), 
						new CaseInsensitiveTokenAndTagFilter(taggedToken.getToken(), taggedToken.getTag()), 
						true));
			}
		}
	}
	
	public void addTagTransitionFeatures(){
		for(String tag : tags){
			setFilteredFeatures.add(
					new CRFFilteredFeature<String, String>(new TagTransitionFeature(null, tag),
					new TwoTagsFilter<String, String>(tag, null), true));
			for(String previousTag : tags){
				setFilteredFeatures.add(new CRFFilteredFeature<String, String>(new TagTransitionFeature(previousTag, tag), 
						new TwoTagsFilter<String, String>(tag, previousTag), true));
			}
		}
	}
}
