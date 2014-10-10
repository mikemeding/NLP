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
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * @author mike
 */
public class TestModel {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// REFACTOR THIS WHEN DONE
//		String[] args = new String[10];
//		args[1] = "doc/model.bin";

		// Read in my model
		NGramModel model = null;
		try {
			model = readModel(args[1]);
		} catch (IOException ioe) {
			System.err.print(ioe.getMessage());
			System.exit(-1); // model parse error
		}

		// parse test-text (use createmodel input as template)
		NGram[] nGram = model.model[1].parseSentence(new String[]{"<s>", "JOHN", "READ", "A", "BOOK", "</s>"});

		// compute probability of parse text and return
		try {
			double logProb = 0.0;
			for (NGram sentGram : nGram) {
				logProb += model.logProbability(sentGram, sentGram.size);
			}
			System.out.println("log base 10 prob:" + logProb);
		} catch (NGramException nge) {
			System.err.println(nge.getMessage());
			System.exit(-1); // error probability calc
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
