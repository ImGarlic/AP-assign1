package smanalyzer.java.service;

import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

import smanalyzer.java.exception.*;
import smanalyzer.java.model.Post;

public class Database {

	private HashMap<Integer, Post> posts;

	// By default, the database will be initialised to empty. Use init to either read from a CSV
	// or generate the hard-coded database.
	public Database() {
		this.posts = new HashMap<Integer, Post>();
	}

	// Intialise the database to either a CSV or the hard-coded default.
	// CSV must be named "posts.csv"
	public void init(String type, String filename) throws FileNotFoundException{
		if (type == "csv") {
			readCSV(filename);
		} else if (type == "default") {
			generateDB();
		}
	}
	
	// Retrieve 1 post of given ID from the database.
	public Post get(int ID) throws PostNotExistException{
		Post post = this.posts.get(ID);

		if (post != null) {
			return post;
		}
		throw new PostNotExistException(ID);
	}
	
	// Adds 1 post to the database. A post is considered to already exist in the database
	// if it has the same ID, no other parameters are checked.
	public void put(Post post) throws PostAlreadyExistsException {
		int ID = post.getID();

		if(this.posts.get(ID) == null) {
			posts.put(ID, post);
		} else {
			throw new PostAlreadyExistsException(ID);
		}
	}

	// Deletes 1 post of given ID from the database.
	public void delete(int ID) throws PostNotExistException {
		if (this.posts.remove(ID) == null) {
			throw new PostNotExistException(ID);
		};
	}
	
	// getMulti returns list of size 'count' containing posts sorted by 'query'. 
	// 'Query' sorts list in descending order on number of likes or shares, and is null-safe.
	public List<Post> getMulti(String query, int count) {
		List<Post> sortedPosts = new ArrayList<Post>(this.posts.values());
		
		if(query.equals("likes")) {
			Collections.sort(sortedPosts, Database.SORT_ON_LIKES);
		}
		if(query.equals("shares")) {
			Collections.sort(sortedPosts, Database.SORT_ON_SHARES);
		}
		
		try {
			return sortedPosts.subList(0, count);
		} catch (IndexOutOfBoundsException e) {
			return sortedPosts.subList(0, sortedPosts.size());
		}
	}

	// Returns the size of the database.
	public int size() {
		return this.posts.size();
	}
	
	// Reads the file named "posts.csv". Individual posts will be skipped if they are of incorrect format.
	// Program will exit if the file is not found
	private void readCSV(String filename) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(filename));
		scanner.nextLine(); // skip headers
		while (scanner.hasNextLine()) {
			try {
				Post nextPost = Post.convertFromCSV(scanner.nextLine());
				this.put(nextPost);
			} catch (InvalidPostException | PostAlreadyExistsException e) {
				System.out.printf("Failed to import post: %s\n", e.getMessage());
			}
		}
	}
	
	// Generate default database
	private void generateDB() {
		try {
			posts.put(20582, new Post(20582, "Come and meet us at Building 14 of RMIT.", "SD2C45", 10, 24, "12/05/2023 10:10"));
			posts.put(10, new Post(10, "Check out this epic film.", "A567VF", 1000, 1587, "01/06/2023 02:20"));
			posts.put(37221, new Post(37221, "Are we into Christmas month already?!", "3827F2", 526, 25, "15/11/2022 11:30"));
			posts.put(382, new Post(382, "What a miracle!", "38726I", 2775, 13589, "12/02/2023 06:18"));
			posts.put(36778, new Post(36778, "Fantastic day today. Congratulations to all winners.", "1258XE", 230, 1214, "06/06/2023 09:00"));
		} catch (InvalidPostException e) {
			System.out.printf("Failed to generate default database, %s\n", e.getMessage());
			System.exit(1);
		}
	}

	private static Comparator<Post> SORT_ON_LIKES = new Comparator<Post>() {
		public int compare(Post p1, Post p2) {
			return p2.getLikes() - p1.getLikes();
		}
	};
	
	private static Comparator<Post> SORT_ON_SHARES = new Comparator<Post>() {
		public int compare(Post p1, Post p2) {
			return p2.getShares() - p1.getShares();
		}
	};
}
