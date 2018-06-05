package crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class clustering {
	public static void clustering() throws Exception {
		int n=test.n+1;
		Map<Integer, HashMap<String, Integer>> dFrequency=new HashMap<Integer, HashMap<String, Integer>>();
		dFrequency=test.tdFrequency;
		Map<Integer, HashMap<String, Double>> leaderFollower = new HashMap<Integer, HashMap<String, Double>>();
		//HashMap<String, Double> temp=new HashMap<String, Double>();
		Random ran = new Random();  
        Set<Integer> leaderSet = new TreeSet<>();  
        while (true) {  
            int a = ran.nextInt(n) + 1;  
            leaderSet.add(a);  
            if (leaderSet.size() > 4) {   
                break;  
            }  
        }
        //System.out.print(leaderSet); 
        Map<Integer, Integer> leaders= new HashMap<Integer, Integer>();
        Map<Integer, Double> scores= new HashMap<Integer, Double>();
        for(Integer i:dFrequency.keySet()) {
        	double[] d=new double[5];
        	int[] bak=new int[5];
        	int ii=0;
        	for(Integer a:leaderSet) {
        		Map<String, Integer> leader = dFrequency.get(a);
        		Map<String, Integer> map = dFrequency.get(i);
        		double sum=0;
        		for(String s:leader.keySet()) {
        			double dist;
        			if(map.containsKey(s)) {
        				dist=Math.pow((leader.get(s)-map.get(s)),2);
        			}else {
        				dist=Math.pow((leader.get(s)-0),2);
        			}
        			sum=sum+dist;
        		}
        		for(String st:map.keySet()) {
        			double dis;
        			if(!leader.containsKey(st)) {
        				dis=Math.pow((map.get(st)-0),2);
        				sum=sum+dis;
        			}
        		}
        		double distance=Math.sqrt(sum);
        		
        		d[ii]=distance;
        		bak[ii]=a;
        		ii++;
        	}
        	for(int x=0;x<d.length;x++) {
        		for(int y=0;y<d.length-x-1;y++) {
        			if(d[y]>d[y+1]) {
            			double tem=d[y];
            			d[y]=d[y+1];
            			d[y+1]=tem;
            			int t=bak[y];
            			bak[y]=bak[y+1];
            			bak[y+1]=t;
            		}
        		}
        	}
        	leaders.put(i,bak[0]);
        	scores.put(i, d[0]);
        }
        
        //System.out.println(leaders);
        //System.out.println(scores);
        for(Integer j:leaders.keySet()) {
        	HashMap<String, Double> temp=new HashMap<String, Double>();
        	if(!leaderFollower.containsKey(leaders.get(j))) {
        		temp=new HashMap<String, Double>();
        		temp.put("doc"+j, scores.get(j));
        	}else {
        		temp=leaderFollower.get(leaders.get(j));
        		temp.put("doc"+j, scores.get(j));
        	}
        	leaderFollower.put(leaders.get(j), temp);
        }
        for(Integer a:leaderFollower.keySet()) {
			Map<String, Double> map = leaderFollower.get(a);
			System.out.println("doc"+a+": "+map);
		}
	}
}
