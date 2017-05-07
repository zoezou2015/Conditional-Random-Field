package com.postagging.postagger.crf;

import java.util.List;
import java.util.Set;


import com.crf.run.CRFTrainer;
import com.crf.run.CRFTrainerFactory;
import com.postagging.postagger.crf.features.StandardFeatureGenerator;
import com.postagging.postagger.crf.features.StandardFilterFactory;
import com.utilities.TaggedToken;

public class CRFPosTaggerTrainerFactory {
	/**
	 * Creates a {@link CrfPosTaggerTrainer} for a given corpus.
	 * 
	 * @param corpus the corpus is an list of sentences, where each sentence is a list of tokens, each tagged with a POS-tag.
	 * @return
	 */
	public CRFPosTaggerTrainer createTrainer(List<List<? extends TaggedToken<String, String>>> corpus)
	{
		CRFTrainerFactory<String, String> factory = new CRFTrainerFactory<String, String>();
		CRFTrainer<String, String> crfTrainer = factory.createTrainer(corpus,
				(Iterable<? extends List<? extends TaggedToken<String, String>>> theCorpus, Set<String> tags) -> new StandardFeatureGenerator(theCorpus, tags),
				new StandardFilterFactory());
		CRFPosTaggerTrainer trainer = new CRFPosTaggerTrainer(crfTrainer);

		return trainer;
	}
}
