Welcome to the Social Media Analyzer by Dylan Marsh!

This implementation of the Analyzer includes all 6 functions derived from 
https://rmit.instructure.com/courses/107565/files/33117847?wrap=1.

The program will expect the CSV "src/smanalyzer/resources/posts.csv" or else the program will immediately
exit on run (unless you use the default, hard coded data, which can be triggered by typing any 
argument in the run command e.g. `java -cp ./bin smanalyzer.java.Main d`).
You may change the file location by changing the variable PATH_TO_CSV in smanalyzer.java.Main.

The program structure is basically:
                                                     -------------
                                                     |           |
                                                     | DATABASE  |
                                                     |           |
------------              -------------      ->      ¯¯¯¯¯¯¯¯¯¯¯¯¯
|          |              |           |                    |
|   USER   |      ->      |   MENU    |                    |
|          |              |           |                    V
¯¯¯¯¯¯¯¯¯¯¯¯              ¯¯¯¯¯¯¯¯¯¯¯¯¯      ->      -------------
                                                     |           |
                                                     |   POST    |
                                                     |           |
                                                     ¯¯¯¯¯¯¯¯¯¯¯¯¯

The user will interact with the program via the command line, which is implemented through
the smanalyzer.java.service.Menu class. This class deals with all the input/output of the
terminal. Thus there are also no tests for any Menu functions.
The Menu class then interacts with the smanalyzer.java.service.Database class to 
perform functions such as adding, deleting, and retrieving posts from the collection. The 
collection is implemented through a java hashmap to allow for O(1) search, add and remove 
times. 
The smanalyzer.java.model.Post class deals with creating and formatting posts. Both the Menu
and the Database class interact with the Post class. The Post class also contains no setter 
functions to avoid any Posts created with partial parameters.

There are also 5 custom exceptions, in which they are pretty self explanatory. 

RUNNING THE PROGRAM:

1. Set JAVA_HOME environment variable in your system.
Windows 10: https://www.youtube.com/watch?v=104dNWmM6Rs
Mac OS Big Sur: https://www.youtube.com/watch?v=9nKIcK5-uxE

2. Download the sample console program from Canvas. Follow the instructions below to compile
and run the program.

    - Open the terminal and navigate to the folder where you unzip the console program. The
      following is an example of the path to the program on Windows 10.
      C:\>cd Users\xxx\eclipse-workspace\smanalyzer

    - Compile the program using javac. Note *.java represent one or multiple Java files under the
      folder; /smanalyzer/java represents the package smanalyzer.java.
      C:\Users\xxx\eclipse-workspace\smanalyzer>javac -d bin src/smanalyzer/java/*.java src/smanalyzer/java/exception/*.java src/smanalyzer/java/model/*.java src/smanalyzer/java/service/*.java

    - Run the program using java. Note -cp represents -classpath which is used to find classes;
      console.program is the package name and Main is the class in the package.
      C:\Users\xxx\eclipse-workspace\smanalyzer>java -cp ./bin smanalyzer.java.Main

RUNNING THE TESTS:

To run the tests using JUnit, it is a similar compile and run process:

    - Open the terminal and navigate to the folder where you unzip the console program. The
      following is an example of the path to the program on Windows 10.
      C:\>cd Users\xxx\eclipse-workspace\smanalyzer

    - Compile the tests using javac and the junit.jar file found in the lib folder.
      For example, to compile the DatabaseTest class on Windows:
      C:\Users\xxx\eclipse-workspace\smanalyzer>javac -d bin -cp lib/junit-4.13.2.jar;bin src/test/java/DatabaseTest.java

    - Run the tests using javac and the junit.jar and hamcrest.jar files found in the lib folder using JUnitCore.
      For example, to compile the DatabaseTest class on Windows:
      C:\Users\xxx\eclipse-workspace\smanalyzer>java -cp lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar;bin;. org.junit.runner.JUnitCore test.java.DatabaseTest

3. Resources
More information about package and class path can be found in the following link:
https://docs.oracle.com/javase/6/docs/technotes/tools/windows/classpath.html

Compile and run JUnit tests from command line:
https://www.codejava.net/testing/how-to-compile-and-run-junit-tests-in-command-line