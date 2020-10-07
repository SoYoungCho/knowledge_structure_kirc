/********************************************************************************************************************
 * FILENAME: GraphUtil.java
 * 
 * ROLE: GraphUtil is used to set vertexes and edges
 * 
 * VARIABLES: -
 * 
 * METHODS:
 * 	public static ArrayList<String> getKeysByValue(Map<String, String> map, String value) 							//
 * 	public static Edge addLane(KSModel ksModel, String laneId, int sourceLocNo, int destLocNo, double duration)		//	
 * 	public static int getNodeId(List<Vertex> vertexes, String name)													//
 * 	public static List<Vertex> addNodes(ArrayList<Map.Entry<String, Integer>> sortedValue)							//
 * 	public static List<Edge> addEdges(KSModel ksModel, String fileLocation)											//	
 * 	public static List<Edge> addEdgesFromString(KSModel ksModel, String source)										//
 * 	public static void printMap(HashMap<?, ?> map)																	//
 * 
 ********************************************************************************************************************/


package f4.com.kirc.core.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import f4.com.kirc.core.model.Edge;
import f4.com.kirc.core.model.KSModel;
import f4.com.kirc.core.model.Vertex;

import java.util.Set;

public class GraphUtil {
	
	public static ArrayList<String> getKeysByValue(Map<String, String> map, String value) {
	     ArrayList<String> keys = new ArrayList<String>();
	     
	     for (Entry<String, String> entry : map.entrySet()) {
	         if (value.equals(entry.getValue())) {
	             keys.add(entry.getKey());
	         }
	     }
	     return keys;
	}
	
	public static Edge addLane(KSModel ksModel, String laneId, int sourceLocNo, int destLocNo, double duration) {
		
		Edge lane = new Edge(laneId, ksModel.getVertexes().get(sourceLocNo), ksModel.getVertexes().get(destLocNo), duration);
				
		return lane;
	}
	
	public static int getNodeId(List<Vertex> vertexes, String name) {

		int i=0;
		for(Vertex v : vertexes) {
			if(name.equals(v.getName()))	break;
			else 							i++;
		}
		
		int result;
		if(i == vertexes.size())			result = -1;
		else								result = i;

		return result;
	}
	
	public static List<Vertex> addNodes(ArrayList<Map.Entry<String, Float>> sortedValue) {
	
		List<Vertex> result = new ArrayList<Vertex>();
		
		for (int i = 0; i < sortedValue.size(); i++) {
	    	Map.Entry<String, Float> aMap = sortedValue.get(i);
 			String key = (String) aMap.getKey();	
 			Float frequency = (Float) aMap.getValue();
 			Vertex location = new Vertex(Float.toString(i), key, frequency);
 			result.add(location);
	    }
		return result;
	}
	
	public static List<Edge> addEdges(KSModel ksModel, String fileLocation){
	
		List<Edge> result = new ArrayList<Edge>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileLocation));
			
			String line = null;
			int lineCnt = 1, edgeCnt = 1;
			while ((line = br.readLine()) != null) {
				if(lineCnt >= 11) {				
					String[] temp = line.split("\t");				
					if(temp[0].equals(""))		break;
					int src = Integer.parseInt(temp[0])-1, dst = Integer.parseInt(temp[1])-1;
					double distance = Double.parseDouble(temp[2]);
					result.add(addLane(ksModel, Integer.toString(edgeCnt), src, dst, distance));
					result.add(addLane(ksModel, Integer.toString(edgeCnt++), dst, src, distance));
				}			
				lineCnt++;
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Edge> addEdgesFromString(KSModel ksModel, String source){
		
		List<Edge> result = new ArrayList<Edge>();
		int edgeCnt = 1;
		String [] paras = source.split("\n");
		for(int i = 10; i<paras.length; i++){	
			String[] temp = paras[i].split("\t");				
			if(temp[0].equals(""))				break;
			int src = Integer.parseInt(temp[0])-1, dst = Integer.parseInt(temp[1])-1;
			double distance = Double.parseDouble(temp[2]);
			result.add(addLane(ksModel, Integer.toString(edgeCnt), src, dst, distance));
			result.add(addLane(ksModel, Integer.toString(edgeCnt++), dst, src, distance));		
		}	
		return result;
	}
	
	public static void printMap(HashMap<?, ?> map) {
		
		Set<?> set = map.entrySet();
		Iterator<?> it = set.iterator();
		while(it.hasNext()) {
			Map.Entry<?, ?> e = (Map.Entry<?, ?>)it.next();
			System.out.println(e.getKey() + "\t" + map.get(e.getKey()));
		}
	}
	public static String printKsEdges(PRXKSGenerator prxks){

		StringBuilder strtemp = new StringBuilder();

		for(int i=0 ; i<prxks.ksModel.getEdges().size() ; i+=2){
			strtemp.append(prxks.ksModel.getEdges().get(i).getSource().toString() + ",");
			strtemp.append(prxks.ksModel.getEdges().get(i).getDestination().toString()+ ",");
			strtemp.append(Math.round(prxks.ksModel.getEdges().get(i).getWeight()*100)/100.00+ "\n");
		}
		System.out.println("strtmp: " + strtemp);
		return strtemp.toString();
	}
	
	public static String getEdges(PRXKSGenerator PRXModel){

		String strtemp = "";

		for(int i=0;i<PRXModel.ksModel.getEdges().size();i+=2){
			strtemp += PRXModel.ksModel.getEdges().get(i).getSource().toString() + ",";
			strtemp += PRXModel.ksModel.getEdges().get(i).getDestination().toString()+ ",";
			strtemp += Math.round(PRXModel.ksModel.getEdges().get(i).getWeight()*100)/100.0+ "\n";
		}
		//System.out.println(strtemp);
		return strtemp;
	}
}