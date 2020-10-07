/********************************************************************************************************************
 * FILENAME: LocalGraph.java
 * 
 * ROLE: it runs local graph through cmd.
 * 		
 * VARIABLES: -
 * 
 * METHODS:
 * 	public LocalGraph(config config)				//	constructor. 
 * 
 * COMMENTS:
 * 	The JPathFinder.jar file is in 'lib' folder.
 * 	JPathFinder.jar is originally someone else's job. plz refer to [http://interlinkinc.net/]
 * 	Refer [/documents/jPathfinder.doc] to figure out what the all buttons in UI for.
 * 
 ********************************************************************************************************************/

package option.com.kirc.core.output.localgraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import f1.com.kirc.core.config.Config;

public class LocalGraph{
	
	public LocalGraph(Config config){

		if(config.execute_drawing_flag==true){
			System.out.println("\n\nOPTION) Running JPathFinder.jar...");
			String command = "java -jar "+config.jpathfinderLocation;
			String s;

			try {
				Process oProcess = new ProcessBuilder("cmd", "/c", command).start();

				/* read external program */
				BufferedReader stdOut   = new BufferedReader(new InputStreamReader(oProcess.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(oProcess.getErrorStream()));

				/* print "standard output" and standard error" */
				while ((s =   stdOut.readLine()) != null) System.out.println(s);
				while ((s = stdError.readLine()) != null) System.err.println(s);

				/* print return value of external program */
				System.out.println("\tJPathfinder.jar has been successfully terminated");
				System.exit(oProcess.exitValue()); 						// get the return value of external program and put it back to this program

				}catch(IOException e){ // error handling
					System.err.println("Error! The JPathfinder is not executed\n" + e.getMessage());
					System.exit(-1);
				}	  
		}
	}
}