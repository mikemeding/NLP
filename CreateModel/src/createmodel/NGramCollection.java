package createmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mike
 */
public class NGramCollection implements Serializable {

	Map<NGram, NGramProperties> nGrams = new HashMap<>();
	int size; // 1 = uni-gram, 2 = bi-gram, 3 = tri-gram, ... etc

	public NGramCollection(int size) {
		this.size = size;
	}

	/**
	 * Add the NGram to our list of NGrams. This checks for duplicates and keeps
	 * track of their counts.
	 *
	 * @param nGram
	 * @throws NGramException
	 */
	public void addNGram(NGram nGram) throws NGramException {
		if (nGram.size == this.size) {
			if (countNGram(nGram) == 0) {
				// if it does not exist
				this.nGrams.put(nGram, new NGramProperties(1));
			} else {
				// if it does exist
				// TODO: this could also be improved.
				Set<NGram> val = this.nGrams.keySet();
				for (NGram temp : val) { // for all keys in hashmap
					if (temp.equals(nGram)) { // if equivilant nGram is found
						this.nGrams.get(temp).count++;
					}
				}
			}

		} else {
			throw new NGramException("Invalid NGram size for container. Collection = " + this.size + ", NGram = " + nGram.size);
		}
	}

	/**
	 * Add a bunch of NGrams at once.
	 *
	 * @param NGrams
	 * @throws NGramException
	 */
	public void addNGram(NGram[] NGrams) throws NGramException {
		for (NGram nGram : NGrams) {
			addNGram(nGram);
		}
	}

	/**
	 * Finds and returns the count of the given NGram. If not found it returns
	 * 0; TODO: this could be optimized later on.
	 *
	 * @param nGram
	 * @return
	 */
	public int countNGram(NGram nGram) throws NGramException {
		if (nGram.size == this.size) {
			Set<NGram> val = this.nGrams.keySet();
			for (NGram temp : val) { // for all keys in hashmap
				if (temp.equals(nGram)) { // if equivilant nGram is found
					return this.nGrams.get(temp).count; // return its count
				}
			}
			return 0; // if NGram is not found
		} else {
			throw new NGramException("Invalid NGram size for container. Collection = " + this.size + ", NGram = " + nGram.size);
		}
	}

	/**
	 * parses a sentence and returns a list of all possible NGrams of the given
	 * container size. (bi-gram,tri-gram, etc)
	 *
	 * @param tokens
	 * @return
	 */
	public NGram[] parseSentence(String[] tokens) {
		ArrayList<NGram> temp = new ArrayList<>();
		for (int x = 0; x < tokens.length; x++) {
			if (x + this.size <= tokens.length) {
				temp.add(new NGram(Arrays.copyOfRange(tokens, x, x + this.size)));
			}
		}
		return temp.toArray(new NGram[temp.size()]);
	}

//	/**
//	 * The probability of a given NGram changes due to the number of total
//	 * NGrams. This must only be run after all NGrams from the training data
//	 * have been added. P(NGram) = (count of this NGram / total # of NGrams of
//	 * this size)
//	 */
//	public void refreshProbabilies() {
//		try {
//			for (NGram nGram : this.nGrams.keySet().toArray(new NGram[this.nGrams.size()])) { // loop all NGrams in our HashMap
//				System.out.println(nGram.toString() + "   " + probabilityNGram(nGram)); // DEBUG MESSAGE
//				this.nGrams.get(nGram).probability = probabilityNGram(nGram); // update NGram
//			}
//		} catch (NGramException nge) {
//			System.err.println(nge.getMessage());
//			System.exit(-1); // big error
//		}
//	}
//
//	/**
//	 * Private method to calculate the probability of a given NGram over our
//	 * given list of NGrams
//	 *
//	 * @param nGram
//	 * @return
//	 * @throws NGramException
//	 */
//	private double probabilityNGram(NGram nGram) throws NGramException {
//		if (nGram.size == this.size || countNGram(nGram) == 0) {
//			return (double) countNGram(nGram) / this.nGrams.size(); // needs to be -log base 10
//		} else {
//			throw new NGramException("Cannot calculate probability of an NGram which does not exist.");
//		}
//	}
}
