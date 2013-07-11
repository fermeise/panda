package edu.kit.iti.algo2.panda.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ObjectStringInputStream extends ObjectInputStream {
	public ObjectStringInputStream(InputStream in) throws IOException {
		super(in);
	}
	
	public String readString() throws IOException {
		return readChars(readInt());
	}
	
	public String readChars(int count) throws IOException {
		char[] chars = new char[count];
		for(int i = 0; i < count; i++) {
			chars[i] = readChar();
		}
		return new String(chars);
	}
}
