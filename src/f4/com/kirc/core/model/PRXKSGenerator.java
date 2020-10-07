/********************************************************************************************************************
 * FILENAME: PRXKSGenerator.java
 * 
 * ROLE: it generates PRX Knowledge Structure
 * 
 * VARIABLES:		
 * 	public KSModel ksModel																// KS model composed of vertexes and edges.
 * 	public String terms																	// vertexes
 * 	public String links																	// edges
 * 
 * METHODS:
 * 	public PRXKSGenerator(config config, NLP nlp, coTableGenerator coTable)				// it sets prx data and write out
 * 	public void writePrxFile(String filePath,int rowCount,double[][] coTable)			// it writes SampleText_data.prx
 * 
 ********************************************************************************************************************/
package f4.com.kirc.core.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import com.google.gson.Gson;

import f1.com.kirc.core.config.Config;
import f3.com.kirc.core.coTable.coTableGenerator;
import f4.com.kirc.core.model.KSModel;

public class PRXKSGenerator {

	public KSModel ksModel = new KSModel();
	public String terms;
	public String links;
	
	public PRXKSGenerator(Config config, coTableGenerator coTable) {

		//System.out.println("STEP 4. Constructing Proximity Model & PathFinder Network...");
		/* knot program result */
		config.prxFile = "./results/result_data/"+config.targetFileName+".prx";
		config.prxpfFile = "./results/result_data/"+config.targetFileName+".pf";
		String prxPath = config.prxFile;
		writePrxFile(prxPath, coTable.rowCount, coTable.coTable);

		File filePath = new File(prxPath);
		PRXModel prxModel = new PRXModel(filePath);
		String prxFileParentPath = filePath.getParent() + File.separator;
		new PFNETCalculator(prxModel, config, prxFileParentPath);

		/* build PathFinder Network */
		ksModel.setDocId(config.targetFileName);
		Gson gson = new Gson();
		ksModel.setVertexes(GraphUtil.addNodes(coTable.sortedValue));
		ksModel.setEdges(GraphUtil.addEdges(ksModel, config.prxpfFile));
		links = gson.toJson(ksModel.getEdges());//+ System.getProperty("line.separator");
		terms = coTable.terms.toString();	

		new File(config.prxFile).delete();
		new File(config.prxpfFile).delete();

		//System.out.println("\tterms have been set!");
		//System.out.println("\tlinks have been set!");
		//System.out.println("\n--------------------------------------------------------");
	}

	public void writePrxFile(String filePath,int rowCount,double[][] coTable){

		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
			out.write("data"); out.newLine();
			out.write("distances"); out.newLine();
			out.write(rowCount +" nodes"); out.newLine();
			out.write("3 decimal places"); out.newLine();
			out.write("1 minimum"); out.newLine();
			out.write("6.999 maximum"); out.newLine();
			out.write("upper triangle:"); out.newLine();

			for(int i=0; i<rowCount; i++) {		
				for(int k=0; k<i; k++)	out.write("\t");

				for(int j=i+1; j<rowCount; j++) out.write(Math.round(coTable[i][j]*100)/100.0 + "\t");

				out.newLine();
			}
			out.close();			

		}catch(Exception e){
			System.out.println("ex = "+e.toString());
		}
	}
}