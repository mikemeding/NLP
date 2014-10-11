/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testmodel;

import createmodel.NGram;
import createmodel.NGramException;
import createmodel.NGramModel;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.CharBuffer;

/**
 *
 * @author mike
 */
public class TestModel {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] arxgs) throws IOException {

		String[] args = new String[10];
		args[0] = "doc/preprocessed-test-data.txt";
		args[1] = "doc/FinalModel.bin";

		// Read in my model
		NGramModel model = null;
		try {
			model = readModel(args[1]);
		} catch (IOException ioe) {
			System.err.print(ioe.getMessage());
			System.exit(-1); // model parse error
		}

		// read incomming test-text file
		FileReader inputStream = null;
		CharBuffer cbuf = CharBuffer.allocate(100000); // size limitation
		String rawTestData = null;
		try {
			inputStream = new FileReader(args[0]);
			int bytesRead = inputStream.read(cbuf); // read entire file into buffer
			rawTestData = new String(cbuf.array(), 0, bytesRead);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			System.exit(-1);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		// parse text into Sentances 
		String sentences[] = rawTestData.split("\n\n");
		for (String sent : sentences) { // for each sentence
			// parse into array of nGrams
			NGram[] nGram = model.model[model.model.length - 1].parseSentence(sent.split(" "));

			// compute probability of sequence of nGrams and return
			try {
				double logProb = 0.0;
				for (NGram sentGram : nGram) {
					logProb += model.logProbability(sentGram, sentGram.size);
				}
				System.out.println(sent);
				System.out.println("log base 10 prob:" + logProb);
			} catch (NGramException nge) {
				System.err.println(nge.getMessage());
				System.exit(-1); // error probability calc
			}
		}

	}

	public static NGramModel readModel(String fileName) throws IOException {
		NGramModel model = null;
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		try {
			fileIn = new FileInputStream(fileName);
			in = new ObjectInputStream(fileIn);
			model = (NGramModel) in.readObject();

		} catch (IOException ioe) {
			System.err.print(ioe.getMessage());
			System.exit(-1); // model parse error
		} catch (ClassNotFoundException cnfe) {
			System.err.print(cnfe.getMessage());
			System.exit(-1); // model parse error
		} finally {
			if (fileIn != null) {
				fileIn.close();
			}
			if (in != null) {
				in.close();
			}
		}
		return model;

	}
}
