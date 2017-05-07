package com.crf.crf;

import com.crf.filters.CRFFeaturesAndFilters;
import com.function.DerivableFunction;
import com.utilities.ArithmeticUtilities;
import com.utilities.CRFException;
import com.utilities.TaggedToken;
import com.utilities.VectorUtilities;

import static com.utilities.ArithmeticUtilities.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;






/**
 *  The CRF log-likelihood function for the given <B>tagged</B> corpus, under the given model.
 * <BR>
 * The "likelihood" of a sequence of tags for a given sentence is the probability of that sequence of tags to occur (to exist)
 * for that sentence.
 * This probability is:
 * \Sum{j=0}^{sentence-length-1}(e^( \Sum_{i=0}^{number-of-features-1}(\theta_i*f_i(j,g,g')) )) / Z(x)
 * where \theta_i is parameter number i (these are the parameters that are learned during training - these parameters form
 * the input vector "point" of this function), f_i is feature number i, g is the tag of token number j, and g' is the tag of
 * token number j-1. Z(x) is the normalization factor.
 * <P>
 * The log-likelihood is \Sum_{sentence \in corpus}log( \Sum{j=0}^{sentence-length-1}(e^( \Sum_{i=0}^{number-of-features-1}(\theta_i*f_i(j,g,g')) )) / Z(x) ).
 * <BR>
 * The log is in the natural basis (e) (It is actually ln).
 * 
 * @author 1001937
 *
 * @param <K>
 * @param <G>
 */
public class CRFLogLikelihoodFunction<K,G> extends DerivableFunction {
	/**
	 * A corpus -- a list of tagged sequences.
	 * <B>The whole corpus should reside in the internal memory completely!</B> Otherwise, the run will be very slow.
	 */
	private final List<? extends List<? extends TaggedToken<K, G> >> corpus;
	private final CRFTags<G> crfTags;
	private final CRFFeaturesAndFilters<K, G> features;
	private final boolean useRegularization;
	private final BigDecimal sigmaSquare_inverseRegularizationFactor;
	
	private static final Logger logger = Logger.getLogger(CRFLogLikelihoodFunction.class);	
	
	
	/**
	 * Constructs the log-likelihood function of the CRF.
	 * Note that the whole corpus should be inside the internal memory (the JVM heap). It should not be in disk or data-base.
	 * 
	 * @param corpus A corpus -- a list of sequences of tokens, where each token has a tag (this is a gold-standard corpus: the tags are known).
	 * @param crfTags The tags that exist in the given corpus. See {@link CrfTagsBuilder}.
	 * @param features The CRF features.
	 * @param useRegularization whether to use a regularization factor or not.
	 * @param sigmaSquare_inverseRegularizationFactor the \sigma^2 parameter of the L2 regularization factor.
	 */
	public CRFLogLikelihoodFunction(List<? extends List<? extends TaggedToken<K, G>>> corpus, CRFTags<G> crfTags,
			CRFFeaturesAndFilters<K, G> features, boolean useRegularization,
			double sigmaSquare_inverseRegularizationFactor)
	{
		super();
		this.corpus = corpus;
		this.crfTags = crfTags;
		this.features = features;
		this.useRegularization = useRegularization;
		this.sigmaSquare_inverseRegularizationFactor = big(sigmaSquare_inverseRegularizationFactor);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.postagging.function.Function#value(double[])
	 */
	@Override
	public BigDecimal value(BigDecimal[] point)
	{
		logger.debug("Calculating value");
		
		CRFModel<K, G> model = createModel(point);
		BigDecimal regularization = useRegularization?calculateRegularizationFactor(point):BigDecimal.ZERO;
		logger.debug("Calculating sum weighted features");
		BigDecimal sumWeightedFeatures = calculateSumWeightedFeatures(model);
		logger.debug("Calculating sum log normalizations");
		BigDecimal sumOfLogNormalizations = calculateSumOfLogNormalizations(model);
		
		// Note: log(x/y) = log(x)-log(y). Thus, log (e^x/y) = x - log(y).
		// In our case we need log(e^(sum_weighted_features_for_sentence)/Normalization) = sum_weighted_features_for_sentence - log(Normalization.)
		// And we sum the above over the whole corpus (i.e., over all the sentences).
		
		BigDecimal ret = safeSubstract(safeSubstract(sumWeightedFeatures, sumOfLogNormalizations), regularization);
		logger.debug("Calculating value - done.");
		return ret;
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.postagging.function.DerivableFunction#gradient(double[])
	 */
	@Override
	public BigDecimal[] gradient(BigDecimal[] point)
	{
		logger.debug("Calculating gradient");
		
		CRFModel<K, G> model = createModel(point);
		
		logger.debug("Calculating empirical feature values");
		CRFEmpiricalFeatureValueDistributionInCorpus<K,G> empiricalFeatureValue = new CRFEmpiricalFeatureValueDistributionInCorpus<K,G>(corpus.iterator(),model.getFeatures());
		empiricalFeatureValue.calculate();
		
		logger.debug("Calculating expected feature values by model");
		CRFFeatureValueExpectationByModel<K, G> featureValueExpectationsByModel = new CRFFeatureValueExpectationByModel<K, G>(corpus.iterator(),model);
		featureValueExpectationsByModel.calculate();
		
		logger.debug("Creating gradient array.");
		BigDecimal[] ret = new BigDecimal[point.length];
		for (int parameterIndex=0;parameterIndex<ret.length;++parameterIndex)
		{
			BigDecimal regularizationDerivative = useRegularization?calculateRegularizationDerivative(point[parameterIndex]):BigDecimal.ZERO;
			ret[parameterIndex] = safeSubstract(safeSubstract(empiricalFeatureValue.getEmpiricalFeatureValue()[parameterIndex], featureValueExpectationsByModel.getFeatureValueExpectation()[parameterIndex]), regularizationDerivative);
		}
		return ret;
	}


	/*
	 * (non-Javadoc)
	 * @see org.postagging.function.Function#size()
	 */
	@Override
	public int size()
	{
		return features.getFilteredFeatures().length;
	}

	
	
	
	private BigDecimal calculateSumWeightedFeatures(CRFModel<K, G> model)
	{
		BigDecimal sumWeightedFeatures = BigDecimal.ZERO;
		for (List<? extends TaggedToken<K, G> > sentence : corpus)
		{
			K[] sentenceAsArray = CRFUtilities.extractSentence(sentence);
			G previousTag = null;
			int tokenIndex=0;
			for (TaggedToken<K, G> taggedToken : sentence)
			{
				BigDecimal sumForThisToken = CRFUtilities.oneTokenSumWeightedFeatures(model,sentenceAsArray,tokenIndex,taggedToken.getTag(),previousTag);
				sumWeightedFeatures = safeAdd(sumWeightedFeatures, sumForThisToken);
				
				previousTag = taggedToken.getTag();
				++tokenIndex;
			}
			if (tokenIndex!=sentence.size()) {throw new CRFException("BUG");}
		}
		
		return sumWeightedFeatures;
	}
	
	private BigDecimal calculateSumOfLogNormalizations(CRFModel<K, G> model)
	{
		ExecutorService executor = Executors.newWorkStealingPool();
		List<Future<BigDecimal>> futures = new LinkedList<>();
		BigDecimal sum = BigDecimal.ZERO;
		for (final List<? extends TaggedToken<K, G> > sentence : corpus)
		{
			futures.add(executor.submit(new Callable<BigDecimal>()
			{
				@Override
				public BigDecimal call() throws Exception
				{
					K[] sentenceAsArray = CRFUtilities.extractSentence(sentence);
					CRFRememberActiveFeatures<K, G> activeFeaturesForSentence = CRFRememberActiveFeatures.findForSentence(features, crfTags, sentenceAsArray);
					CRFForwardBackward<K, G> forwardBackward = new CRFForwardBackward<K, G>(model,sentenceAsArray,activeFeaturesForSentence);
					//forwardBackward.calculateForwardAndBackward();
					forwardBackward.calculateOnlyNormalizationFactor();
					
					return ArithmeticUtilities.log(forwardBackward.getCalculatedNormalizationFactor());
				}
			}));
		}
		for (Future<BigDecimal> future : futures)
		{
			try
			{
				sum = safeAdd(sum, future.get());
			}
			catch (InterruptedException | ExecutionException e)
			{
				throw new CRFException(e);
			}
		}
		
		return sum;
	}
	
	private BigDecimal calculateRegularizationFactor(BigDecimal[] parameters)
	{
		return safeDivide(VectorUtilities.euclideanNormSquare(parameters), safeMultiply(big(2.0), sigmaSquare_inverseRegularizationFactor));
	}
	
	private BigDecimal calculateRegularizationDerivative(BigDecimal parameter)
	{
		return safeDivide(parameter, sigmaSquare_inverseRegularizationFactor);
	}

	
	private CRFModel<K, G> createModel(BigDecimal[] point)
	{
		if (point.length!=features.getFilteredFeatures().length) {throw new CRFException("Number of parameters differs from number of features.");}
		ArrayList<BigDecimal> parameters = new ArrayList<BigDecimal>(point.length);
		for (BigDecimal parameter : point)
		{
			parameters.add(parameter);
		}
		return new CRFModel<K, G>(crfTags,features,parameters);
	}
	
}
