package edu.kit.iti.algo2.panda.indexing;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Map.Entry;

import edu.kit.iti.algo2.panda.util.ObjectStringInputStream;

public class InvertedIndexSerializer {
	private static final String indexFilePrefix = "%PND-0.3i%";
	private static final String indexFilePrefixShorts = "%PND-0.3s%";
	
	public static void toStream(InvertedIndex index, OutputStream outputStream) throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(outputStream);
		
		boolean useShorts = index.maxDocumentId <= Short.MAX_VALUE;
		
		if(useShorts) {
			stream.writeChars(indexFilePrefixShorts);
		} else {
			stream.writeChars(indexFilePrefix);
		}
		
		stream.writeInt(index.maxDocumentId);
		stream.writeLong(index.totalDocumentLength);
		stream.writeBoolean(index.calculatedInitialScoring);
		stream.writeInt(index.documentsAddedSinceScoring);
		
		stream.writeInt(index.obsoleteDocuments.size());
		for(Integer id: index.obsoleteDocuments) {
			stream.writeInt(id);
		}
		
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
				if(useShorts) {
					stream.writeShort(documents[i] - offset);
				} else {
					stream.writeInt(documents[i] - offset);
				}
				stream.writeShort(scores[i]);
				offset = documents[i];
			}
		}
		stream.close();
	}
	
	public static InvertedIndex fromStream(InputStream inputStream) throws IOException, ParseException {
		ObjectStringInputStream stream = new ObjectStringInputStream(inputStream);
		
		String prefix = stream.readChars(indexFilePrefix.length());
		boolean useShorts = prefix.equals(indexFilePrefixShorts);
		if(!useShorts && !prefix.equals(indexFilePrefix)) {
			stream.close();
			throw new ParseException("The file does not seem to be a correct panda index file " +
					"or the version does not match.", 0);
		}
		
		InvertedIndex index = new InvertedIndex();
		
		try {
			index.maxDocumentId = stream.readInt();
			index.documentLength = new int[index.maxDocumentId];
			index.totalDocumentLength = stream.readLong();
			index.calculatedInitialScoring = stream.readBoolean();
			index.documentsAddedSinceScoring = stream.readInt();
			
			int obsoleteDocumentCount = stream.readInt();
			for(int i = 0; i < obsoleteDocumentCount; i++) {
				index.obsoleteDocuments.add(stream.readInt());
			}
			
			while(true) {
				String word = stream.readString();
				
				InvertedList list = new InvertedList();
				final int documentCount = stream.readInt();
				int offset = 0;
				for(int i = 0; i < documentCount; i++) {
					if(useShorts) {
						offset += stream.readShort();
					} else {
						offset += stream.readInt();
					}
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
}
