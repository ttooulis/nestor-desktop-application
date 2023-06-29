package cy.ac.ouc.cognition.nestor.app;

import static cy.ac.ouc.cognition.nestor.lib.utility.Trace.*;

import cy.ac.ouc.cognition.nestor.lib.utility.Parameter;


public class NESTORParameterApp extends Parameter {

	/********************************
	 * NESTOR App Parameters
	 ********************************/
	public static String NESTORThing_LineSeperator = System.getProperty("line.separator");
	public static String NESTORPipeline_ParseDataFile = "ParseData.txt";
	public static String NESTORPipeline_DocumentJSONFile = "Document.json";
	public static String NESTORPipeline_LogicalAnnotationsFile = "LogicalAnnotations.txt";

	
	public static void Load(String settingsFile) {

		Initialize(settingsFile);
		
		outln(TraceLevel.IMPORTANT, "\nLoading NESTOR App parameters...");

		NESTORThing_LineSeperator = ReadParameterTrace("LineSeperator", System.getProperty("line.separator"));
		NESTORPipeline_ParseDataFile = ReadParameterTrace("ParseDataFile", "ParseData.txt");
		NESTORPipeline_DocumentJSONFile = ReadParameterTrace("DocumentJSONFile", "Document.json");
		NESTORPipeline_LogicalAnnotationsFile = ReadParameterTrace("LogicalAnnotationsFile", "LogicalAnnotations.txt");
		
		outln(TraceLevel.IMPORTANT, "NESTOR App Parameters Loaded!");
	}

}
