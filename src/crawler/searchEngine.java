package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.tartarus.snowball.ext.PorterStemmer;

public class searchEngine {
	static HashMap<Integer, Double> score= new HashMap<Integer, Double>();
	static Map<String,Integer> qtFrequency= new HashMap<String, Integer>();
	static Map<Integer, HashMap<String, String>> re=new HashMap<Integer, HashMap<String, String>>();
	static Map<Integer, String> tList=new HashMap<Integer, String>();
	static int counts=0;
	public static void queryProcess(String query, String stopw) throws Exception {
		//System.out.println(query);
		
		String[] stopwList=new String[10000];
		stopw=stopw.toLowerCase();
		stopwList=stopw.split(" ");
		
		query = query.replaceAll("\\p{Punct}", "");
		query = query.replaceAll("\\pN", "");
		query=query.toLowerCase();
		String[] qWords= new String[20];
		qWords= query.split(" ");
		ArrayList<String> stemmedw = new ArrayList<String>();
		for (int i = 0; i < qWords.length; i++) {
			if(!Arrays.asList(stopwList).contains(qWords[i])) {
				PorterStemmer stemmer = new PorterStemmer();
				stemmer.setCurrent(qWords[i]);
				stemmer.stem();
				String stemmedWord = stemmer.getCurrent();
				stemmedw.add(stemmedWord);
			}else {
				continue;
			}
		}
		for(String stemw: stemmedw) {
			if(!qtFrequency.containsKey(stemw)) {
				 qtFrequency.put(stemw, 0);
			 }
			qtFrequency.put(stemw, qtFrequency.get(stemw) + 1);
		}
	}
	public static void similarity() throws Exception {
		double cq,dq,qp,cd,dd,dp,p;
		double sumq=0;
		double sumd=0;
		double sump=0;
		Map<String, Double> cosq= new HashMap<String, Double>();
		HashMap<String, Double> temp= new HashMap<String, Double>();
		Map<Integer, HashMap<String, Double>> cosd = new HashMap<Integer, HashMap<String, Double>>();
//		HashMap<Integer, Double> score= new HashMap<Integer, Double>();
		Map<String, Integer> qFrequency= new HashMap<String, Integer>();
		Map<Integer, HashMap<String, Integer>> tFrequency=new HashMap<Integer, HashMap<String, Integer>>();
		Map<Integer, HashMap<String, Integer>> dFrequency=new HashMap<Integer, HashMap<String, Integer>>();
		/*Map<Integer, HashMap<String, String>> re=new HashMap<Integer, HashMap<String, String>>();
		Map<Integer, String> tList=new HashMap<Integer, String>();*/
		tFrequency=test.titles;
		dFrequency=test.tdFrequency;
		re=test.result;
		tList=test.t;
		qFrequency=searchEngine.qtFrequency;
		for(String s:qFrequency.keySet()) {
			qp=Math.pow(qFrequency.get(s),2);
			sumq=sumq+qp;
		}
		dq=Math.sqrt(sumq);
		for(String s:qFrequency.keySet()) {
			cq=qFrequency.get(s)/dq;
			cosq.put(s, cq);
		}
		//System.out.println(cosq);
		//System.out.println("---------------");
		for(Integer a:dFrequency.keySet()) {
			Map<String, Integer> map= dFrequency.get(a);
			//System.out.println(map);
			for(String st:map.keySet()) {
				dp=Math.pow(map.get(st), 2);
				sumd=sumd+dp;
			}
			dd=Math.sqrt(sumd);
			sumd = 0;
			//System.out.println(sumd);
			for(String st:map.keySet()) {
				cd=map.get(st)/dd;
				temp.put(st, cd);
			}
			//System.out.println(temp);
			//System.out.println("---------------");
			cosd.put(a, temp);
			temp = new HashMap();
		}
		/*System.out.println(cosd);
		System.out.println("---------------");*/
		for(Integer b:cosd.keySet()) {
			Map<String, Double> ma= cosd.get(b);
			for(String str:cosq.keySet()) {
				if(ma.containsKey(str)) {
					p=ma.get(str)*cosq.get(str);
					sump=sump+p;
				}
			}
			
			//System.out.println(score.get(b));
			if(score.containsKey(b)) {
				score.put(b, Math.max(score.get(b), sump));
			}else {
				score.put(b, sump);
				//System.out.println(b);
			}
			
			if(score.get(b)==null||score.get(b)==0) {
				score.remove(b);
			}
			
			sump = 0;
		}
		for(Integer c:tFrequency.keySet()) {
			Map<String, Integer> m= tFrequency.get(c);
			//System.out.println(cosq.keySet().size());
			for(String ss:cosq.keySet()) {
				if(m.containsKey(ss)) {
					if(!score.containsKey(c)) {
						score.put(c, 0.0);
					}
					score.put(c, score.get(c)+0.25);
					break;
				}
			}
		}
		//System.out.println(score);
		PriorityQueue<Integer> queue = new PriorityQueue<Integer>((i1, i2) -> score.get(i1).compareTo(score.get(i2)));
		for(int key : score.keySet()) {
			queue.add(key);
			if(queue.size() > 6) {
				queue.poll();
			}
		}
		List<Integer> dec=new ArrayList<Integer>() ;
		while(!queue.isEmpty()) {
			int doc = queue.poll();
			dec.add(doc);
		}
		/*for(int k=dec.size()-1;k>=0;k--) {
			if(score.get(dec.get(k))==0) {
				break;
			}
			//counts++;
			System.out.println("doc"+dec.get(k)+"'s score is "+score.get(dec.get(k)));
			System.out.println("Title: "+tList.get(dec.get(k))+re.get(dec.get(k)));
		}*/
		
		//System.out.println(score);
	}
	
	/*public static void thesaurusExpansion(String query) throws Exception {
		Map<String,String[]> thesaurus=new HashMap<String,String[]>();
		String[] beautiful= {"nice","fancy"};
		String[] chapter= {"chpt"};
		String[] chpt= {"chapter"};
		String[] responsible= {"owner","accountable"};
		String[] freemanmoore= {"freeman","moore"};
		String[] dept= {"department"};
		String[] brown= {"beige","tan","auburn"};
		String[] tues= {"Tuesday"};
		String[] sole= {"owner","single","shoe","boot"};
		String[] homework= {"hmwk","home","work"};
		String[] novel= {"book","unique"};
		String[] computer= {"cse"};
		String[] story= {"novel","book"};
		String[] hocuspocus= {"magic","abracadabra"};
		String[] thisworks= {"this","work"};
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
		for(String b:thesaurus.keySet()) {
			if(query.indexOf(b)!=-1) {
				while()
				query.replace(b, replacement)
			}
		}
		ArrayList<String> stemmedw = new ArrayList<String>();
		for(String a:thesaurus.keySet()) {
			for (int i = 0; i < thesaurus.get(a).length; i++) {
				PorterStemmer stemmer = new PorterStemmer();
				stemmer.setCurrent(thesaurus.get(a)[i]);
				stemmer.stem();
				String stemmedWord = stemmer.getCurrent();
				stemmedw.add(stemmedWord);
			}
		}
		for(String b:thesaurus.keySet()) {
			if(searchEngine.qtFrequency.containsKey(b)) {
				
			}
		}
	}*/
}
