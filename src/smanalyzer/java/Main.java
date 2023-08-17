package smanalyzer.java;

import java.io.File;
import java.io.FileNotFoundException;

import smanalyzer.java.service.*;

public class Main {
	static String USE_CSV = "csv";
	static String USE_DEFAULT = "default";
	static String PATH_TO_CSV = "src/smanalyzer/resources/posts.csv";

	public static void main(String[] args) {
		Menu menu = new Menu();
		Database db = new Database(); 
		
		// Enter any argument into the command-line to use the hard-coded database.
		if(args.length > 0) {
			try {
				File csv = new File(PATH_TO_CSV);
				db.init(USE_DEFAULT, csv);
			} catch(FileNotFoundException e) {
				System.out.printf("Missing database file: %s\n", PATH_TO_CSV);
				System.exit(1);
			}
		} else {
			try {
				File csv = new File(PATH_TO_CSV);
				db.init(USE_CSV, csv);
			} catch(FileNotFoundException e) {
				System.out.printf("Missing database file: %s\n", PATH_TO_CSV);
				System.exit(1);
			}
		}
		menu.start(db);
	}

}

