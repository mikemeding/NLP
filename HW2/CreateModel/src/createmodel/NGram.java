package createmodel;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author mike
 */
public class NGram implements Serializable {

	public int size; // 1 = uni-gram, 2 = bi-gram, 3 = tri-gram, ... etc
	public String[] tokens;

	public NGram(String[] tokens) {
		this.tokens = tokens;
		this.size = tokens.length;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public boolean equals(NGram nGram) {
		return Arrays.equals(this.tokens, nGram.tokens); // deep equals comparing all tokens
	}

	@Override
	public String toString() {
		return Arrays.toString(tokens);
	}

}
