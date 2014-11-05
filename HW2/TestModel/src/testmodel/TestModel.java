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
	public static void main(String[] args) throws IOException {
//		String[] args = new String[10];
		if (args.length < 3) {
			System.err.println("Incorrect arguments. [test-data.txt][model-data.bin][val]");
			System.exit(-1);
		}

//		//TEST DATA
//		args[0] = "doc/smallTest.txt";
//		args[1] = "doc/model.bin";
//		args[2] = ".03";
		// parse additive constant
		double additiveConstant = Double.parseDouble(args[2]);

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

		System.out.println("TEST DATA:");

		// parse text into Sentances 
		String sentences[] = rawTestData.split("\n\n");
		double logProb = 0.0;
		for (String sent : sentences) { // for each sentence
			// parse into array of nGrams
			NGram[] nGram = model.model[model.model.length - 1].parseSentence(sent.split(" "));

			// compute probability of sequence of nGrams and return
			try {

				for (NGram sentGram : nGram) {
					logProb += model.logProbability(sentGram, sentGram.size, additiveConstant);
				}
				System.out.println(sent);
			} catch (NGramException nge) {
				System.err.println(nge.getMessage());
				System.exit(-1); // error probability calc
			}
		}

		System.out.println("\nLog base 10 prob: " + logProb);

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
