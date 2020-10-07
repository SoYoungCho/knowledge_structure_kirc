/********************************************************************************************************************
 * FILENAME: outputInterface.java
 * 
 * ROLE: it is an interface to print out the result of this program
 * 		
 * VARIABLES:
 * 	public String terms																//	result in list of terms separeted by comma
 * 	public String links																//	result in list of edges
 * 	public config config															//	configurations
 * 	public PRXKSGenerator PRXModel													//	prx model
 * 	public int number_of_nodes														// 	number of nodes
 * 
 * METHODS:
 * 	public outputInterface(config config, PRXKSGenerator PRXModel)					//	constructor.
 * 	public void setPrintData(config config, PRXKSGenerator PRXModel)				//	setting data befor writing into file
 * 	public void printOUT(config config, PRXKSGenerator PRXModel)					//	converting all results into JSON-format and print
 * 	private String json_f1()														//	json_f1 represents { "nodes":[{"name":"nodeName","group":nodeGoup}, "edges":[{}]}
 * 	private String json_f2()														//	json_f2 represents {"nodes":["links"]}
 * 	public void printtoDB()															// 	put all the results into Database
 * 
 * COMMENTS:
 * 	JSON FORMAT 1	=> { "nodes":[{"id":"0","name":"appl","frequency":"3"}],"links":[{"id":"1","source":"0","destination":"2","weight":"1.0}] }. It describes node ID.
 * 	JSON FORMAT 2	=> { "nodes":[{"id":"0","name":"appl","frequency":"3"}],"links":[{"id":"1","source":"appl","destination":"banana","weight":"1.0}] }. It describes node NAME.
 * 	TXT VERTEXES	=> ID	NAME	FREQUENCY
 * 	TXT EDGES		=> ID	WEIGHT	SOURCE	DESTINATION
 * 	printtoDB(config, PRXModel) is not complete since we do not know which database would be used. Thus, we have put dummy variables for convinience.
 * 
 ********************************************************************************************************************/

package f5.com.kirc.core.output;

import com.kirc.core.utils.FileUtils;

import f1.com.kirc.core.config.Config;
import f4.com.kirc.core.model.*;

public class outputInterface {

	public String terms;
	public String links;
	public Config config;
	public PRXKSGenerator PRXModel;
	public int number_of_nodes;

	public outputInterface(Config config, PRXKSGenerator PRXModel){		

		//System.out.println("STEP 5. Writing the results...");

		/* set data to print */
		setPrintData(config, PRXModel);

		/* print to JSON and TXT format */
		printOUT(config, PRXModel);

//		System.out.println("\n--------------------------------------------------------");
//		System.out.println("Conversion from TEXT to KNOWLEDGE STRUCTURE complete!");
//		System.out.println("--------------------------------------------------------");
	}

	public void setPrintData(Config config, PRXKSGenerator PRXModel){
		this.PRXModel = PRXModel;
		this.config = config;
		this.terms = PRXModel.terms;
		this.links = PRXModel.links;
		this.number_of_nodes = config.top_N_nodes;
	}

	public void printOUT(Config config, PRXKSGenerator PRXModel) {

		String outputPath;
		FileUtils fu = new FileUtils();

		/* JSON format 1 */
		if(config.output_json_f1_flag){
			outputPath = config.outputLocation + "/"+config.targetFileName+"_JSON_f1.txt";
			fu.writeStringToFile(json_f1(), outputPath);
		}

		/* JSON format 2 */
		if(config.output_json_f2_flag){
			outputPath = config.outputLocation + "/"+config.targetFileName+"_JSON_f2.txt";
			fu.writeStringToFile(json_f2(), outputPath);
		}	

		/* TXT VERTICES OUTPUT*/
		String strtemp="";
		if(config.output_txt_vertices_flag){
			strtemp = "ID\tNAME\tFREQUENCY\n";
			for(int i=0;i<this.PRXModel.ksModel.getVertexes().size();i++){
				strtemp=strtemp+this.PRXModel.ksModel.getVertexes().get(i).getId()+"\t";
				strtemp=strtemp+this.PRXModel.ksModel.getVertexes().get(i).getName()+"\t";
				strtemp=strtemp+this.PRXModel.ksModel.getVertexes().get(i).getFrequency();
				if(i<this.PRXModel.ksModel.getVertexes().size()-1) strtemp=strtemp+"\n";
			}
			outputPath = config.outputLocation + "/"+config.targetFileName+"_VERTICES.txt";
			fu.writeStringToFile(strtemp, outputPath);
			System.out.println("\tVERTICES: TXT file has been written!");
		}

		/* TXT EDGES OUTPUT*/
		if(config.output_txt_edges_flag){
			strtemp="ID\tSOURCE\tDESTINATION\tWEIGHT\n";
			for(int i=0;i<this.PRXModel.ksModel.getEdges().size();i++){
				strtemp=strtemp+this.PRXModel.ksModel.getEdges().get(i).getId()+"\t";
				strtemp=strtemp+this.PRXModel.ksModel.getEdges().get(i).getSource()+"\t";
				strtemp=strtemp+this.PRXModel.ksModel.getEdges().get(i).getDestination()+"\t";
				strtemp=strtemp+Math.round(this.PRXModel.ksModel.getEdges().get(i).getWeight()*100)/100.0;
				if(i<this.PRXModel.ksModel.getEdges().size()-1) strtemp=strtemp+"\n";
			}
			outputPath = config.outputLocation + "/"+config.targetFileName+"_EDGES.txt";
			fu.writeStringToFile(strtemp, outputPath);
			System.out.println("\tEDGES: TXT file has been written!");

		}

		/* KS Graph Model OUTPUT
		strtemp="";
		for(int i=0;i<this.PRXModel.ksModel.getEdges().size();i+=2){
			strtemp=strtemp+this.PRXModel.ksModel.getEdges().get(i).getId()+",";
			strtemp=strtemp+this.PRXModel.ksModel.getEdges().get(i).getSource()+",";
			strtemp=strtemp+this.PRXModel.ksModel.getEdges().get(i).getDestination()+",";
			strtemp=strtemp+Math.round(this.PRXModel.ksModel.getEdges().get(i).getWeight()*100)/100.0;
			if(i<this.PRXModel.ksModel.getEdges().size()-1) strtemp=strtemp+"\n";
		}
		outputPath = config.outputLocation + "/"+config.targetFile.getName().split(".txt")[0]+"_KSmodel.txt";
		fu.writeStringToFile(strtemp, outputPath);
		System.out.println("\tKS model: TXT file has been written!");
		 */


		/* R SCRIPT OUTPUT*/ 
		if(config.output_R_flag){
			strtemp="#first install.packages(\"igraph\") \n library(igraph) \n g <- make_graph(c(";
			for(int i=0;i<this.PRXModel.ksModel.getEdges().size();){
				strtemp=strtemp+"\"";
				strtemp=strtemp+this.PRXModel.ksModel.getEdges().get(i).getSource()+"\",";
				strtemp=strtemp+"\"";
				strtemp=strtemp+this.PRXModel.ksModel.getEdges().get(i).getDestination()+"\"";
				i++;
				if(i<this.PRXModel.ksModel.getEdges().size()) strtemp=strtemp+",";
			}

			strtemp=strtemp+("), directed = FALSE) \n "
					+ "palette <- c(\"white\",\"ivory3\",\"mediumpurple\",\"skyblue2\",\"palegreen\",\"yellow\",\"sandybrown\",\"sienna1\",\"tomato\",\"firebrick1\") \n"
					+"E(g)$weight <- c(");

			for(int i=0;i<this.PRXModel.ksModel.getEdges().size();){
				strtemp=strtemp+Math.round(this.PRXModel.ksModel.getEdges().get(i).getWeight()*100)/100.0;
				i++;
				if(i<this.PRXModel.ksModel.getEdges().size()) strtemp=strtemp+",";
			}

			strtemp=strtemp+(") \n "
					+"pdf(file='"+config.graphOutputLocation.replace("\\", "/")+"/"+config.targetFileName+"_"+config.cot_mode+".pdf',width = 15, height = 15)\n"
					+"par(mfrow=c(2,2)) \n"
					+"# degree centrality \n"
					+"c.d   <- degree(g)\n"
					+"col <- as.integer(9*(c.d-min(c.d))/diff(range(c.d))+1)\n"
					+"set.seed(1)\n"
					+"plot(g,vertex.color=palette[col],main=\"Degree Centrality\",vertex.label.color=\"black\", layout=layout.fruchterman.reingold)\n"
					+"# eigenvalue centrality\n"
					+"c.e   <- evcent(g)$vector\n"
					+"col <- as.integer(9*(c.e-min(c.e))/diff(range(c.e))+1)\n"
					+"set.seed(1)\n"
					+"plot(g,vertex.color=palette[col],main=\"Eigenvalue Centrality\",vertex.label.color=\"black\",layout=layout.fruchterman.reingold)\n"
					+"# betweenness centrality\n"
					+"c.b <- betweenness(g, directed = FALSE)\n"
					+"col <- as.integer(9*(c.b-min(c.b))/diff(range(c.b))+1)\n"
					+"set.seed(1)\n"
					+"plot(g,vertex.color=palette[col],main=\"Betweenness Centrality\",vertex.label.color=\"black\",layout=layout.fruchterman.reingold)\n"
					+"# closeness centrality\n"
					+"c.c <- closeness(g)\n"
					+"col <- as.integer(9*(c.c-min(c.c))/diff(range(c.c))+1)\n"
					+"set.seed(1)\n"
					+"plot(g,vertex.color=palette[col],main=\"Closeness Centrality\",vertex.label.color=\"black\",layout=layout.fruchterman.reingold)\n"
					+"dev.off()\n");
			outputPath = config.outputLocation + "/"+config.targetFileName +".R";
			fu.writeStringToFile(strtemp, outputPath);
		}
	}

	private String json_f1(){

		String json = "";
		json = json + "{ \"nodes\":[";
		for(int i=0;i<PRXModel.ksModel.getVertexes().size();i++){

			json = json + "{\"id\":\""+PRXModel.ksModel.getVertexes().get(i).getId()+"\",";
			json = json + "\"name\":\""+PRXModel.ksModel.getVertexes().get(i).getName()+"\",";
			json = json + "\"frequency\":\""+PRXModel.ksModel.getVertexes().get(i).getFrequency()+"\"}";

			if(i<PRXModel.ksModel.getVertexes().size()-1)	
				json = json + ",";
			else	
				json = json + "]";
		}

		json = json + ",\"links\":[";

		for(int i=0;i<PRXModel.ksModel.getEdges().size();i++){
			json = json + "{\"id\":\""+PRXModel.ksModel.getEdges().get(i).getId()+"\",";			
			json = json + "\"source\":\""+PRXModel.ksModel.getEdges().get(i).getSource().getId()+"\",";
			json = json + "\"destination\":\""+PRXModel.ksModel.getEdges().get(i).getDestination().getId()+"\",";
			json = json + "\"weight\":\""+PRXModel.ksModel.getEdges().get(i).getWeight()+"}";

			if(i<PRXModel.ksModel.getEdges().size()-1)	
				json = json + ",";
			else	
				json = json + "] }";
		}			
		System.out.println("\tjson 1 format has been written! ");
		return json;
	}

	private String json_f2(){
		String json = "";
		json = json + "{ \"nodes\":[";
		for(int i=0;i<PRXModel.ksModel.getVertexes().size();i++){

			json = json + "{\"id\":\""+PRXModel.ksModel.getVertexes().get(i).getId()+"\",";
			json = json + "\"name\":\""+PRXModel.ksModel.getVertexes().get(i).getName()+"\",";
			json = json + "\"frequency\":\""+PRXModel.ksModel.getVertexes().get(i).getFrequency()+"\"}";

			if(i<PRXModel.ksModel.getVertexes().size()-1)	
				json = json + ",";
			else	
				json = json + "]";
		}

		json = json + ",\"links\":[";

		for(int i=0;i<PRXModel.ksModel.getEdges().size();i++){
			json = json + "{\"id\":\""+PRXModel.ksModel.getEdges().get(i).getId()+"\",";			
			json = json + "\"source\":\""+PRXModel.ksModel.getEdges().get(i).getSource().getName()+"\",";
			json = json + "\"destination\":\""+PRXModel.ksModel.getEdges().get(i).getDestination().getName()+"\",";
			json = json + "\"weight\":\""+PRXModel.ksModel.getEdges().get(i).getWeight()+"}";

			if(i<PRXModel.ksModel.getEdges().size()-1)	
				json = json + ",";
			else	
				json = json + "] }";
		}			
		System.out.println("\tjson 2 format has been written! ");
		return json;
	}
}