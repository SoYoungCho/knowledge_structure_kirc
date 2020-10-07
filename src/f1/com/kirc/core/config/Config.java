/********************************************************************************************************************
 * FILENAME: config.java
 * 
 * ROLE: config.java sets all the pre-definded variables. the values are loaded from config.cfg file
 * 		
 * VARIABLES:
 * 	public String configFile					// config.properties file location
 * 	public String title							// filename without 'filename extension' like '.txt'
 * 	public String resourceLocation				// resource folder location
 * 
 * 	public File[] targetFiles					// resource files
 * 	public File targetFile						// a resource file. This is one element of the array targetFiles[]
 * 	public int num_of_files						// number of resource files
 *
 *	public String db_host						// database information which contains the results of this program
 *	public String db_id							// database ID
 *	public String db_pw							// database Password
 *	public String db_schema_name				// database schema name
 *	public String db_table_name					// database table name
 *
 *	public int top_N_nodes						// only top-N nodes will shown in the result
 *	public String cot_mode						// cotable mode. ss, ps, scs, pcs
 *
 *	public String outputLocation				// output file location
 *	public String prxdataLocation				// prx (proximity) data location
 *	public String prxpfdataLocation				// prx pf (prx pathfinder) data location
 *	public String engTaggerLocation				// English tagger location
 *	public String korTaggerLocation 			// Korean tagger location
 *
 *	public String prxFile						// a file pointer of a prxFile
 *	public String prxpfFile						// a file pointer of a prxpfFile
 * 
 * 	public boolean output_db_flag				// flag to put the result into database
 * 	public boolean output_json_f1_flag			// a flag to write the result into json_format 1
 * 	public boolean output_json_f2_flag			// a flag to write the result into json_format 2
 * 
 * 	public boolean output_txt_vertices_flag		// a flag to determine the output vertexes.txt come out or not
 * 	public boolean output_txt_edges_flag		// a flag to determine the output edges.txt come out or not
 * 
 * 	public boolean execute_drawing_flag			// a flag to execute jpathfinder drawing tool
 * 	public String jpathfinderLocation			// a location of jpathfinder.jar
 * 
 * 	public boolean run_webgraph_flag			// a flag to execute webgraph drawing tool
 * 	public String webgraphLocation				// web graph location
 * 	public boolean web_directed_flag			// a flag whether the web graph is directed or not
 * 
 * METHODS:
 * 	public config()								// constructor. the main function of config.config
 * 	public String getRootPath()					// to get its root path when reading a file
 * 	public File[] read_folder(String resource)	// read a directory and return all files in it
 * 
 ********************************************************************************************************************/
package f1.com.kirc.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

public class Config{

	public String configFile;				
	public String title;					
	public String resourceLocation;		

	ArrayList<File> files;

	public ArrayList<File> resourceFiles;			
	private File targetFile;		
	public String targetFileName;
	public int num_of_files;				
	public int length_of_resource;

	public String db_host;					
	public String db_id;
	public String db_pw;

	public String prxpfFile;
	public String prxFile;
	
	public int max_N_nodes;
	public int top_N_nodes;				
	public String cot_mode;					

	public String outputLocation;			
	public String graphOutputLocation;
	public String prxdataLocation;				
	public String prxpfdataLocation;			
	public String engTaggerLocation;
	public String korTaggerLocation;
	public String libLocation;
	public String LuceneIndexLocation;
	public String sentenceDecetorBinLocation;

	public int num_of_trec_files;	

	public boolean output_db_flag;
	public boolean output_json_f1_flag;
	public boolean output_json_f2_flag;

	public boolean output_txt_vertices_flag;
	public boolean output_txt_edges_flag;
	public boolean output_R_flag;
	public static String encodetype;
	public boolean execute_drawing_flag;
	public String jpathfinderLocation;	

	public boolean run_webgraph_flag;
	public String webgraphLocation;
	public boolean web_directed_flag;

	public Config() {

		System.out.println("STEP 1. Reading from config.cfg...");
		String root_path = "";
		try{	
			Properties props = new Properties();	
			FileInputStream fis = null;
			try{
				String jar_path = Config.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				System.out.println("jar_path1 : " + jar_path);
//				jar_path = jar_path.substring(0,jar_path.indexOf("/knowledge_structure"));
				jar_path = jar_path.substring(0,jar_path.indexOf("/target"));
				jar_path = jar_path.substring(1);
				configFile = jar_path+"/config.properties";	  
				libLocation = jar_path+"/lib";
				root_path= jar_path;
				// .property file location						// create .property instance
				fis = new FileInputStream(configFile);
				System.out.println("hihi2");
			}
			catch(Exception e){
				configFile = "./config.properties";	
				libLocation = getRootPath()+"/lib";
				root_path= getRootPath();							// create .property instance
				System.out.println("root_path11 : " + root_path);
				fis = new FileInputStream(configFile);
			}

			// put .property into filestream
			props.load(new java.io.BufferedInputStream(fis));	    		// loading from .property

			/* load config variables */
			this.db_host = props.getProperty("DB_HOST");
			this.db_id = props.getProperty("DB_ID");
			this.db_pw = props.getProperty("DB_PW");
			this.max_N_nodes = Integer.parseInt(props.getProperty("TOP_N_NODES"));
			this.top_N_nodes = max_N_nodes;
			this.resourceLocation = root_path+props.getProperty("RESOURCE_LOCATION");
			this.cot_mode = props.getProperty("COT_MODE");
			this.outputLocation = root_path+props.getProperty("OUTPUT_LOCATION");
			this.graphOutputLocation =root_path+props.getProperty("GRAPH_OUTPUT_LOCATION");
			this.prxdataLocation =root_path+props.getProperty("PRX_DATA_LOCATION");
			this.prxpfdataLocation = root_path+props.getProperty("PRX_PF_DATA_LOCATION");
			this.engTaggerLocation =root_path+props.getProperty("ENG_TAGGER_LOCATION");
			this.korTaggerLocation = root_path+props.getProperty("KOR_TAGGER_LOCATION");
			this.LuceneIndexLocation = root_path+props.getProperty("LUCENE_INDEX_LOCATION");
			this.sentenceDecetorBinLocation =root_path + "/lib/en-sent.bin";


			this.jpathfinderLocation = root_path+props.getProperty("JPATHFINDER_LOCATION");
			this.webgraphLocation = root_path+props.getProperty("WEBGRAPH_LOCATION");
			//this.libLocation = getRootPath()+props.getProperty("LIB_LOCATION");

			if(props.getProperty("OUTPUT_DB").matches("on")) this.output_db_flag = true;
			else this.output_db_flag = false;
			if(props.getProperty("OUTPUT_JSON_F1").matches("on")) this.output_json_f1_flag = true;
			else this.output_json_f1_flag = false;
			if(props.getProperty("OUTPUT_JSON_F2").matches("on")) this.output_json_f2_flag = true;
			else this.output_json_f2_flag = false;
			if(props.getProperty("OUTPUT_VERTICES").matches("on")) this.output_txt_vertices_flag = true;
			else this.output_txt_vertices_flag = false;
			if(props.getProperty("OUTPUT_EDGES").matches("on")) this.output_txt_edges_flag = true;
			else this.output_txt_edges_flag = false;
			if(props.getProperty("OUTPUT_R").matches("on")) this.output_R_flag = true;
			else this.output_R_flag = false;
			if(props.getProperty("EXE_DRAWING").matches("on")) this.execute_drawing_flag = true;
			else this.execute_drawing_flag = false;
			if(props.getProperty("RUN_WEBGRAPH").matches("on")) this.run_webgraph_flag = true;
			else this.run_webgraph_flag = false;
			if(props.getProperty("DIRECTED").matches("on")) this.web_directed_flag = true;
			else this.web_directed_flag = false;

			/* print the contents in the config file */
			System.out.println("\tDB_HOST: " + this.db_host);
			System.out.println("\tDB_ID: " + this.db_id);
			System.out.println("\tDB_PW: " + this.db_pw);

			System.out.println();	    	
			System.out.println("\tTOP_N_NODES: " + this.top_N_nodes);
			System.out.println("\tCOTABLE_MODE: " + this.cot_mode);
			System.out.println("\tENG_TAGGER_LOCATION: " + this.engTaggerLocation);
			System.out.println("\tKOR_TAGGER_LOCATION: " + this.korTaggerLocation);

			System.out.println();
			System.out.println("\tRESOURCE_LOCATION: " + this.resourceLocation); 
			System.out.println("\tOUTPUT_LOCATION: " + this.outputLocation);
			System.out.println("\tLIB_LOCATION: " + this.libLocation);

			System.out.println();
			System.out.println("\tPRX_DATA_LOCATION: " + this.prxdataLocation);
			System.out.println("\tPRX_PF_DATA_LOCATION: " + this.prxpfdataLocation);
			System.out.println("\tJPATHFINDER_LOCATION: " + this.jpathfinderLocation);
			System.out.println("\tWEBGRAPH_LOCATION: " + this.webgraphLocation);

			System.out.println();
			System.out.println("\tOUTPUT_DB: " + props.getProperty("OUTPUT_DB"));
			System.out.println("\tOUTPUT_JSON_F1: " + props.getProperty("OUTPUT_JSON_F1"));
			System.out.println("\tOUTPUT_JSON_F2: " + props.getProperty("OUTPUT_JSON_F2"));
			System.out.println("\tOUTPUT_VERTICES: " + props.getProperty("OUTPUT_VERTICES"));
			System.out.println("\tOUTPUT_EDGES: " + props.getProperty("OUTPUT_EDGES"));

			System.out.println("\tEXE_DRAWING: " + props.getProperty("EXE_DRAWING"));
			System.out.println("\tRUN_WEBGRAPH: " + props.getProperty("RUN_WEBGRAPH"));
			System.out.println("\tDIRECTED: " + props.getProperty("DIRECTED"));

			/**set resourceFiles **/
			//this.files = new ArrayList<File>();
			//this.resourceFiles = read_folder(resourceLocation, files);
			//this.num_of_files = resourceFiles.size();

			/*
			this.files = new ArrayList<File>();
			this.trecXmlFiles = read_folder(trecDataLocation, files);
			this.num_of_trec_files = trecXmlFiles.size();
			 */		 

		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("\n--------------------------------------------------------");
	}

	public String getRootPath() {

		File file = new File(".");
		String result = "";

		try{
			result = file.getCanonicalPath().toString();
		}catch(Exception e){
			System.out.println("exception getRootPath = " + e.toString());
		}
		return result;
	}

	public ArrayList<File> read_folder(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				read_folder(file.getAbsolutePath(), files);
			}
		}
		return files;
	}

	public File getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(File targetFile) {
		this.targetFile = targetFile;
		this.targetFileName = this.targetFile.getName().split("\\.")[0];
	}
}