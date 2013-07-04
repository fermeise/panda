package edu.kit.iti.algo2.panda.indexing;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map.Entry;

public class InvertedIndexSerializer {
	private static final String indexFilePrefix = "%PND-0.1%";
	
	public static void toStream(InvertedIndex index, OutputStream outputStream) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(outputStream);
		
		stream.writeChars(indexFilePrefix);
		stream.writeInt(index.documentCount);
		stream.writeFloat(index.averageDocumentLength);
		
		for(Entry<String, InvertedList> entry: index.invertedIndex.entrySet()) {
			final String word = entry.getKey();
			stream.writeInt(word.length());
			stream.writeChars(word);
			
			final int[] documents = entry.getValue().getDocuments();
			final int[] scores = entry.getValue().getScores();
			final int documentCount = entry.getValue().getDocumentCount();
			
			stream.writeInt(documentCount);
			int offset = 0;
			for(int i = 0; i < documentCount; i++) {
				stream.writeInt(documents[i] - offset);
				stream.writeShort(scores[i] - offset);
				offset = documents[i];
			}
		}
		stream.close();
	}
	
	public static InvertedIndex fromStream(InputStream inputStream) throws IOException {
		ObjectInputStream stream = new ObjectInputStream(inputStream);
		
		if(!readChars(stream, indexFilePrefix.length()).equals(indexFilePrefix)) {
			throw new IOException("The file does not seem to be a correct panda index file or the version does not match.");
		}
		
		InvertedIndex index = new InvertedIndex();
		
		try {
			index.documentCount = stream.readInt();
			index.averageDocumentLength = stream.readFloat();
			
			while(true) {
				final int wordLength = stream.readInt();
				String word = readChars(stream, wordLength);
				
				InvertedList list = new InvertedList();
				final int documentCount = stream.readInt();
				int offset = 0;
				for(int i = 0; i < documentCount; i++) {
					offset += stream.readInt();
					final int score = stream.readShort();
					list.add(offset, score);
				}
				
				index.invertedIndex.put(word, list);
			}
		} catch(EOFException e) {
		} finally {
			stream.close();
		}
		
		return index;
	}
	
	public static String readChars(ObjectInputStream stream, int count) throws IOException {
		char[] chars = new char[count];
		for(int i = 0; i < count; i++) {
			chars[i] = stream.readChar();
		}
		return new String(chars);
	}
}
