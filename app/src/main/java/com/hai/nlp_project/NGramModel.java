package com.hai.nlp_project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.StringTokenizer;

public class NGramModel {
	public static Hashtable<String, Hashtable<String, Integer>> ngram;
	public SyllableTree tree;
	public NGramModel(String file) {
		ngram = new Hashtable<String, Hashtable<String, Integer>>();
		tree = new SyllableTree(file);
	}
	
	void construct(String corpus) {
		try {
			ngram.clear();
			FileReader fr = new FileReader(corpus);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s, " ");
				String pre = "";
				boolean existedPre = false;
				while (st.hasMoreTokens()) {
					String syl = st.nextToken();
					boolean existedSyl = tree.search(syl);
					if (existedSyl) {
						if (ngram.containsKey(syl)) {
							Hashtable<String, Integer> value = ngram.get(syl);
							if (existedPre) {
								if (value.containsKey(pre)) {
									value.put(pre, value.get(pre) + 1);
								}
								else {
									value.put(pre, 1);
									
								}
							}
						}
						else {
							Hashtable<String, Integer> tempHash = new Hashtable<String, Integer>();
							if (existedPre) {
								tempHash.put(pre, 1);
							}
							ngram.put(syl, tempHash);
									
						}
					}
					pre = syl;
					existedPre = existedSyl;
				}
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void loadNgram() {
		
	}
	
	void smoothing() {
		
	}
	
	public void testNgram() {
		for (int i = 0; i < 10; i++) {
			Hashtable<String, Integer> temp = new Hashtable<String, Integer>();
			
			for (int j = 0; j < 5; j++) {
				temp.put("sub-string" + j, j + 10);
			}
			ngram.put("string" + i, temp);
		}
		saveNgram();
	}
	
	public void saveNgram() {
		try {
			FileWriter fw = new FileWriter("output/ngram");
			
			Enumeration<String> keys = ngram.keys();
			while (keys.hasMoreElements()) {
				String s = keys.nextElement();
				fw.write(s);
				Hashtable<String, Integer> subTable = ngram.get(s);
				Enumeration<String> subkeys = subTable.keys();
				while (subkeys.hasMoreElements()) {
					String ss = subkeys.nextElement();
					int count = subTable.get(ss);
					fw.write(" "+ ss + " " + count);
				}
				fw.write("\n");
			}
			
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test(String input) {
		
	}
	
}

class SyllableTree{
	class SyllableNode {
		String content;
		SyllableNode left;
		SyllableNode right;
		public SyllableNode(String s) {
			content = s;
		}
	};
	SyllableNode root;
	public SyllableTree(String content) {
//		try {
//			FileReader fr = new FileReader(file);
//			BufferedReader br = new BufferedReader(fr);
//			String s = br.readLine();
//			root = new SyllableNode(br.readLine());
//			while ((s = br.readLine()) != null) {
//				append(s, root);
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}

		Scanner sc = new Scanner(content);
		String s = sc.next();
		root = new SyllableNode(sc.next());
		while (sc.hasNext()){
			s = sc.next();
			append(s, root);
		}

	}
	void append(String s, SyllableNode r) {
		if (s.compareTo(r.content) < 0) {
			if (r.left == null) {
				r.left = new SyllableNode(s);
			}
			else {
				append(s, r.left);
			}
		}
		else if (s.compareTo(r.content) > 0) {
			if (r.right == null) {
				r.right = new SyllableNode(s);
			}
			else {
				append(s, r.right);
			}
		}
	}
	public boolean search(String s) {
		return search(s, root);
	}
	boolean search(String s, SyllableNode n) {
		if (n == null) {
			return false;
		}
		else if (n.content.compareTo(s) == 0) {
			return true;
		}
		else {
			return s.compareTo(n.content) < 0 ?  search(s, n.left) : search(s, n.right);
		}
	}
}
