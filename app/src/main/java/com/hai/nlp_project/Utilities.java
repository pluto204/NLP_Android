package com.hai.nlp_project;

import java.util.regex.Pattern;

public class Utilities {
	public static Pattern unusual = Pattern.compile(".*[@#$%fjwz&^].*");
	public static String consonants = new String("bcdđghklmnpqrstvx");
	public static Pattern symbol = Pattern.compile("[().,!?]");
    public static boolean containUnusualCharacter(String s)
    {
    	symbol.matcher(s).replaceAll("");
    	//s.replaceAll("[().,!?]", "");
    	return unusual.matcher(s).matches();
    	
    }
    
    public static boolean isNumber(String s) {
    	int len = s.length();
    	if (s == null || len == 0) return false;
    	int i = 0;
    	char c = s.charAt(i);
    	if (c == '-' || c == '+') {
    		if (len == 1) return false;
    		else i = 1;
    	}
    	for (; i < len; i++) {
    		c = s.charAt(i);
    		if (c < '0' && c > '9') {
    			return false;
    		}
    	}
    	return true;
    }
    public static boolean isVowel(char c) {
    	return !consonants.contains(c+"");
    }
    
    public static String getVowelComb(String s) {
    	StringBuffer sb = new StringBuffer();
    	char c;
    	for(int i = 0; i < s.length(); i++) {
    		c = s.charAt(i);
    		if (isVowel(c)) {
    			sb.append(c);
    		}
    	}
    	return sb.toString();
    }
}
