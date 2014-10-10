/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hw2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 *
 * @author mike
 */
public class PreProcess {

	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("ERROR: Missing input file argument.");
			System.exit(0);
		}

		// INPUT VARS
		FileReader inputStream = null;
		CharBuffer cbuf = CharBuffer.allocate(100000);
		InputStream inputSentenceModel = null;
		InputStream inputTokenizerModel = null;
		try {

			// get trained english sentence model for sentence splitter
			inputSentenceModel = new FileInputStream("../doc/en-sent.bin");
			SentenceModel model = new SentenceModel(inputSentenceModel);
			SentenceDetectorME sDetector = new SentenceDetectorME(model);

			// get english tokenizer model
			inputTokenizerModel = new FileInputStream("../doc/en-token.bin");
			TokenizerModel tokModel = new TokenizerModel(inputTokenizerModel);
			Tokenizer tokenizer = new TokenizerME(tokModel);

			// read incomming data file into String 
			inputStream = new FileReader(args[0]);
			int bytesRead = inputStream.read(cbuf); // read entire file into buffer
			String rawText = new String(cbuf.array(), 0, bytesRead);

			// parse text into Sentances 
			String sentences[] = sDetector.sentDetect(rawText);
			for (int x = 0; x < sentences.length; x++) { // add start and end tags to all sentences
				String[] tokenizedSentence = tokenizer.tokenize(sentences[x]);
				System.out.print("<s> "); // start tag
				for (int y = 0; y < tokenizedSentence.length; y++) {
					if (y == tokenizedSentence.length - 1) {
						System.out.print(tokenizedSentence[y]);
					} else {
						System.out.print(tokenizedSentence[y] + " ");
					}

				}
				if (x != sentences.length - 1) {
					System.out.print(" </s>\n\n");
				} else {
					System.out.print(" </s>");
				}
			}
			System.out.print("\0"); // EOF

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} finally {
			// close input streams
			if (inputStream != null) {
				inputStream.close();
			}
			if (inputSentenceModel != null) {
				inputSentenceModel.close();
			}
			if (inputTokenizerModel != null) {
				inputTokenizerModel.close();
			}
		}

	}

}
