package exercises;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

//import org.apache.commons.lang3.builder.HashCodeBuilder;


public class WordTraversal implements Comparable<WordTraversal>{

	public final char[] word;
	public String getWord(){
		return String.valueOf(this.word);
	}
	public int getLength() {
		return word.length;
	}
	/*
	 * returns a String that differs from word by having char c in position i
	 */
	private String getWord(char c, int i) {
		char[] out = this.word.clone();
		out[i] = c;
		return String.valueOf(out);
	}

	public final WordTraversal parent;

	private final int generation;

	public int getGeneration() {
		return generation;
	}

	private static int maxGen = 0;
	private void updateGen(){
		maxGen = Math.max(maxGen, this.generation);
	}

	private double distance;
	private boolean checkedForwards;
	private boolean checkedSideways;
	private boolean checkedBackwards;
	private boolean reached;
	public static final char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	public static final char[] EMPTY = {};

/*
 * for both lookup and englishDict, element i of the ArrayList is the Map or Set that corresponds to words of length i.
 * lookup is checked before building a new WordTraversal, to make sure that one doesn't already exist for the same word.
 */
	// use ArrayList as interface, not just implementation, because fast random access is essential.
	private static ArrayList<Map<String, WordTraversal>> lookup;
	private static ArrayList<Set<String>> englishDict = new ArrayList<Set<String>>(10);

//	// Use SortedSet and TreeSet instead of Queue and PriorityQueue because I need fast access to contains(o)
//	// These are now instance variables, declared elsewhere.
//	private static SortedSet<WordTraversal> pq = new TreeSet<>();
//	private static Set<String> alreadyChecked = new HashSet<>();

/*
 * Main constructor.
 */
//	Before calling constructor, check to make sure that lookup.get(len).contains(String word) is false
	public WordTraversal(char[] word, WordTraversal parent, char[] end) {
		this.word = word;
//		this.length = word.length;
		this.parent = parent;
		if (parent == null) this.generation = 0;
		else this.generation = parent.generation + 1;
//		double the integer distance of all words, so that an odd value corresponds to words that have been checkedSideways
//		the random decimal part of the distance is so that the comparator won't see different words
//		with the same integer distance as equal
		if (end.length > 0){
			this.distance = distance(word, end) * 2 + Math.random();
		}
		else {
			this.distance = 0;
		}
		this.checkedForwards = false;
		this.checkedSideways = false;
		this.checkedBackwards = false;
	}

/*
 * Alternate constructors, mostly for convenience of not having to convert between String and char[]
 */
	public WordTraversal(String word, WordTraversal parent, char[] end) {
		this(word.toCharArray(), parent, end);
	}
	public WordTraversal(String word, WordTraversal parent) {
		this(word.toCharArray(), parent, EMPTY);
	}
	public WordTraversal(char[] word, WordTraversal parent) {
		this(word, parent, EMPTY);
	}
	/*
	 * these constructors are used for building the spanning tree.
	 */
	public WordTraversal(char[] word, WordTraversal parent, boolean reached) {
		this(word, parent);
		this.reached = reached;
	}
	public WordTraversal(String word, WordTraversal parent, boolean reached) {
		this(word.toCharArray(), parent, reached);
	}

	/*
	 * Default file name
	 */
	public static void readDictionary() throws IOException{
		readDictionary("words.txt");
	}
	public static void readDictionary(String file) throws IOException{
		// read in the English dictionary, storing words of each length in a separate HashSet
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;
		while ((line = br.readLine()) != null){
			int len = line.length();
			String w = line.toUpperCase();
			try{
				if (englishDict.get(len) == null)
					englishDict.set(len, new HashSet<String>());
			} catch (IndexOutOfBoundsException e){
				while (englishDict.size() <= len){
					englishDict.add(null);
				}
				englishDict.set(len, new HashSet<String>());
			}
			englishDict.get(len).add(w);
		}
		br.close();
		englishDict.trimToSize();
		lookup = new ArrayList<Map<String, WordTraversal>>
			(Collections.nCopies(englishDict.size(), new HashMap<String, WordTraversal>()));
	}

	/*
	 * Can be called several different ways:
	 *
	 * java WordTraversal
	 *		generates a random length in the interval [4,8]
	 *		picks two random words of that length
	 *		finds a path between them
	 *
	 *
	 * java WordTraversal length
	 * 		when length is an integer, picks two random words of that length
	 * 		finds a path between them
	 *
	 * java WordTraversal startWord
	 * 		when startWord is not an integer, picks a random word of the same length as startWord
	 * 		finds a path between them
	 * 		if startWord is a non-word, the program will still try to find a path. In some cases, it may be successful,
	 * 		since only the intermediate and final steps must be English words.
	 *
	 * java WordTraversal startWord endWord
	 * 		finds a path between startWord and endWord.
	 * 		if startWord and endWord are different lengths, throws an IllegalArgumentException
	 * 		if endWord is not in the English dictionary, throws an IllegalArgumentException
	 *
	 */
	public static void main(String[] args) throws IOException{
		readDictionary("words.txt");
		int len = 0;
		char[] start = EMPTY;
		char[] end = EMPTY;
		if (args.length == 1) {
			try{
				len = Integer.valueOf(args[0]);
			} catch(NumberFormatException e){
				start = args[0].toUpperCase().toCharArray();
				len = start.length;
			}
		}
		else if (args.length >= 2){
			if (args[0].length() != args[1].length())
				throw new IllegalArgumentException("Starting and ending words must be the same length.");
			len = args[0].length();
			start = args[0].toUpperCase().toCharArray();
			end = args[1].toUpperCase().toCharArray();
			if (!englishDict.get(len).contains(args[1].toUpperCase()))
				throw new IllegalArgumentException("Ending word " + args[1].toUpperCase() + " not in the english dictionary.");
		}

/*
 * deprecated code for testing the detailed behavior of the .equals, .contains, and .hashCode methods
 * when called on Objects of different classes
 */
//		Set<WordTraversal> test = new HashSet<>();
//		WordTraversal w1 = new WordTraversal("FOO", null);
//		WordTraversal w2 = new WordTraversal("FOO", w1);
//		String s = "FOO";
//		System.out.println(w1.hashCode());
//		System.out.println(w2.hashCode());
//		System.out.println(s.hashCode());
//		System.out.println(sun.misc.Hashing.stringHash32(s));
//		test.add(new WordTraversal("FOO", null));
//		System.out.println(test.contains(s));
//
//		Set<String> test2 = new HashSet<>();
//		WordTraversal w3 = new WordTraversal("FOO", null);
//		String s3 = "FOO";
//		String s2 = "FOO";
//		System.out.println(w3.hashCode());
//		System.out.println(s2.hashCode());
//		System.out.println(s3.hashCode());
//		System.out.println(sun.misc.Hashing.stringHash32(s2));
//		System.out.println(sun.misc.Hashing.stringHash32(s3));
//		test2.add(s2);
//		System.out.println(test2.contains(s3));
//		System.out.println(test2.contains(w3));

		/*
		 * use a List<Object> to store the output of findPath. The output list is:
		 * 0. WordTraversal s = the starting word
		 * 1. WordTraversal e = the ending word, with parent information for traceback
		 * 2. SortedSet<WordTraversal> pq = the priority queue used to store the words that have been partially checked
		 * 3. Set<WordTraversal> alreadyChecked = the set of words that have already been checked.
		 *
		 * 2 and 3 are used by the spanning tree algorithm to avoid duplicating effort.
		 */
		List<Object> l;
		if (len == 0) l = findPath();
		else if (start.length == 0) l = findPath(len);
		else if (end.length == 0) l = findPath(start);
		else l = findPath(start, end);


		/*
		 * Spanning tree functionality not complete yet.
		 */

//		WordTraversal w = (WordTraversal) l.get(0);
//		@SuppressWarnings("unchecked")
//		SortedSet<WordTraversal> pq = (SortedSet<WordTraversal>) l.get(2);
//		@SuppressWarnings("unchecked")
//		Set<WordTraversal> alreadyChecked = (Set<WordTraversal>) l.get(3);
//		Set<WordTraversal> st = w.buildSpanningTree(pq, alreadyChecked);
//		System.out.println();
//		System.out.println("The spanning tree from " + String.valueOf(start) + " contains " + st.size() + " words.");
	}



	public static List<Object> findPath() throws IOException {
		// pick a random length in the interval [4,8]
		int len = 4 + (int) (Math.random() * 5);
		return findPath(len);
	}

	public static List<Object> findPath(int len) throws IOException{
		int dictLen = englishDict.get(len).size();
		char[] start, end;
		Iterator<String> it = englishDict.get(len).iterator();
		int i1, i2;
		do{
			System.out.println("Picking random words...");
			i1 = (int) (Math.random() * dictLen);
			i2 = (int) (Math.random() * dictLen);
		} while (i1 == i2);
		int i = 0;
		String s = it.next();
		// generate both words
		for (; i < Math.min(i1, i2); i++)
			s = it.next();
		start = s.toCharArray();
		for (i++; i < Math.max(i1, i2); i++)
			s = it.next();
		end = s.toCharArray();
		return findPath(start, end);
	}

	public static List<Object> findPath(char[] start) throws IOException{
		int len = start.length;

		Iterator<String> it = englishDict.get(len).iterator();
		int i1 = (int) (Math.random() * englishDict.get(len).size());
		String s = it.next();
		int i = 0;
		for (; i < i1; i++)
			s = it.next();
		char[] end = s.toCharArray();
		return findPath(start, end);
	}

	public static List<Object> findPath(char[] start, char[] end) throws IOException {
		int len = start.length;
//		use SortedSet and TreeSet instead of Queue and PriorityQueue because I need fast access to contains(o)
		SortedSet<WordTraversal> pq = new TreeSet<>();
		Set<WordTraversal> alreadyChecked = new HashSet<>();

		System.out.println("Trying to find a path from " + String.valueOf(start) + " to " + String.valueOf(end) + ".");

		WordTraversal startWord = new WordTraversal(start, null, end);
		WordTraversal endWord = new WordTraversal(end, null, end);
		lookup.get(len).put(startWord.getWord(), startWord);
//		don't do the same for endWord because we want to build a real endWord (with parent information) once we finish the search

		if (!englishDict.get(len).contains(endWord.getWord())){
			System.out.println("My dictionary doesn't contain the target word " + String.valueOf(end) + ".");
			System.out.println("My dictionary contained " + englishDict.get(len).size() + " words of length " + len + ".");
		}
		else{
			endWord = search(startWord, endWord, pq, alreadyChecked);
			if (endWord == null){
				System.out.println("I searched " + alreadyChecked.size() + " words.");
				System.out.println("I couldn't find a path from " + String.valueOf(start) + " to " + String.valueOf(end) + ".");
				System.out.println("My dictionary contained " + englishDict.get(len).size() + " words of length " + len + ".");
			}
			else{
				List<WordTraversal> backtrace = backtrace(endWord);
				System.out.println("Path from " + String.valueOf(start) + " to " + String.valueOf(end) + " uses " + backtrace.size() + " steps:");
				System.out.println();
				for (WordTraversal w : backtrace) System.out.println(w.word);
				System.out.println();
				System.out.println("My dictionary contained " + englishDict.get(len).size() + " words of length " + len + ".");
				System.out.println("I completely checked " + alreadyChecked.size() + " words and partially checked " + pq.size() + " words.");
			}
		}
		List<Object> l = new ArrayList<>();
		l.add(startWord);
		l.add(endWord);
		l.add(pq);
		l.add(alreadyChecked);
		return l;
	}
/*
 * Main searching function.
 *
 * There are three types of checks: Forwards, Sideways and Backwards.
 *
 * A Forwards check changes one char from the current word (which does NOT match the corresponding char of the target word)
 * to the corresponding char of the target word.
 *
 * A Sideways check changes one char from the current word (which does NOT match the corresponding char of the target word)
 * to a different char that does NOT match the corresponding char of the target word.
 *
 * A Backwards check changes one char from the current word (which DOES match the corresponding char of the target word)
 * to a char that does NOT match the corresponding char of the target word.
 *
 * After generating the new word, each type of check verifies that the new word is in the englishDict, and if it is,
 * adds it to the priority queue.
 *
 * This function uses a depth-first search algorithm modified to prefer Forwards checks to Sideways checks,
 * and Sideways checks to Backwards checks, which results in a faster search than a simple DFS,
 * while still always finding a path if one exists.
 */
	private static WordTraversal search(WordTraversal start, WordTraversal endWord
			, SortedSet<WordTraversal> pq, Set<WordTraversal> alreadyChecked){

				pq.add(start);
				search:
				while (!pq.isEmpty()){
					WordTraversal w = pq.first();
					if (w.equals(endWord)) return w;
					if (!w.checkedForwards){
						/*
						 * If we find a new word while checking forwards, checkForwards adds it to pq.
						 * Because it's closer to the destination, we have a new first element in pq.
						 */
						if (w.checkForwards(endWord, pq, alreadyChecked)) continue search;
					}
					if (!w.checkedSideways){
						/*
						 * If we find a new word while checking sideways, checkSideways adds it to pq.
						 * Because it's the same integer distance to the destination, it could either
						 * be before or after w in pq. To guarantee that w doesn't remain the first element in pq,
						 * we have to push w down manually. Since SortedSet only updates its order upon insertion,
						 * we have to remove w, push it down, and re-insert it.
						 */
						if (w.checkSideways(endWord, pq, alreadyChecked)) {
		//					move w down in the queue, so we can get a different element next time
							pq.remove(w);
							w.distance++;
							pq.add(w);
							continue search;
						}
					}
					if (!w.checkedBackwards){
						/*
						 * We don't need to do anything to the priority of w after checkingBackwards, because it's now
						 * been completely checked and will be removed from pq entirely.
						 */
						w.checkBackwards(endWord, pq, alreadyChecked);
					}

					alreadyChecked.add(w);
					pq.remove(w);

				}
				// we've exhausted the queue, without finding a path.
				return null;
			}





	public static List<WordTraversal> backtrace(WordTraversal endWord){
		LinkedList<WordTraversal> out = new LinkedList<>();
		out.addLast(endWord);
		WordTraversal n = endWord.parent;

		while (n != null){
			out.addFirst(n);
			n = n.parent;
		}
		return out;
	}

	private boolean checkForwards(WordTraversal endWord, SortedSet<WordTraversal> pq, Set<WordTraversal> alreadyChecked){
			boolean out = false;
			int len = endWord.getLength();
			for (int i = 0; i < endWord.getLength(); i++){
				if (this.word[i] == endWord.word[i]) continue;
				String ns = this.getWord(endWord.word[i], i);
				if (!englishDict.get(len).contains(ns) || alreadyChecked.contains(ns)) continue;
				WordTraversal n;
				if ((n = lookup.get(len).get(ns)) == null){
					n = new WordTraversal(ns,this,endWord.word);
					lookup.get(len).put(ns, n);
				}
				if (!pq.contains(n)){
					pq.add(n);
					n.updateGen();
					out = true;
				}
			}
			this.checkedForwards = true;
			return out;
		}

		private boolean checkSideways(WordTraversal endWord, SortedSet<WordTraversal> pq, Set<WordTraversal> alreadyChecked){
			boolean out = false;
			int len = endWord.getLength();
			for (int i = 0; i < endWord.getLength(); i++){
				if (this.word[i] == endWord.word[i]) continue;
				for (char c : ALPHABET){
					String ns = this.getWord(c, i);
					if (!englishDict.get(len).contains(ns) || alreadyChecked.contains(ns)) continue;
					WordTraversal n;
					if ((n = lookup.get(len).get(ns)) == null){
						n = new WordTraversal(ns,this,endWord.word);
						lookup.get(len).put(ns, n);
					}
					if (!pq.contains(n)){
						pq.add(n);
						n.updateGen();
						out = true;
					}
				}
			}
			this.checkedSideways = true;
			return out;
		}

		private boolean checkBackwards(WordTraversal endWord, SortedSet<WordTraversal> pq, Set<WordTraversal> alreadyChecked){
			boolean out = false;
			int len = endWord.getLength();
			for (int i = 0; i < endWord.getLength(); i++){
				if (this.word[i] != endWord.word[i]) continue;
				for (char c : ALPHABET){
					String ns = this.getWord(c, i);
					/*
					 * calls the non-reflexive WordTraversal.equals method. This is safe, because if it
					 * behaves unexpectedly, it will return false negatives, which will result in checking
					 * correctly later on.
					 */
					if (!englishDict.get(len).contains(ns) || alreadyChecked.contains(ns)) continue;
					WordTraversal n;
					if ((n = lookup.get(len).get(ns)) == null){
						n = new WordTraversal(ns,this,endWord.word);
						lookup.get(len).put(ns, n);
					}
					if (!pq.contains(n) && !alreadyChecked.contains(n)){
						pq.add(n);
						n.updateGen();
						out = true;
					}
				}
			}
			this.checkedBackwards = true;
			return out;
		}

	public int distance(char[] start, char[] end){
		int out = 0;
		for (int i = 0; i < start.length; i++){
			if (start[i] != end[i]) out++;
		}
		return out;
	}

	public int distance(WordTraversal start, WordTraversal end){
		return distance(start.word, end.word);
	}
	public int distance(WordTraversal start, char[] end){
		return distance(start.word, end);
	}

	/*
	 * Prototype code to count the number of words connected to a given starting word.
	 * Mostly, but not completely functional
	 */

	public Set<WordTraversal> buildSpanningTree(){
		return buildSpanningTreeSlow(new HashSet<WordTraversal>());
	}

	private Set<WordTraversal> buildSpanningTreeSlow(Set<WordTraversal> st){
		st.add(this);
		int len = this.getLength();
		for (int i = 0; i < len; i++){
			for (char c : ALPHABET){
				String ns = this.getWord(c, i);
				WordTraversal n;
				if (!englishDict.get(len).contains(ns)) continue;
				if ((n = lookup.get(len).get(ns)) == null){
					n = new WordTraversal(ns,this);
					lookup.get(len).put(ns, n);
				}
				if (!st.contains(n)){
					st.addAll(n.buildSpanningTreeSlow(st));
				}
			}
		}

		return st;
	}

	public static List<Set<WordTraversal>> partition(int len){
		Set<WordTraversal> words = new HashSet<>();
		for (String s : englishDict.get(len)){
			words.add(new WordTraversal(s, null, false));
		}
		List<Set<WordTraversal>> trees = new ArrayList<Set<WordTraversal>>();
		Iterator<WordTraversal> it = words.iterator();
		while (it.hasNext()){
			WordTraversal w = it.next();
			if (w.reached) continue;
			trees.add(w.buildSpanningTree());
		}
		return trees;
	}

	/*
	 * Prototype code to use the information from the search part of the program to speed the process of building a spanning tree.
	 * Not complete.
	 */

	private Set<WordTraversal> buildSpanningTreeUnfinished(SortedSet<WordTraversal> pq, Set<WordTraversal> alreadyChecked){
		int len = this.getLength();
		for (int i = 0; i < len; i++){
			for (char c : ALPHABET){
				String ns = this.getWord(c, i);
				WordTraversal n;
				if (!englishDict.get(len).contains(ns)) continue;
				if ((n = lookup.get(len).get(ns)) == null){
					n = new WordTraversal(ns,this);
					lookup.get(len).put(ns, n);
				}
				if (!pq.contains(n)) alreadyChecked.add(n);
			}
		}
		return alreadyChecked;
	}



	@Override
	public boolean equals(Object o){
		/*
		 * When the second line is not commented out, this equals method is not reflexive.
		 *
		 * In particular, String.equals(WordTraversal) will always return false,
		 * but WordTraversal.equals(String) will sometimes return true.
		 *
		 * By allowing w.equals(String s) to return true for w of type WordTraversal, we enable
		 * HashSet<String> st;
		 * WordTraversal w;
		 * st.contains(w) == true if st contains a string equal to w.getWord()
		 *
		 * However, the opposite will not hold:
		 * HashSet<WordTraversal> wt;
		 * String s;
		 * wt.contains(s) == false always.
		 *
		 * However, this behavior should not be relied upon, since it relies on the non-contracted fact that
		 * (at least my implementation of) HashSet uses key.equals(k) to resolve HashSet.contains(key),
		 * rather than the equally sensible k.equals(key). Because of this, I removed the non-reflexive behavior
		 */
		if (o instanceof WordTraversal) return this.getWord().equals(((WordTraversal) o).getWord());
//		else if (o instanceof String) return this.getWord().equals( (String) o);
		else return false;
	}

	@Override
	public int hashCode(){
		return this.getWord().hashCode();
	}

	@Override
	public int compareTo(WordTraversal o) {
//		negate these values if the priority queue is running backwards
		if (o.getWord().equals(this.getWord())) return 0; //used to maintain consistency with equals
		else return (int) Math.signum(this.distance - o.distance);
	}
	@Override
	public String toString(){
		return this.getWord();
	}
}

