package crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class main {
	static Map<String, String[]> thesaurus = new HashMap<String, String[]>();

	public static void main(String[] args) throws Exception {
		String[] beautiful = { "nice", "fancy" };
		String[] chapter = { "chpt" };
		String[] chpt = { "chapter" };
		String[] responsible = { "owner", "accountable" };
		String[] freemanmoore = { "freeman", "moore" };
		String[] dept = { "department" };
		String[] brown = { "beige", "tan", "auburn" };
		String[] tues = { "Tuesday" };
		String[] sole = { "owner", "single", "shoe", "boot" };
		String[] homework = { "hmwk", "home", "work" };
		String[] novel = { "book", "unique" };
		String[] computer = { "cse" };
		String[] story = { "novel", "book" };
		String[] hocuspocus = { "magic", "abracadabra" };
		String[] thisworks = { "this", "work" };
		thesaurus.put("beautiful", beautiful);
		thesaurus.put("chapter", chapter);
		thesaurus.put("chpt", chpt);
		thesaurus.put("responsible", responsible);
		thesaurus.put("freemanmoore", freemanmoore);
		thesaurus.put("dept", dept);
		thesaurus.put("brown", brown);
		thesaurus.put("tues", tues);
		thesaurus.put("sole", sole);
		thesaurus.put("homework", homework);
		thesaurus.put("novel", novel);
		thesaurus.put("computer", computer);
		thesaurus.put("story", story);
		thesaurus.put("hocuspocus", hocuspocus);
		thesaurus.put("thisworks", thisworks);

		int N = 60;
		String stopw = new String();
		String query = new String();
		Scanner scan = new Scanner(System.in);
		System.out.println("Input list of stop words(use space to seperate): ");
		stopw = scan.nextLine();
		test.myCrawler(N, stopw);
		clustering.clustering();
		String isStopCommand = "";
		while (true) {
			searchEngine.score = new HashMap<Integer,Double>();
			searchEngine.qtFrequency= new HashMap<String, Integer>();
			//searchEngine.re=new HashMap();
			//searchEngine.tList=new HashMap();
			Scanner scan_1 = new Scanner(System.in);
			System.out.println("Input query words(use space to seperate): ");
			query = scan_1.nextLine();
			
			isStopCommand = query;
			isStopCommand = isStopCommand.toLowerCase();
			if (isStopCommand == "stop") {
				break;
			}
			//System.out.println(query);
			searchEngine.queryProcess(query, stopw);
			searchEngine.similarity();
			//System.out.println(query);
			//System.out.println(searchEngine.score.keySet().size());
			if (searchEngine.score.keySet().size() < 3) {
				String[] sq = query.split(" ");
				//System.out.println(query);
				
				//System.out.println("******");
				//System.out.println(sq[1]);
				for (String b : sq) {
					if (!(thesaurus.get(b) == null || thesaurus.get(b).length == 0)) {
						for (String s : thesaurus.get(b)) {
							query=query.replace(b, s);
							//System.out.println(isStopCommand);
							searchEngine.queryProcess(query, stopw);
							searchEngine.similarity();
						}
					}
					if (searchEngine.score.keySet().size() >= 3) {
						break;
					}
				}
			}
			// sort
			//System.out.println(searchEngine.score);
			PriorityQueue<Integer> queue = new PriorityQueue<Integer>(
					(i1, i2) -> searchEngine.score.get(i1).compareTo(searchEngine.score.get(i2)));
			for (int key : searchEngine.score.keySet()) {
				queue.add(key);
				if (queue.size() > 6) {
					queue.poll();
				}
			}
			List<Integer> dec = new ArrayList<Integer>();
			while (!queue.isEmpty()) {
				int doc = queue.poll();
				dec.add(doc);
			}
			for (int k = dec.size() - 1; k >= 0; k--) {
				if (searchEngine.score.get(dec.get(k)) == 0) {
					break;
				}
				// searchEngine.counts++;
				System.out.println("doc" + dec.get(k) + "'s score is " + searchEngine.score.get(dec.get(k)));
				System.out.println("Title: " + searchEngine.tList.get(dec.get(k)) + searchEngine.re.get(dec.get(k)));
			}
			if(searchEngine.score.keySet().size()==0) {
				System.out.println("There is no document matching!");
			}
		}
	}
}
