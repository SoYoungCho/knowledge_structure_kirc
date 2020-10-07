/********************************************************************************************************************
 * FILENAME: writeWebgraph.java
 * 
 * ROLE: it generates knowledge structure graph of .html format
 * 		
 * VARIABLES: -
 * 
 * METHODS:
 * 	public writeWebgraph(config config, PRXKSGenerator PRXModel)			//	a constructor. 
 * 	public void writeData(config config, PRXKSGenerator PRXModel)			//	write SampleText_data.js which would be refered to SampleText_graph.html
 * 	public void writeHTML(config config, PRXKSGenerator PRXModel)			//	write SampleText_graph.html
 * 
 * COMMENTS:
 * 	The JPathFinder.jar file is in 'lib' folder.
 * 	Refer to [http://visjs.org/]
 * 
 ********************************************************************************************************************/

package option.com.kirc.core.output.webgraph;

import com.kirc.core.utils.FileUtils;

import f1.com.kirc.core.config.Config;
import f4.com.kirc.core.model.*;

public class writeWebgraph{
	public writeWebgraph(Config config, PRXKSGenerator PRXModel)  {
		
		writeData(config, PRXModel);
		
		writeHTML(config, PRXModel);
		
		System.out.println("OPTION) HTML file for Knowledge Structure has been written!\n");
	}
	
	//test
	
	public void writeData(Config config, PRXKSGenerator PRXModel){
		
		String outputPath = config.webgraphLocation + "/data/"+config.targetFileName+"_data.js";

		FileUtils fu = new FileUtils();
		Vertex v;
		
		/* GRAPH DATA OUTPUT*/
		String strtemp = "var nodes = [\n";
		
		//for(int i=0;i<PRXModel.ksModel.getVertexes().size();i++){
		for(int i=0;i<config.top_N_nodes;i++){
			v = PRXModel.ksModel.getVertexes().get(i);
			strtemp=strtemp+"\t{id: "+v.getId()+",\t";
			strtemp=strtemp+"label: '"+v.getName()+"',\t";
			strtemp=strtemp+"title: '"+v.getFrequency()+"'}";
			if(i<PRXModel.ksModel.getVertexes().size()-1) strtemp=strtemp+",\n";
		}
		strtemp = strtemp + "\n];\n\n";
		
		/* TXT EDGES OUTPUT*/
		Edge e;
		if(config.web_directed_flag){				//when the graph is directed
			strtemp = strtemp+"var edges = [\n";
			for(int i=0;i<PRXModel.ksModel.getEdges().size();i++){
				e = PRXModel.ksModel.getEdges().get(i);
				strtemp=strtemp+"\t{from: "+e.getSource().getId()+",\t";
				strtemp=strtemp+"to: "+e.getDestination().getId()+",\t";
				strtemp=strtemp+"label: '"+Math.round(e.getWeight()*100)/100.0+"',\t";
				strtemp=strtemp+"arrows: 'to'}";
				if(i<PRXModel.ksModel.getEdges().size()-1) strtemp=strtemp+",\n";
			}
			strtemp = strtemp + "\n];";
			fu.writeStringToFile(strtemp, outputPath);		
		}else{										//when the graph is not directed
			strtemp = strtemp+"var edges = [\n";
			for(int i=0;i<PRXModel.ksModel.getEdges().size();i++){
				e = PRXModel.ksModel.getEdges().get(i);
				strtemp=strtemp+"\t{from: "+e.getSource().getId()+",\t";
				strtemp=strtemp+"to: "+e.getDestination().getId()+",\t";
				strtemp=strtemp+"label: '"+Math.round(e.getWeight()*100)/100.0+"'";
				strtemp=strtemp+"}";
				if(i<PRXModel.ksModel.getEdges().size()-2) strtemp=strtemp+",\n";
				i++;
			}
			strtemp = strtemp + "\n];";
			fu.writeStringToFile(strtemp, outputPath);		
		}
	}
	
	public void writeHTML(Config config, PRXKSGenerator PRXModel) {
	
		String outputPath = config.webgraphLocation + "/"+config.targetFileName+"_graph.html";
		FileUtils fu = new FileUtils();
		
		/* write down .html*/
		String forceDirection = "none";
		String gravitationalConstant = "1.35";
		String springLength = "235";
		String minVelocity = "0.75";

		String strtemp = "";
		
		
		
		
		strtemp = "<!doctype html><html><head><title>"+config.targetFileName+" Knowledge Structure</title>"
				+ "<script type=\"text/javascript\" src=\""+config.libLocation+"/drawing/vis.js\"></script>"
				+ "<link href=\""+config.libLocation+"/drawing/vis.css\" rel=\"stylesheet\" type=\"text/css\" />"
				+ "<script src=\""+config.webgraphLocation+"/data/"+config.targetFileName+"_data.js\" charset="+"\\\""+config.encodetype+"\\\""+"></script>"
				+ "<style type=\"text/css\">#mynetwork {width: 100%;height: 1000px;border: 1px solid lightgray;}</style></head><body>"
				+ "<p>This is the Knowledge Structure of "+config.targetFileName+".</p><div id=\"mynetwork\"></div>"
				+ "<script type=\"text/javascript\">"
				+ "var container = document.getElementById('mynetwork');"
				+ "var data = { nodes: nodes, edges: edges }; var options = { \"edges\": {\"smooth\": {\"forceDirection\": \""+forceDirection+"\"}},"
				+ "\"physics\": {\"barnesHut\": {\"centralGravity\": "+gravitationalConstant+",\"springLength\": "+springLength+", \"avoidOverlap\": 1},\"minVelocity\": "+minVelocity+"}}; var network = new vis.Network(container, data, options);</script>"
				+ "<script src=\".graph/drawing/googleAnalytics.js\"></script></body></html>";		

		fu.writeStringToFile(strtemp, outputPath);
	}
}