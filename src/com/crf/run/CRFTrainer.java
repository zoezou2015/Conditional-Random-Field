package com.crf.run;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.crf.crf.CRFLogLikelihoodFunction;
import com.crf.crf.CRFModel;
import com.crf.crf.CRFTags;
import com.crf.filters.CRFFeaturesAndFilters;
import com.function.DerivableFunction;
import com.function.optimization.LbfgsMinimizer;
import com.function.optimization.NegatedFunction;
import com.utilities.CRFException;
import com.utilities.MiscellaneousUtilities;
import com.utilities.StringUtilities;
import com.utilities.TaggedToken;
import static com.function.optimization.LbfgsMinimizer.*;


public class CRFTrainer<K,G>
{
	/**
	 * Change to <tt>true</tt> if you want this debug information.
	 * Note that setting to <tt>true</tt> increases run time.
	 */
	public static final boolean PRINT_DEBUG_INFO_TAG_DIFFERENCE_BETWEEN_ITERATIONS = false;

	public static final int PRETRAIN_RANDOM_SELECTION_REQUIRED_FOR = 100;
	public static final int PRETRAIN_RANDOM_SELECTION_SIZE = 50;
	
	public static final double DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR = 10.0;
	public static final boolean DEFAULT_USE_REGULARIZATION = true;

	
	private final CRFFeaturesAndFilters<K, G> features;
	private final CRFTags<G> crfTags;
	private final boolean useRegularization;
	private final double sigmaSquare_inverseRegularizationFactor;
	
	private CRFModel<K, G> learnedModel = null;

	private static final Logger logger = Logger.getLogger(CRFTrainer.class);

	
	public CRFTrainer(CRFFeaturesAndFilters<K, G> features, CRFTags<G> crfTags)
	{
		this(features,crfTags,DEFAULT_USE_REGULARIZATION,DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR);
	}

	public CRFTrainer(CRFFeaturesAndFilters<K, G> features, CRFTags<G> crfTags,
			boolean useRegularization, double sigmaSquare_inverseRegularizationFactor)
	{
		super();
		this.features = features;
		this.crfTags = crfTags;
		this.useRegularization = useRegularization;
		this.sigmaSquare_inverseRegularizationFactor = sigmaSquare_inverseRegularizationFactor;
	}


	public void train(List<? extends List<? extends TaggedToken<K, G> >> corpus)
	{
		logger.info("CRF training: Number of tags = "+crfTags.getTags().size()+". Number of features = "+features.getFilteredFeatures().length +".");
		logger.info("Creating log likelihood function.");
		
		BigDecimal[] parameters = null;
		if (corpus.size()>=PRETRAIN_RANDOM_SELECTION_REQUIRED_FOR)
		{
			logger.info("Performing pre-train, then full train.");
			List<? extends List<? extends TaggedToken<K, G> >> preTrainCorpus = MiscellaneousUtilities.selectRandomlyFromList(PRETRAIN_RANDOM_SELECTION_SIZE, corpus);
			logger.info("Performing pre-train.");
			BigDecimal[] preTrainParameters = optimizeForCorpus(preTrainCorpus, null);
			logger.info("Performing full-train.");
			parameters = optimizeForCorpus(corpus, preTrainParameters);
		}
		else
		{
			logger.info("Corpus is relatively small. No pre-train is performed. Performing full-train.");
			parameters = optimizeForCorpus(corpus, null);
		}
		
		if (parameters.length!=features.getFilteredFeatures().length) {throw new CRFException("Number of parameters, returned by LBFGS optimizer, differs from number of features.");}
		
		ArrayList<BigDecimal> parametersAsList = arrayBigDecimalToList(parameters);
		
		learnedModel = new CRFModel<K, G>(crfTags,features,parametersAsList);
		logger.info("Training of CRF - done.");
	}
	
	
	
	
	
	
	public CRFModel<K, G> getLearnedModel()
	{
		return learnedModel;
	}

	public CRFInferencePerformer<K, G> getInferencePerformer()
	{
		if (null==learnedModel) throw new CRFException("Not yet trained");
		return new CRFInferencePerformer<K,G>(learnedModel);
		
	}

	
	private BigDecimal[] optimizeFunction(DerivableFunction convexNegatedCrfFunction, BigDecimal[] initialPoint ,List<? extends List<? extends TaggedToken<K, G> >> corpus)
	{
		logger.info("Optimizing log likelihood function.");
		LbfgsMinimizer lbfgsOptimizer = new LbfgsMinimizer(convexNegatedCrfFunction);
		if (is(PRINT_DEBUG_INFO_TAG_DIFFERENCE_BETWEEN_ITERATIONS))
		{
			lbfgsOptimizer.setDebugInfo(new CrfDebugInfo(corpus));
		}
		if (initialPoint!=null)
		{
			lbfgsOptimizer.setInitialPoint(initialPoint);	
		}
		lbfgsOptimizer.find();
		BigDecimal[] parameters = lbfgsOptimizer.getPoint();
		return parameters;
	}
	
	private BigDecimal[] optimizeForCorpus(List<? extends List<? extends TaggedToken<K, G> >> corpus, BigDecimal[] initialPoint)
	{
		if (logger.isDebugEnabled()) {logger.debug("OptimizeForCorpus. Corpus size = "+corpus.size());}
		DerivableFunction convexNegatedCrfFunction = NegatedFunction.fromDerivableFunction(createLogLikelihoodFunctionConcave(corpus));
		BigDecimal[] parameters = optimizeFunction(convexNegatedCrfFunction, initialPoint, corpus);
		if (logger.isDebugEnabled()) {logger.debug("Parameters: "+StringUtilities.arrayOfBigDecimalToString(parameters));}
		return parameters;
	}
	
	
	private DerivableFunction createLogLikelihoodFunctionConcave(List<? extends List<? extends TaggedToken<K, G> >> corpus)
	{
		return new CRFLogLikelihoodFunction<K, G>(corpus,crfTags,features,useRegularization,sigmaSquare_inverseRegularizationFactor);
	}
	
	
	
	@SuppressWarnings("unused")
	private static ArrayList<Double> arrayDoubleToList(double[] array)
	{
		ArrayList<Double> ret = new ArrayList<Double>(array.length);
		for (double d : array)
		{
			ret.add(d);
		}
		return ret;
	}

	private static ArrayList<BigDecimal> arrayBigDecimalToList(BigDecimal[] array)
	{
		ArrayList<BigDecimal> ret = new ArrayList<BigDecimal>(array.length);
		for (BigDecimal d : array)
		{
			ret.add(d);
		}
		return ret;
	}


	/**
	 * Used if PRINT_DEBUG_INFO_TAG_DIFFERENCE_BETWEEN_ITERATIONS is true.
	 * Prints debug information of how many tags are equals and how many are different between iterations.
	 *
	 * <p>
	 * Date: Oct 13, 2016
	 * @author Asher Stern
	 *
	 */
	private class CrfDebugInfo implements LbfgsMinimizer.DebugInfo
	{
		public CrfDebugInfo(List<? extends List<? extends TaggedToken<K, G>>> corpus)
		{
			super();
			this.corpus = corpus;
		}

		@Override
		public String info(BigDecimal[] point)
		{
			ArrayList<BigDecimal> parametersAsList = arrayBigDecimalToList(point);
			CRFModel<K, G> crfModel = new CRFModel<K, G>(crfTags,features,parametersAsList);
			CRFInferencePerformer<K, G> crfInferencePerformer = new CRFInferencePerformer<K, G>(crfModel);
			
			List<G> tagsAllCorpus = new ArrayList<>();
			for (List<? extends TaggedToken<K, G> > taggedSequence : corpus)
			{
				ArrayList<K> sequence = new ArrayList<K>(taggedSequence.size());
				for (TaggedToken<K, G> taggedToken : taggedSequence)
				{
					sequence.add(taggedToken.getToken());
				}
				List<TaggedToken<K,G>> inferencedTaggedSequence = crfInferencePerformer.tagSequence(sequence);
				for (TaggedToken<K, G> taggedToken : inferencedTaggedSequence)
				{
					tagsAllCorpus.add(taggedToken.getTag());
				}
			}
			String ret = null;
			if (tagsPreviousIteration==null)
			{
				ret = "No debug information for first iteration.";
			}
			else
			{
				ret = comparisonInformation(tagsPreviousIteration, tagsAllCorpus);
			}
			tagsPreviousIteration = tagsAllCorpus;
			return ret;
		}
		
		private String comparisonInformation(List<G> previous, List<G> current)
		{
			if (previous.size()!=current.size()) return "Error: not same size";
			Iterator<G> previousIterator = previous.iterator();
			Iterator<G> currentIterator = current.iterator();
			int equal = 0;
			int different = 0;
			while (previousIterator.hasNext())
			{
				G previousTag = previousIterator.next();
				G currentTag = currentIterator.next();
				if (tagsEqual(previousTag, currentTag))
				{
					++equal;
				}
				else
				{
					++different;
				}
			}
			return "Number of equal tags = "+equal+". Number of different tags = "+different+".";
		}
		
		private final List<? extends List<? extends TaggedToken<K, G> >> corpus;
		private List<G> tagsPreviousIteration = null;
	}
	
	private static final <G> boolean tagsEqual(G g1, G g2)
	{
		if (g1==g2) return true;
		if (g1==null) return false;
		if (g2==null) return false;
		return g1.equals(g2);
	}
	
	/**
	 * Returns its argument.<BR>
	 * Used to get rid of "dead code" warnings/errors (in some compilers).
	 */
	private static final boolean is(boolean b)
	{
		return b;
	}
	
	
	
}
