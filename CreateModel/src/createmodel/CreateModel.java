/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package createmodel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.CharBuffer;

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
		if (args.length < 3) {
			System.err.println("ERROR: Missing arguments. ([trainingData.file],[int n-gram],[modelData.file])");
			System.exit(0);
		}
//		args[0] = "doc/preprocessed-training-data.txt"; // test data
//		args[1] = "3";	// n-gram size
//		args[2] = "nGramModel.bin";
		// get n-gram size
		int nGramMaxSize = Integer.parseInt(args[1]);

		// INPUT VARS
		FileReader inputStream = null;
		CharBuffer cbuf = CharBuffer.allocate(100000); // size limitation
		try {

			// read incomming data file into String 
			inputStream = new FileReader(args[0]);
			int bytesRead = inputStream.read(cbuf); // read entire file into buffer
			String rawTrainingData = new String(cbuf.array(), 0, bytesRead);

			// parse text into Sentances 
			String sentences[] = rawTrainingData.split("\n\n");

			// create our model of all unigram,bigram,trigram... for given data
			NGramModel nGramModel = new NGramModel(nGramMaxSize);
			try {
				for (int gramSize = 0; gramSize < nGramMaxSize; gramSize++) { // for each n-gram size (unigram, bigram, trigram)
					NGramCollection nGramCollectionTemp = new NGramCollection(gramSize + 1);
					for (String sent : sentences) {
						String[] tokens = sent.split(" "); // entire corpra of tokens
						nGramCollectionTemp.addNGram(nGramCollectionTemp.parseSentence(tokens)); // add to our n-gram collector
					}
					nGramModel.addNGramCollection(nGramCollectionTemp);
				}
			} catch (NGramException nge) {
				System.err.println(nge.getMessage());
				System.exit(-1); // model creation error
			}

			saveModel(nGramModel, args[2]);

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

		}
	}

	/**
	 * Save our awesome n-gram model that we spent all night making... mmmmmm.
	 *
	 * @param model
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveModel(NGramModel model, String fileName) throws IOException {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {
			fileOut = new FileOutputStream(fileName);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(model);

			System.out.println("Model file saved in : " + fileName);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			System.exit(-1); // write to file error
		} finally {
			if (out != null) {
				out.close();
			}
			if (fileOut != null) {
				fileOut.close();
			}
		}

	}
}
