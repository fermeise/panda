package edu.kit.iti.algo2.panda.indexing.kgrams;

import java.util.Arrays;

public class LevinshteinDistance {
	public static int distance(char[] s, char[] t) {
		if (Arrays.equals(s, t)) return 0;
		if (s.length == 0) return t.length;
		if (t.length == 0) return s.length;
		
		int[] v0 = new int[t.length + 1];
		int[] v1 = new int[t.length + 1];
		
		for (int i=0; i < v0.length; i++) {
			v0[i] = i;
		}
		
		for (int i=0; i < s.length; i++) {
			v1[0] = i + 1;
			
			for (int j=0; j < t.length; j++) {
				int cost = (s[i] == t[j]) ? 0 : 1;
				v1[j+1] = Math.min(Math.min(v1[j]+1, v0[j + 1] + 1), v0[j] + cost);
				v0[j] = v1[j];
			}
			v0[v0.length-1] = v1[v0.length-1];
			
		}
		return v1[t.length];
	}
}
