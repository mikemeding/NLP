package createmodel;

import java.io.Serializable;

/**
 *
 * @author mike
 */
public class NGramProperties implements Serializable {

	public int count;
	public double probability;

	public NGramProperties(int count) {
		this.count = count;
	}

	public NGramProperties(int count, double probability) {
		this.count = count;
		this.probability = probability;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

}
