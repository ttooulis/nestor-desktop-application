package cy.ac.ouc.cognition.nestor.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;

import cy.ac.ouc.cognition.nestor.lib.pipeline.Pipeline;
import cy.ac.ouc.cognition.nestor.lib.utility.ParameterLib;
import static cy.ac.ouc.cognition.nestor.lib.utility.Trace.errln;

public class NESTORMainWindow {


	enum PipeLineState {
		NLP_NOT_LOADED,
		NLP_LOADED,
		NEW_INSTRUCTION,
		NL_PROCESSED,
		LOGICALANNOTATIONS_BUILT,
		EXPRESSION_INFERRED
	}

	
    private static String ls = NESTORParameterApp.NESTORThing_LineSeperator;

	private static PipeLineState currentPipeLineState = PipeLineState.NLP_NOT_LOADED;
	
	/* Controls definition */
	private static Text txtNaturalLanguage;
	private static Text txtInferredExpressions;

	private static Button btnTranslationPolicyEdit;

	private static Button btnViewNLPData;
	private static Button btnViewDocumentJSON;
	private static Button btnProcessNL;
	
	private static Button btnViewLogicalAnnotation;
	private static Button btnBuildLogicalAnnotation;
	private static Button btnTranslate;

	private static Button btnAddExpressionToTarget;

	private static Label lblLblMessageArea;
	private static Label lblTPStatus;

	protected Shell shlNESTOR;


	private static Pipeline NESTORPipe;


	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {

			NESTORParameterApp.Load("NESTORApp.xml");
			NESTORPipe = new Pipeline();

			NESTORMainWindow window = new NESTORMainWindow();

			window.createContents();

	    	AdjustControlState(PipeLineState.NLP_NOT_LOADED);

	    	window.open();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}



	static void AdjustControlState(PipeLineState lPipeLineState) {

		switch (lPipeLineState) {
			case NLP_NOT_LOADED:
    			btnProcessNL.setEnabled(false);
    			btnViewNLPData.setEnabled(false);
    			btnViewDocumentJSON.setEnabled(false);
    			btnBuildLogicalAnnotation.setEnabled(false);
    			btnViewLogicalAnnotation.setEnabled(false);
    			btnTranslate.setEnabled(false);
    			btnAddExpressionToTarget.setEnabled(false);
    			break;

			case NLP_LOADED:
			case NEW_INSTRUCTION:
    			btnProcessNL.setEnabled(true);
    			btnViewNLPData.setEnabled(false);
    			btnViewDocumentJSON.setEnabled(false);
    			btnBuildLogicalAnnotation.setEnabled(true);
    			btnViewLogicalAnnotation.setEnabled(false);
    			btnTranslate.setEnabled(true);
    			btnAddExpressionToTarget.setEnabled(false);
    			break;

			case NL_PROCESSED:
    			btnProcessNL.setEnabled(false);
    			btnViewNLPData.setEnabled(true);
    			btnViewDocumentJSON.setEnabled(true);
    			btnBuildLogicalAnnotation.setEnabled(true);
    			btnViewLogicalAnnotation.setEnabled(false);
    			btnTranslate.setEnabled(true);
    			btnAddExpressionToTarget.setEnabled(false);
    			break;

			case LOGICALANNOTATIONS_BUILT:
    			btnProcessNL.setEnabled(false);
    			btnViewNLPData.setEnabled(true);
    			btnViewDocumentJSON.setEnabled(true);
    			btnBuildLogicalAnnotation.setEnabled(true);
    			btnViewLogicalAnnotation.setEnabled(true);
    			btnTranslate.setEnabled(true);
    			btnAddExpressionToTarget.setEnabled(false);
    			break;

			case EXPRESSION_INFERRED:
    			btnProcessNL.setEnabled(false);
    			btnViewNLPData.setEnabled(true);
    			btnViewDocumentJSON.setEnabled(true);
    			btnBuildLogicalAnnotation.setEnabled(false);
    			btnViewLogicalAnnotation.setEnabled(true);
    			btnTranslate.setEnabled(true);
    			btnAddExpressionToTarget.setEnabled(true);
    			break;

			default:
    			btnProcessNL.setEnabled(false);
    			btnViewNLPData.setEnabled(false);
    			btnViewDocumentJSON.setEnabled(false);
    			btnBuildLogicalAnnotation.setEnabled(false);
    			btnViewLogicalAnnotation.setEnabled(false);
    			btnTranslate.setEnabled(false);
    			btnAddExpressionToTarget.setEnabled(false);
    			break;

		}

		currentPipeLineState = lPipeLineState;
	}


	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		shlNESTOR.open();
		shlNESTOR.layout();
		while (!shlNESTOR.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}


	/**
	 * Create contents of the window.
	 */
	protected void createContents() {

		shlNESTOR = new Shell();
		shlNESTOR.setSize(700, 800);
		shlNESTOR.setText("NESTOR - Knowledge-Based Natural Language to Symbolic Form Translator");
    	shlNESTOR.setLayout(new FillLayout(SWT.VERTICAL));

    	Composite cmpStatus = new Composite(shlNESTOR, SWT.NONE);
    	cmpStatus.setLayout(new FillLayout(SWT.HORIZONTAL));
    	
    	Group grpNLP = new Group(cmpStatus, SWT.NONE);
    	grpNLP.setText("NLP");
    	grpNLP.setLayout(new FillLayout(SWT.VERTICAL));
    	
    	Label lblNLPInfo = new Label(grpNLP, SWT.NONE);
    	lblNLPInfo.setAlignment(SWT.CENTER);
		lblNLPInfo.setText("Stanford Core NLP");
   	
    	Label lblNLPStatus = new Label(grpNLP, SWT.NONE);
    	
    	Button btnLoadNLP = new Button(grpNLP, SWT.NONE);
    	btnLoadNLP.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			try {
    				lblNLPStatus.setText("NLP Loading...");
    				System.gc();
    				NESTORPipe.loadNLProcessor();
    		    	AdjustControlState(PipeLineState.NLP_LOADED);
    				lblNLPStatus.setText("NLP Loaded.");
    			}
    			catch (Exception | OutOfMemoryError nlpe) {
    				System.gc();
    				lblNLPStatus.setText("NLP Failed to Load.");
    				System.out.println("NLP failed to load!");
    				System.out.println(nlpe.getMessage());
    		    	AdjustControlState(PipeLineState.NLP_NOT_LOADED);
    	    	}
    		}
    	});
    	btnLoadNLP.setText("Load NLP Library");
    	
    	Group grpTranslationPolicy = new Group(cmpStatus, SWT.NONE);
    	grpTranslationPolicy.setText("Translation Policy");
    	grpTranslationPolicy.setLayout(new FillLayout(SWT.VERTICAL));
    	
    	Label lblTPInfo = new Label(grpTranslationPolicy, SWT.NONE);
    	lblTPInfo.setAlignment(SWT.CENTER);
    	lblTPInfo.setText(ParameterLib.NESTORPipeline_TranslationPolicyFile);
 	
    	lblTPStatus = new Label(grpTranslationPolicy, SWT.NONE);
    	lblTPStatus.setAlignment(SWT.CENTER);
    	
    	Button btnTranslationPolicyVersion = new Button(grpTranslationPolicy, SWT.NONE);
    	btnTranslationPolicyVersion.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {

    			String TranslationPolicyString = "";
    	        try {
    	        	TranslationPolicyString = Files.readString(Paths.get(ParameterLib.NESTORPipeline_TranslationPolicyFile));
        	    	lblTPStatus.setText(NESTORPipe.getTranslationPolicyVersion(TranslationPolicyString));
    	        	
    			} 
    	        
    	        catch (FileNotFoundException nfe) {
    				errln("Translation Policy file not found: " + nfe.getMessage());
        	    	lblTPStatus.setText("File not found");
    			}
    	        
    	        catch (IOException ioe) {
    				errln("Error reading Translation Policy: " + ioe.getMessage());
        	    	lblTPStatus.setText("File error");
    			}

    		}
    	});
    	btnTranslationPolicyVersion.setText("Translation Policy Version");
    	
    	btnTranslationPolicyEdit = new Button(grpTranslationPolicy, SWT.NONE);
    	btnTranslationPolicyEdit.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {

    	        try {
    	        	Program.launch(ParameterLib.NESTORPipeline_TranslationPolicyFile);

    	        } catch (Exception ple) {
		        	MessageBox mt = new MessageBox(shlNESTOR);
		        	mt.setMessage(ple.toString());
		        	mt.open();          
    	        }
    		}
    	});
    	btnTranslationPolicyEdit.setText("Edit Translation Policy");
    	
    	Group grpTargetPolicy = new Group(cmpStatus, SWT.NONE);
    	grpTargetPolicy.setText("Target Policy");
    	grpTargetPolicy.setLayout(new FillLayout(SWT.VERTICAL));
    	
    	Label lblTargetPolicyInfo = new Label(grpTargetPolicy, SWT.NONE);
    	lblTargetPolicyInfo.setAlignment(SWT.CENTER);
    	lblTargetPolicyInfo.setText(ParameterLib.NESTORPipeline_TargetPolicyFile);
    	
    	Label lblTargetPolicyStatus = new Label(grpTargetPolicy, SWT.NONE);
    	lblTargetPolicyStatus.setText("");
    	
    	Button btnTargetPolicyEdit = new Button(grpTargetPolicy, SWT.NONE);
    	btnTargetPolicyEdit.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    	        try {
    	        	Program.launch(ParameterLib.NESTORPipeline_TargetPolicyFile);

    	        } catch (Exception ple) {
		        	MessageBox mt = new MessageBox(shlNESTOR);
		        	mt.setMessage(ple.toString());
		        	mt.open();          
    	        }
    		}
    	});
    	btnTargetPolicyEdit.setText("Edit Target Policy");
    	
    	Group grpNaturalLanguage = new Group(shlNESTOR, SWT.NONE);
    	grpNaturalLanguage.setText("Natural Language");
    	grpNaturalLanguage.setLayout(new FillLayout(SWT.HORIZONTAL));
    	
    	txtNaturalLanguage = new Text(grpNaturalLanguage, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
    	txtNaturalLanguage.addModifyListener(new ModifyListener() {
    		public void modifyText(ModifyEvent arg0) {
	    		if (currentPipeLineState.ordinal() >= PipeLineState.NLP_LOADED.ordinal())
    		    	AdjustControlState(PipeLineState.NEW_INSTRUCTION);
	    		else
	    			AdjustControlState(currentPipeLineState);
    		}
    	});
    	
    	Composite cmpInstructionTextControls = new Composite(shlNESTOR, SWT.NONE);
    	cmpInstructionTextControls.setLayout(new FillLayout(SWT.HORIZONTAL));
    	
    	Group grpNLPControls = new Group(cmpInstructionTextControls, SWT.NONE);
    	grpNLPControls.setText("Natural Language Processing");
    	grpNLPControls.setLayout(new FillLayout(SWT.VERTICAL));
    	
    	btnViewNLPData = new Button(grpNLPControls, SWT.NONE);
    	btnViewNLPData.setEnabled(false);
    	btnViewNLPData.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    	        try
    	        {
    	        	if (currentPipeLineState.ordinal() >= PipeLineState.NL_PROCESSED.ordinal()) {
    	        		Files.write(Paths.get( NESTORParameterApp.NESTORPipeline_ParseDataFile), NESTORPipe.getParseData().getBytes());
    	        		Program.launch( NESTORParameterApp.NESTORPipeline_ParseDataFile);
    	        	}
    	        	else {
    		        	MessageBox m = new MessageBox(shlNESTOR);
    		        	m.setMessage("Wrong pipeline state (" + currentPipeLineState + "): Cannot show parse data! (MSG005-1)");
    		        	m.open();            	
    	        	}
    	        }
    	        catch(Exception e2) {
		        	MessageBox m = new MessageBox(shlNESTOR);
		        	m.setMessage("Error: Cannot show parse data! (MSG005-2)" + ls + e2.getMessage());
		        	m.open();            	
     	        }
    		}
    	});
    	btnViewNLPData.setText("View NL Processing Data");
    	
    	btnViewDocumentJSON = new Button(grpNLPControls, SWT.NONE);
    	btnViewDocumentJSON.setEnabled(false);
    	btnViewDocumentJSON.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    	        try
    	        {
    	        	if (currentPipeLineState.ordinal() >= PipeLineState.NL_PROCESSED.ordinal()) {
    	        		Files.write(Paths.get( NESTORParameterApp.NESTORPipeline_DocumentJSONFile), NESTORPipe.getDocumentJSON().getBytes());
    	        		Program.launch( NESTORParameterApp.NESTORPipeline_DocumentJSONFile);
    	        	}
    	        	else {
    		        	MessageBox m = new MessageBox(shlNESTOR);
    		        	m.setMessage("Wrong pipeline state (" + currentPipeLineState + "): Cannot show document JSON! (MSG005-3)");
    		        	m.open();            	
    	        	}
    	        }
    	        catch(Exception e2) {
		        	MessageBox m = new MessageBox(shlNESTOR);
		        	m.setMessage("Error: Cannot show parse data! (MSG005-2)" + ls + e2.getMessage());
		        	m.open();            	
     	        }
    		}
    	});
    	btnViewDocumentJSON.setText("View Document JSON");
    	
    	btnProcessNL = new Button(grpNLPControls, SWT.NONE);
    	btnProcessNL.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			PipeLineState prevPipeLineState = currentPipeLineState;
    	        try
    	        {
    				txtInferredExpressions.setText("");
    	        	String instructionText = txtNaturalLanguage.getText();
    	        	if (	currentPipeLineState.ordinal() >= PipeLineState.NEW_INSTRUCTION.ordinal() &&
    	        			instructionText != null && instructionText != "") {
    	        		NESTORPipe.processNL(instructionText);
    	        		AdjustControlState(PipeLineState.NL_PROCESSED);
    	        	}
    	        	else if (currentPipeLineState.ordinal() < PipeLineState.NEW_INSTRUCTION.ordinal()) {
		        		AdjustControlState(prevPipeLineState);
    		        	MessageBox m = new MessageBox(shlNESTOR);
    		        	m.setMessage("Wrong pipeline state (" + currentPipeLineState + "): Cannot parse! (MSG006-1)");
    		        	m.open();            	   	        		
    	        	}
    	        	else {
		        		AdjustControlState(prevPipeLineState);
    		        	MessageBox m = new MessageBox(shlNESTOR);
    		        	m.setMessage("No instructions provided: Cannot parse! (MSG006-2)");
    		        	m.open();            	
    	        	}
    	        }
    	        catch(Exception e2) {
	        		AdjustControlState(prevPipeLineState);
		        	MessageBox m = new MessageBox(shlNESTOR);
		        	m.setMessage("Error: Cannot parse natural language! (MSG006-3)" + ls + e2.getMessage());
		        	m.open();            	
     	        }
    		}
    	});
    	btnProcessNL.setEnabled(false);
    	btnProcessNL.setText("Process Natural Language");
    	
    	Group grpLogicalProcessing = new Group(cmpInstructionTextControls, SWT.NONE);
    	grpLogicalProcessing.setText("Logical Processing");
    	grpLogicalProcessing.setLayout(new FillLayout(SWT.VERTICAL));
    	
    	btnViewLogicalAnnotation = new Button(grpLogicalProcessing, SWT.NONE);
    	btnViewLogicalAnnotation.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
        	    try {
        	        if (currentPipeLineState.ordinal() >= PipeLineState.LOGICALANNOTATIONS_BUILT.ordinal()) {
        	        	Files.write(Paths.get(NESTORParameterApp.NESTORPipeline_LogicalAnnotationsFile), NESTORPipe.getLogicalAnnotationText().getBytes());
        	        	Program.launch(NESTORParameterApp.NESTORPipeline_LogicalAnnotationsFile);
        	        }
        	        else {
        		        MessageBox m = new MessageBox(shlNESTOR);
        		        m.setMessage("Wrong pipeline state (" + currentPipeLineState + "): Cannot show logical annotations! (MSG009-1)");
        		        m.open();            	
        	        }
        	    }
        	    catch(Exception e2) {
    		        MessageBox m = new MessageBox(shlNESTOR);
    		        m.setMessage("Error: Cannot show logical annotations! (MSG009-2)" + ls + e2.getMessage());
    		        m.open();            	
         	    }
    		}
    	});
    	btnViewLogicalAnnotation.setText("View Logical Annotation");
    	btnViewLogicalAnnotation.setEnabled(false);
    	
    	btnBuildLogicalAnnotation = new Button(grpLogicalProcessing, SWT.NONE);
    	btnBuildLogicalAnnotation.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			PipeLineState prevPipeLineState = currentPipeLineState;

    			try {
    				txtInferredExpressions.setText("");
    	        	String instructionText = txtNaturalLanguage.getText();

    	        	if (	currentPipeLineState.ordinal() >= PipeLineState.NEW_INSTRUCTION.ordinal() &&
    	        			instructionText != null && instructionText != "") {

       	        		if (currentPipeLineState.ordinal() < PipeLineState.NL_PROCESSED.ordinal())
       	        			NESTORPipe.processNL(instructionText);

    	        		NESTORPipe.buildLogicalAnnotation();;
    	        		AdjustControlState(PipeLineState.LOGICALANNOTATIONS_BUILT);
    	        	}

     	        	else if (instructionText == null || instructionText == "") {
		        		AdjustControlState(prevPipeLineState);
    		        	MessageBox m = new MessageBox(shlNESTOR);
    		        	m.setMessage("No instructions provided: Cannot parse! (MSG010-1)");
    		        	m.open();            	
    	        	}
    	        	
    	        	else {
		        		AdjustControlState(prevPipeLineState);
    		        	MessageBox m = new MessageBox(shlNESTOR);
    		        	m.setMessage("Wrong pipeline state (" + currentPipeLineState + "): Cannot build logical annotations! (MSG010-2)");
    		        	m.open();            	   	        		
    	        	}
    	        }
    	        catch(Exception e2) {
	        		AdjustControlState(prevPipeLineState);
		        	MessageBox m = new MessageBox(shlNESTOR);
		        	m.setMessage("Error: Cannot parse natural language or build logical annotations! (MSG010-3)" + ls + e2.getMessage());
		        	m.open();            	
     	        }
    		}
    	});
    	btnBuildLogicalAnnotation.setText("Build Logical Annotation");
    	btnBuildLogicalAnnotation.setEnabled(false);
    	
    	btnTranslate = new Button(grpLogicalProcessing, SWT.NONE);
    	btnTranslate.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {

    			PipeLineState prevPipeLineState = currentPipeLineState;

    	        try {
 
    	        	txtInferredExpressions.setText("");
    	        	String instructionText = txtNaturalLanguage.getText();
    	        	if (	currentPipeLineState.ordinal() >= PipeLineState.NEW_INSTRUCTION.ordinal() &&
    	        			instructionText != null && instructionText != "") {

    	        		if (currentPipeLineState.ordinal() < PipeLineState.NL_PROCESSED.ordinal())
       	        			NESTORPipe.processNL(instructionText);
    	        		
    	        		if (currentPipeLineState.ordinal() < PipeLineState.LOGICALANNOTATIONS_BUILT.ordinal())
    	        			NESTORPipe.buildLogicalAnnotation();

    	        		
    	    			String TranslationPolicyString = "";
    	    	        try {
    	    	        	TranslationPolicyString = Files.readString(Paths.get(ParameterLib.NESTORPipeline_TranslationPolicyFile));
    	        	    	lblTPStatus.setText(NESTORPipe.getTranslationPolicyVersion(TranslationPolicyString));  	    	        	
    	    			}
    	    	        
    	    	        catch (FileNotFoundException nfe) {
    	    				errln("Translation Policy file not found: " + nfe.getMessage());
    	        	    	lblTPStatus.setText("File not found");
    	        	    	throw(nfe);
    	    			}
    	    	        
    	    	        catch (IOException ioe) {
    	    				errln("Error reading Translation Policy: " + ioe.getMessage());
    	        	    	lblTPStatus.setText("File error");
    	        	    	throw(ioe);
    	    			}
    	        	    
    	    	        NESTORPipe.inferTranslation(TranslationPolicyString);       		
    	        		txtInferredExpressions.setText(NESTORPipe.getTranslationText());
    	        		AdjustControlState(PipeLineState.EXPRESSION_INFERRED);

    	        	}

    	        	else if (instructionText == null || instructionText == "") {
		        		AdjustControlState(prevPipeLineState);
    		        	MessageBox m = new MessageBox(shlNESTOR);
    		        	m.setMessage("No instruction provided: Cannot parse! (MSG011-4)");
    		        	m.open();            	
    	        	}
    	        	
    	        	else {
		        		AdjustControlState(prevPipeLineState);
    		        	MessageBox m = new MessageBox(shlNESTOR);
    		        	m.setMessage("Wrong pipeline state (" + currentPipeLineState + "): Cannot build logical annotations! (MSG011-5)");
    		        	m.open();            	   	        		
    	        	}
    	        }
 
    	        catch(Exception e2) {
	        		AdjustControlState(prevPipeLineState);
		        	MessageBox m = new MessageBox(shlNESTOR);
		        	m.setMessage("Error: Cannot parse natural language or build logical annotations or infer expressions! (MSG011-6)" + ls + e2.toString());
		        	m.open();            	
     	        }

    		}
    	});
    	btnTranslate.setText("Translate to Logic Representation");
    	btnTranslate.setEnabled(false);
    	
    	Group grpInferredExpressions = new Group(shlNESTOR, SWT.NONE);
    	grpInferredExpressions.setText("Inferred Expressions");
    	grpInferredExpressions.setLayout(new FillLayout(SWT.HORIZONTAL));
    	
    			    txtInferredExpressions = new Text(grpInferredExpressions, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
    	
    	Composite cmpInferTranslationControls = new Composite(shlNESTOR, SWT.NONE);
    	cmpInferTranslationControls.setLayout(new FillLayout(SWT.VERTICAL));
    	
    	Composite cmpGRC1 = new Composite(cmpInferTranslationControls, SWT.NONE);
    	cmpGRC1.setLayout(new FillLayout(SWT.VERTICAL));
    	
    	@SuppressWarnings("unused")
		Label lblGRC1Filler1 = new Label(cmpGRC1, SWT.NONE);
    	
    	Composite cmpGRC2 = new Composite(cmpInferTranslationControls, SWT.NONE);
    	cmpGRC2.setLayout(new FillLayout(SWT.HORIZONTAL));
    	
    	@SuppressWarnings("unused")
		Label lblGRC2Filler1 = new Label(cmpGRC2, SWT.NONE);
    	
    	@SuppressWarnings("unused")
		Label lblGRC2Filler2 = new Label(cmpGRC2, SWT.NONE);
    	
    	btnAddExpressionToTarget = new Button(cmpGRC2, SWT.CENTER);
    	btnAddExpressionToTarget.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
     	        try {
        	        if (currentPipeLineState.ordinal() >= PipeLineState.EXPRESSION_INFERRED.ordinal()) {
        	        	Files.write(Paths.get(ParameterLib.NESTORPipeline_TargetPolicyFile), txtInferredExpressions.getText().getBytes(), StandardOpenOption.APPEND);
			        	MessageBox m = new MessageBox(shlNESTOR);
			        	m.setMessage("Expressions added to Target Policy!");
			        	m.open();
        	        }
        	        else {
			        	MessageBox m = new MessageBox(shlNESTOR);
			        	m.setMessage("Wrong pipeline state (" + currentPipeLineState + "): Cannot build logical annotations! (MSG012-1)");
			        	m.open();
        	        }
				} catch (IOException e1) {
    		        MessageBox m = new MessageBox(shlNESTOR);
    		        m.setMessage("Error: Cannot write expressions to Target Policy! (MSG012-2)" + ls + e1.getMessage());
    		        m.open();            	
				}
    		}
    	});
    	btnAddExpressionToTarget.setText("Add Expressions to Target Policy");
    	
    	lblLblMessageArea = new Label(cmpInferTranslationControls, SWT.NONE);
    	lblLblMessageArea.setText("");

	}

}
