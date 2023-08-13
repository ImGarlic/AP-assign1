package smanalyzer;

import smanalyzer.service.*;

public class Main {
	static String USE_CSV = "csv";
	static String USE_DEFAULT = "default";

	public static void main(String[] args) {
		Menu menu = new Menu();
		Database db = new Database(); 
		
		// Enter any argument into the command-line to use the hard-coded database.
		if(args.length > 0) {
			db.init(USE_DEFAULT);
		} else {
			db.init(USE_CSV);
		}
		menu.start(db);
	}

}

