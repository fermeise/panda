package edu.kit.iti.algo2.panda.indexing.kgrams;

import java.util.Arrays;

public class LevenshteinDistance {
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
				if (s[i] == t[j]) {
					v1[j+1] = v0[j];
				} else {
					v1[j+1] = Math.min(Math.min(v1[j], v0[j + 1]), v0[j]) + 1;
				}
				v0[j] = v1[j];
			}
			v0[t.length] = v1[t.length];
			
		}
		return v1[t.length];
	}
	
	public static boolean smallerOrEqualThan(char[] s, char[] t, int maxDistance) {
		if (maxDistance < 0) return false;
		if (maxDistance == 0) return Arrays.equals(s, t);
		if (Arrays.equals(s, t)) return true;
		if (s.length == 0) return t.length <= maxDistance;
		if (t.length == 0) return s.length <= maxDistance;
		
		if (t.length > s.length) {
			char[] temp = t;
			t = s;
			s = temp;
		}
		
		int[] v0 = new int[t.length + 1];
		int[] v1 = new int[t.length + 1];
		
		for (int i=0; i < Math.min(maxDistance + 2, v0.length); i++) {
			v0[i] = i;
		}
		
		for (int i=0; i < s.length; i++) {
			v1[0] = i + 1;
			int lower = Math.max(0, i - maxDistance - 1);
			int upper = Math.min(t.length, i + maxDistance + 1);
			for (int j=lower; j < upper; j++) {
				if (s[i] == t[j]) {
					v1[j + 1] = v0[j];
				} else {
					v1[j + 1] = Math.min(Math.min(v1[j], v0[j + 1]), v0[j]) + 1;
				}
				v0[j] = v1[j];
			}
			v0[upper] = v1[upper];
		}
		return v0[t.length] <= maxDistance;
	}
}
