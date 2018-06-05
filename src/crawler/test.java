package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tartarus.snowball.ext.PorterStemmer;

class test {
	static Map<Integer, HashMap<String, String>> result = new HashMap<Integer, HashMap<String, String>>();
	static Map<Integer, HashMap<String, Integer>> titles = new HashMap<Integer, HashMap<String, Integer>>();
	static Map<Integer, HashMap<String, Integer>> tdFrequency = new HashMap<Integer, HashMap<String, Integer>>();
	static Map<Integer, String> t=new HashMap<Integer, String>();
	static int n=0;
	/*public static void main(String[] args) throws Exception {
		int N = 0;
		String stopw=new String();
		Scanner scan = new Scanner(System.in);
		System.out.println("Input number of pages need to retrieve: ");
		N = Integer.parseInt(scan.next());
		Scanner scan_1=new Scanner(System.in);
		System.out.println("Input list of stop words(use space to seperate): " );
		stopw=scan_1.nextLine();

		myCrawler(N,stopw);

	}*/

	@SuppressWarnings("static-access")
	public static void myCrawler(int N,String stopw) throws Exception {
		ArrayList<String> robots = new ArrayList<String>();
		ArrayList<String> results = new ArrayList<String>();
		Queue<String> todo = new LinkedList<String>();
		ArrayList<String> duplicate = new ArrayList<String>();
		ArrayList<String> visited = new ArrayList<String>();
		ArrayList<String> disallowed = new ArrayList<String>();
		ArrayList<String> outside = new ArrayList<String>();
		ArrayList<String> files = new ArrayList<String>();
		ArrayList<String> img = new ArrayList<String>();
		ArrayList<String> brokenLinks = new ArrayList<String>();
		int delay=0;
		todo.add("https://s2.smu.edu/~fmoore/");

		Set<String> texts = new HashSet<String>();
		String[] stopwList=new String[10000];
		
		//set stop words into a set
		stopw=stopw.toLowerCase();
		stopwList=stopw.split(" ");
		//for(int i=0;i<stopwList.length;i++) {
		//System.out.println(stopwList[i]);}
		
		//use fake user-agent
		String USER_AGENT="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";
		// add robots.txt validation
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(new URL("https://s2.smu.edu/~fmoore/robots.txt").openStream()))) {
			String l = null;
			while ((l = in.readLine()) != null) {
				if (l.startsWith("Disallow:")) {
					robots.add(l.substring(10));
				}
				if(l.startsWith("Crawl-delay:")) {
					delay = Integer.parseInt(l.substring(12));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(robots);

		//int n = 0;
		while (!todo.isEmpty() && n < N) {

			try {
				// check if the url is allowed
				String tod = todo.poll();
				//System.out.println(tod);
				boolean flag = false;
				for (String rob : robots) {
					if (tod.indexOf(rob) != -1) {
						disallowed.add(tod);
						flag = true;
						break;
					}
				}
				if (flag)
					continue;
				//check out the mail links
				if (tod.startsWith("mailto")) {
					outside.add(tod);
					continue;
				}

				URL url = new URL(tod);

				// check out the broken links
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
					brokenLinks.add(tod);
					continue;
				}

				// check out of scope
				if (!(tod.startsWith("https://s2.smu.edu/~fmoore") 
						|| tod.startsWith("http://s2.smu.edu/~fmoore")
						|| tod.startsWith("https://lyle.smu.edu/~fmoore")
						|| tod.startsWith("http://lyle.smu.edu/~fmoore"))) {
					if (!outside.contains(tod)) {
						outside.add(tod);
						continue;
					}
				}

				// seperate url type
				if (!(tod.endsWith("html") 
						|| tod.endsWith("htm") 
						|| tod.endsWith("php") 
						|| tod.endsWith("txt")
						|| tod.equals("https://s2.smu.edu/~fmoore/"))) {
					// images or files
					if ((tod.endsWith("jpg") 
							|| tod.endsWith("gif") 
							|| tod.endsWith("png") 
							|| tod.endsWith("jpeg"))) {
						if (!img.contains(tod)) {
							img.add(tod);
						}
					} else {
						if (!files.contains(tod)) {
							files.add(tod);
						}
					}
					continue;
				}
				try {
					Connection connection = Jsoup.connect(tod).userAgent(USER_AGENT);
					Document document = Jsoup.connect(tod).get();
					String text = document.body().text();
					String title = document.title();
					String ti=title;

					// duplication detection
					if (!texts.add(text)) {
						duplicate.add(tod);
						continue;
					}
					//System.out.println(tod);
					//System.out.println("Title: "+title);
					//titles.put(n+1, title);

					if (!visited.add(tod)) {
						continue;
					}
					// System.out.println(tod);

					// stemming
					text = text.replaceAll("\\p{Punct}", " ");
					text = text.replaceAll("\\pN", "");
					text = text.replaceAll("\\n", "");
					text=text.toLowerCase();
					title = title.replaceAll("\\p{Punct}", " ");
					title = title.replaceAll("\\pN", "");
					title = title.replaceAll("\\n", "");
					title=title.toLowerCase();
					// System.out.println(text);
					String[] words = new String[10000];
					String[] tWords= new String[20];
					words = text.split("\\s+");
					tWords= title.split("\\s+");
					 /*for(int i=0;i<words.length;i++) { 
						 System.out.println(words[i]); 
					}
					 System.out.println("------------");*/
					ArrayList<String> stemmedw = new ArrayList<String>();
					for (int i = 0; i < words.length; i++) {
						if(!Arrays.asList(stopwList).contains(words[i])) {
							PorterStemmer stemmer = new PorterStemmer();
							stemmer.setCurrent(words[i]);
							stemmer.stem();
							String stemmedWord = stemmer.getCurrent();
							stemmedw.add(stemmedWord);
						}else {
							continue;
						}
					}
					ArrayList<String> stemmedt = new ArrayList<String>();
					for (int i = 0; i < tWords.length; i++) {
						if(!Arrays.asList(stopwList).contains(tWords[i])) {
							PorterStemmer tstemmer = new PorterStemmer();
							tstemmer.setCurrent(tWords[i]);
							tstemmer.stem();
							String stemmedTWord = tstemmer.getCurrent();
							stemmedt.add(stemmedTWord);
						}else {
							continue;
						}
					}
					 /*for(String s:stemmedw) { 
					  System.out.println(s); 
					  }*/
					 
					// create the term-document frequency matrix
					 int docID=n+1; 
					 int count=0;
					 String first20="";
					 t.put(docID, ti);
					 result.put(docID, new HashMap<String,String>());
					 tdFrequency.put(docID, new HashMap<String,Integer>());
					 titles.put(docID, new HashMap<String,Integer>());
					 for(String stemw: stemmedw) { 
						 if(count<20) {
							 first20=first20+" "+stemw;
							 count++;
						 }
						 HashMap<String, Integer> temp=tdFrequency.get(docID);
						 if(!temp.containsKey(stemw)) {
							 temp.put(stemw, 0);
						 }
						 temp.put(stemw, temp.get(stemw) + 1);
						 tdFrequency.put(docID, temp);
					 }
					 HashMap<String,String> r=new HashMap<String,String>();
					 r.put(tod, first20);
					 result.put(docID, r);
					 for(String stemt: stemmedt) {
						 HashMap<String, Integer> temp=titles.get(docID);
						 if(!temp.containsKey(stemt)) {
							 temp.put(stemt, 0);
						 }
						 temp.put(stemt, temp.get(stemt) + 1);
						 titles.put(docID, temp);
					 }
					 
					// get all urls in the current crawling page
					Elements links = document.select("a[href]");
					Elements links_1 = document.select("img[src]");
					for (Element link : links) {
						todo.add(link.attr("abs:href"));
					}
					for (Element link : links_1) {
						todo.add(link.attr("abs:src"));
					}

				} catch (Exception e) {
					continue;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			n++;
			
			//implement the delay request of the website
			if(delay!=0) {
				delay=delay*1000;
				try {   
					Thread.currentThread().sleep(delay); 
				}catch(Exception e){}
			}
		}
	
		/*for(Integer a:tdFrequency.keySet()) {
			Map<String, Integer> map = tdFrequency.get(a);
			System.out.println(a+" "+map);
		}*/
		/*boolean f=false;
		f=CSVUtils.exportCsv(new File("C:\\CSE\\7337\\project\\td frequency matrix.csv"),tdFrequency);
		f=CSVUtils.exportCsv(new File("C:\\CSE\\7337\\project\\titles td frequency matrix.csv"),titles);*/
		//clustering.clustering(n+1,titles,tdFrequency);
		//searchEngine.similarity(tdFrequency,titles,result);
		}
}