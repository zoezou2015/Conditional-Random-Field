package com.crf.run;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.crf.crf.CRFTags;
import com.crf.crf.CRFUtilities;
import com.utilities.CRFException;
import com.utilities.TaggedToken;

/**
 * Finds the set of tags in the given corpus, and which tag can follow which tag.
 * @author 1001937
 *
 * @param <G>
 */
public class CRFTagsBuilder<G> {
	public CRFTagsBuilder(Iterable<? extends List<? extends TaggedToken<?, G>>> corpus)
	{
		super();
		this.corpus = corpus;
	}



	public void build()
	{ 
		Set<G> tags = new LinkedHashSet<G>();
		Map<G, Set<G>> canPrecede = new LinkedHashMap<G, Set<G>>();
		Map<G, Set<G>> canFollow = new LinkedHashMap<G, Set<G>>();
		
		for (List<? extends TaggedToken<?, G> > sentence : corpus)
		{
			G previousTag = null;
			for (TaggedToken<?, G> taggedToken : sentence)
			{
				G tag = taggedToken.getTag();
				tags.add(tag);
				CRFUtilities.putInMapSet(canPrecede, tag, previousTag);
				CRFUtilities.putInMapSet(canFollow, previousTag, tag);
				
				previousTag = tag;
			}
		}
		addEmptySets(canPrecede,tags);
		addEmptySets(canFollow,tags);
		
		crfTags = new CRFTags<G>(tags, canFollow, canPrecede);
		
		if (logger.isDebugEnabled())
		{
			int pairs = 0;
			for (G tag : canFollow.keySet())
			{
				pairs += canFollow.get(tag).size();
			}
			logger.debug("Number of tag-pairs detected in corpus (including null) = " + pairs + ".");
			
		}
	}
	
	

	public CRFTags<G> getCrfTags()
	{
		if (null==crfTags) {throw new CRFException("Not yet built.");}
		return crfTags;
	}
	
	
	private void addEmptySets(Map<G, Set<G>> map, final Set<G> allTags)
	{
		for (G tag : allTags)
		{
			if (!(map.containsKey(tag)))
			{
				map.put(tag, (Set<G>)Collections.emptySet());
			}
		}
		
	}


	private final Iterable<? extends List<? extends TaggedToken<?, G>>> corpus;
	private CRFTags<G> crfTags = null;
	
	private static final Logger logger = Logger.getLogger(CRFTagsBuilder.class);
}
