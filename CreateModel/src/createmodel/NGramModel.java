/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package createmodel;

import java.io.Serializable;

/**
 *
 * @author mike
 */
public class NGramModel implements Serializable {

	NGramCollection[] model;

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
	 * @param nGram
	 * @return
	 */
	public double logProbability(NGram nGram) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
