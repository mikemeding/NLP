/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package createmodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 *
 * @author mike
 */
public class CreateModel {

	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		if (args1.length < 3) {
//			System.err.println("ERROR: Missing arguments. ([trainingData.file],[int n-gram],[modelData.file])");
//			System.exit(0);
//		}
		String[] args1 = new String[10]; // refactor this back to args when done!!!!!!
		args1[0] = "/BIG/DOCUMENTS/NLP/CreateModel/doc/preprocessed-training-data.txt"; // test data
		args1[1] = "3";	// n-gram size
		// get n-gram size
		int nGramSize = Integer.parseInt(args1[1]);

		// INPUT VARS
		FileReader inputStream = null;
		CharBuffer cbuf = CharBuffer.allocate(10000);
		InputStream inputTokenizerModel = null;
		try {

			// get english tokenizer model
			inputTokenizerModel = new FileInputStream("/BIG/DOCUMENTS/NLP/CreateModel/doc/en-token.bin");
			TokenizerModel tokModel = new TokenizerModel(inputTokenizerModel);
			Tokenizer tokenizer = new TokenizerME(tokModel);

			// read incomming data file into String 
			inputStream = new FileReader(args1[0]);
			int bytesRead = inputStream.read(cbuf); // read entire file into buffer
			String rawTrainingData = new String(cbuf.array(), 0, bytesRead);

			// parse raw data into tokens
			String[] tokens = tokenizer.tokenize(rawTrainingData);

			// parse text into Sentances 
			String sentences[] = getSentences(tokens, rawTrainingData);

			// get number of unique words in the corpra
			String[] uniqueWords = getUniqueWords(tokenizer.tokenize(rawTrainingData));

			// test countOccur
			int occur = countOccur("/SS", tokens);

			// 2D array of nGrams with acess to their individual words.
			String[][] nGramCollection = new String[(tokens.length * nGramSize)][nGramSize]; //TODO: THIS MAY CAUSE ERROR

			// generate and store all possible n-grams of the given size or smaller
			int z = 1;
			int nGramTotal = 0;
			while (z <= nGramSize) {
				for (int s = 0; s < sentences.length; s++) {
					String[] sentTokens = tokenizer.tokenize(sentences[s]);
					int i = 0;
					for (i = 0; i < sentTokens.length; i++) { // for each token
						if (i + z <= sentTokens.length) { // check for overrun
							for (int n = 0; n < z; n++) { // print n-gram
								nGramCollection[nGramTotal + i][n] = sentTokens[i + n]; // collect nGrams
							}
						}
					}
					nGramTotal += (i - (nGramSize - 1)); // DAMN this took forever to figure out....
				}
				z++;
			}

			// test nGram counter
			int grams = nGramCount(new String[]{"that", "will", "be"}, nGramCollection);

			// test probablility of nGram
			double prob = nGramProbability(new String[]{"that", "will", "be"}, nGramCollection);

			// output probability table
			for (int x = 0; x < nGramCollection.length; x++) {
				System.out.print(Arrays.toString(nGramCollection[x]) + "  ");
				System.out.print(nGramProbability(nGramCollection[x], nGramCollection));
				System.out.println("\n");
			}

			int x = 0; // stop point

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		} catch (NumberFormatException nfe) {
			System.err.println("n-gram size parse error");
		} finally {
			// close input streams
			if (inputStream != null) {
				inputStream.close();
			}
			if (inputTokenizerModel != null) {
				inputTokenizerModel.close();
			}
		}
	}

	/**
	 * Assuming that the raw text has been preprocessed by my preprocessor This
	 * will pull out all the sentences and put them into an array
	 *
	 * @return
	 */
	public static String[] getSentences(String[] tokens, String rawText) {
		String sentence = "";
		Set<String> sentences = new HashSet<>();
		for (int i = 0; i < tokens.length; i++) {
			sentence += tokens[i] + " ";
			if (tokens[i].equals("/SS")) {
				sentences.add(sentence);
				sentence = ""; // reset
			}
		}
		return sentences.toArray(new String[sentences.size()]);
	}

	/**
	 * Takes an array of tokens and returns the list of unique tokens
	 *
	 * @param tokens
	 * @return
	 */
	public static String[] getUniqueWords(String[] tokens) {
		Set<String> vocab = new HashSet<>();

		for (String word : tokens) {
			if (!vocab.contains(word)) { // if it does not exist
				vocab.add(word); // add it.
			}
		}

		String[] rtrn = vocab.toArray(new String[vocab.size()]); // parse hashmap to array
		return rtrn;
	}

	/**
	 * Counts the occurrences of a given word over a given array of tokens
	 *
	 * @param word
	 * @param text
	 * @return
	 */
	public static int countOccur(String word, String[] text) {
		int occur = 0;
		for (String token : text) {
			if (token.equals(word)) {
				occur++;
			}
		}
		return occur;
	}

	/**
	 * Gets the count of a given nGram over a given corpra.
	 *
	 * @param nGramSize
	 * @param nGram
	 * @param corpra
	 * @return
	 */
	public static int nGramCount(String[] nGram, String[][] corpra) {
		int count = 0;
		for (String[] nGramFound : corpra) { // loop over entire corpra
			if (Arrays.equals(nGramFound, nGram)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * This function returns the probablity of a given nGram over the given
	 * corpra. It is assumed that the corpra has been trained for that size of
	 * nGram.
	 *
	 * @param nGram (The nGram to calculate for)
	 * @param corpra (The text to calculate against)
	 * @return
	 */
	public static double nGramProbability(String[] nGram, String[][] corpra) {
		try {
			double probablility = 0.0;
			String[] nGramDivisor = new String[nGram.length];
			nGramDivisor = Arrays.copyOf(nGram, nGram.length); // deep copy array
			nGramDivisor[nGramDivisor.length - 1] = null; // set last element to null
			int dividend = nGramCount(nGram, corpra);
			int divisor = nGramCount(nGramDivisor, corpra);
			probablility = (double) dividend / divisor; // parse probability as a double
			if (probablility == 1.0) {
				return 0.0;
			} else {
				return -Math.log10(probablility);
			}
		} catch (ArithmeticException ae) {
			return 0.0;
		}
	}

}
