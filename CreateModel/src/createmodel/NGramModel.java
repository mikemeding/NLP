/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package createmodel;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author mike
 */
public class NGramModel implements Serializable {

	private static final long serialVersionUID = 2558381608141582463L;

	public NGramCollection[] model;

	/**
	 * Starting from an empty model
	 *
	 * @param maxSize
	 */
	public NGramModel(int maxSize) {
		this.model = new NGramCollection[maxSize];
	}

	/**
	 * Pre-existing model
	 *
	 * @param model
	 */
	public NGramModel(NGramCollection[] model) {
		this.model = model;
	}

	/**
	 * Only adds NGram collection if none exists
	 *
	 * @param nGramCollection
	 */
	public void addNGramCollection(NGramCollection nGramCollection) {
		if (this.model[nGramCollection.size - 1] == null) {
			this.model[nGramCollection.size - 1] = nGramCollection;
		}
	}

	/**
	 * Calculate the log probability of the given NGram with our model.
	 *
	 * @param modelSize
	 * @param nGram
	 * @param additiveConstant
	 * @return
	 */
	public double logProbability(NGram nGram, int modelSize, double additiveConstant) throws NGramException {
		// if single unigram
		if (nGram.size == 1) {
			double prob = probabilityUnigram(nGram);
			return Math.log10(prob) * (-1); //-log(P(unigram))
		}

		// if unigram model
		if (this.model.length == 1) {
			double sum = 1; // im stupid
			for (String token : nGram.getTokens()) {
				double prob = probabilityUnigram(new NGram(new String[]{token}));
				sum = sum * prob;

			}
			return Math.log10(sum) * (-1);
		} else {
			// if not unigram model
			return Math.log10((double) probabilityNGram(nGram, nGram.size, additiveConstant)) * (-1);

		}

//		throw new UnsupportedOperationException(); // no support of ngrams yet
	}

	/**
	 * Gives the raw unigram probability of a single unigram P(term) = C(term) /
	 * C(total unigrams) Assumes that the nGram is already a unigram
	 *
	 * @param nGram
	 * @return
	 * @throws NGramException
	 */
	private double probabilityUnigram(NGram nGram) throws NGramException {
		if (this.model[0].countNGram(nGram) != 0) {
			int count = this.model[0].countNGram(nGram);
			int total = this.model[0].nGrams.size();
			return (double) count / total;
		} else {
			// we have encountered an <UNK>
			NGram UNK = new NGram(new String[]{"<UNK>"});
			this.model[0].addNGram(UNK); // add to the count of <UNK>
			int count = this.model[0].countNGram(UNK);
			int size = this.model[0].nGrams.size();
			return (double) count / size; // C(<UNK>) / C(total unigrams)
		}
	}

	/**
	 * Gives the raw probability of an NGram occuring in our data (NO SMOOTHING)
	 *
	 * @param nGram
	 * @param modelSize
	 * @return
	 */
	private double probabilityNGram(NGram nGram, int modelSize, double additiveConstant) throws NGramException {
		double numerCount = (double) this.model[modelSize - 1].countNGram(nGram);
		numerCount += additiveConstant;
		double denomCount = (double) this.model[modelSize - 1].countAnyEnding(Arrays.copyOfRange(nGram.tokens, 0, modelSize - 1));
		denomCount += (1 + additiveConstant);
		if (numerCount == 0) { // will never happen if smoothing is applied
			throw new NGramException("-Infinity. Not possible with given data.");
		}
		return (double) numerCount / denomCount;
	}
}
