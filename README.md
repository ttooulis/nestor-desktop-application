# NESTOR Desktop Application (nestor-desktop-application)
A Java Desktop Application that uses the Java Object (NESTOR Library) that Implements the Pipeline for the Translation of Natural Language to Symbolic Form using a Prudens Knowledge Base

NESTOR Desktop Application should be compiled with Java 17.0.2 (jdk-17.0.2). This is an Eclipse Project

The following libraries are required for compiling:

NESTOR Library: NESTORLib.jar
Prudens: Library prudensApp.jar (for backward compatibility)
Json: json-20220924.jar
Google gson: gson-2.10.1.jar
EJML: stanford-corenlp-full-3.9.1/ejml-0.23.jar
Standford CoreNLP Library: stanford-corenlp-full-3.9.1/stanford-corenlp-3.9.1.jar
Standford CoreNLP Models Library: stanford-corenlp-3.9.1-models.jar

# Download Application Binaries
You can download NESTOR Desktop Application using the following URL:
https://tinyurl.com/nestor-desktop-app

# Running and using the application
This description is not ment to be a complete manual for NESTOR application or NESTOR library but as start-up help instructions.

Application requirements
------------------------
To run NESTOR Desktop Application, you will need:
- JDK 17.0.2
- Lots of RAM (at least 8GB)

Before first run check list
---------------------------
- Download the application files to a folder of your choice on your computer.
- Make sure you set JAVA_HOME in NESTOR.bat/NESTOR.sh file to the correct Java home path on your computer.
- If you want to try a different Java Runtime Environment (not recommended) you make sure you set JAVA_HOME path accordingly.
- If you want to turn off application console use javaw executable instead of java in NESTOR.bat/NESTOR.sh
- File NESTOR.xml contains the application parameters. The file that comes with the application does not contain all possible parameters but only the ones that are set to values different than the default values. You can see the full list of supported parameters in console output where they are printed along with their values. Do not change or add a parameter value, unless you know what you are doing.
- The application binary classes along with required libraries are packed in NESTOR.jar.
- Use script file NESTOR.bat for Windows or NESTOR.sh for Linux to run the application.

Translation Policy that comes with NESTOR (Translation-Policy.prudens)
----------------------------------------------------------------------
For the time being, the application comes with a basic default translation policy capable of translating a subset of zero conditional sentences into logic: Conditional sentences in general describe the result (consequence part) of a certain condition (antecedent part). The antecedent part tells you the condition (i.e. 'If you work hard') and the consequence part tells you the result ('you will be paid well'). The order of the parts or the mark word (i.e. if 'when' is used instead of 'if') does not change the meaning. Zero conditional sentences are used to talk about things that are generally true, especially for laws and rules and both parts of the sentence are present simple ('If the sun is shining then the day is hot').

The subset of zero conditional sentences supported by this basic translation policy is defined as follows:

Both the antecedent and consequence parts of the zero conditional sentences should be simple declarative to-be clauses. A simple declarative to-be clause is defined as a clause of the form [<Subject> <to-be-verb> <Predicate>] where <Predicate> could be a <Noun>, an <Adjective> or a <Verbal>, the <Subject> or the <Predicate> cannot be pronouns and negation of the <to-be-verb> is not allowed. For example, 'John is singing', 'Fans are happy', 'Penguin is a bird' are considered to be simple declarative to-be clauses and ‘Fans are happy when John is singing’ is a zero conditional sentence supported by the basic default translation policy.
