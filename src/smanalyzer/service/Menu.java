package smanalyzer.service;

import java.security.InvalidParameterException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import smanalyzer.exception.*;
import smanalyzer.model.Post;

public class Menu {
	
	public void start(Database db) {
		Scanner input = new Scanner(System.in);
		
		System.out.printf("Welcome to Social Media Analyzer!\n" + 
						  "--------------------------------------------------------------------------------\n");
		printMenu();
		readMenuSelection(input, db);
		
		input.close();
	}
	
	// Print the user selection menu.
	public void printMenu() {
		String menuScreen = "> Select from main menu\n"
						  + "--------------------------------------------------------------------------------\n"
						  + "	1) Add a social media post\n"
						  + "	2) Delete an existing social media post\n"
						  + "	3) Retrieve a social media post\n"
						  + "	4) Retrieve the top N posts with most likes\n"
						  + "	5) Retrieve the top N posts with most shares\n"
						  + "	6) Exit\n"
						  + "Please select: ";
		
		System.out.printf(menuScreen);
	}

	// Read user input only for the menu options.
	public void readMenuSelection(Scanner input, Database db) {
		int menuSelect;
		boolean active = true;
		while(active) {
			try {
				menuSelect = input.nextInt();
			
				switch(menuSelect) {
				case 1:
					addPost(input, db);
					break;
				case 2:
					deletePost(input, db);
					break;
				case 3:
					retrievePost(input, db);
					break;
				case 4:
					retrieveTopLiked(input, db);
					break;
				case 5:
					retrieveTopShared(input, db);
					break;
				case 6:
					active = false;
					break;
				default:
					throw new InvalidMenuOptionException();
				}

				if(active && menuSelect < 7) {
					System.out.print("Press Enter to continue...");
					input.nextLine();
					System.out.println();
					printMenu();
				}
			
			} catch (InputMismatchException | InvalidMenuOptionException e) {
				System.out.print("Please select 1-6.\nPlease select: ");
				input.nextLine();
			}
		}

	}

	// Read user input to add a post to the database.
	public void addPost(Scanner input, Database db) {
		Post newPost;
		int ID = readID(input);
		String content = readContent(input);
		String author = readAuthor(input);
		int likes = readLikes(input);
		int shares = readShares(input);
		String dateTime = readDateTime(input);

		try {
			newPost = new Post(ID, content, author, likes, shares, dateTime);
			db.put(newPost);
			System.out.println("Post has been added to the collection!");

		} catch (InvalidPostException | PostAlreadyExistsException e) {
			System.out.println("Failed to add post: " + e.getMessage());
		}
		
	}
	
	// Read user input to delete a post from the database.
	public void deletePost(Scanner input, Database db) {
		int ID = readID(input);

		try {
			db.delete(ID);
			System.out.println("Succesfully deleted!");
		} catch(PostNotExistException e) {
			System.out.println("Failed to delete post: " + e.getMessage());
		}

	}
	
	// Read user input to aretrieve a post from the database.
	public void retrievePost(Scanner input, Database db) {
		int ID = readID(input);
		
		try {
			Post post = db.get(ID);
			post.print();
		} catch(PostNotExistException e) {
			System.out.println("Failed to retrieve post: " + e.getMessage());
		}

	}
	
	// Read user input to retrieve the top x most-liked posts from the database.
	public void retrieveTopLiked(Scanner input, Database db) {
		int count = readCount(input);

		List<Post> topLikedPosts = db.getMulti("likes", count);

		if (db.size() <= count) {
			System.out.printf("\nOnly %d posts exist in the collection. Showing all of them:\n", topLikedPosts.size());
		} else {
			System.out.printf("\nThe %d top-liked posts are:\n", count);
		}

		for (Post post : topLikedPosts) {
			post.print();
		}
		System.out.println();
		
	}
	
	// Read user input to retrieve the top x most-shared posts from the database.
	public void retrieveTopShared(Scanner input, Database db) {
		int count = readCount(input);

		List<Post> topSharedPosts = db.getMulti("shares", count);

		if (db.size() <= count) {
			System.out.printf("\nOnly %d posts exist in the collection. Showing all of them:\n", topSharedPosts.size());
		} else {
			System.out.printf("\nThe %d top-shared posts are:\n", count);
		}

		for (Post post : topSharedPosts) {
			post.print();
		}
		System.out.println();
		
	}
	
	// Read user input for a post ID
	public int readID(Scanner input) {
		int ID = -1;
		while(ID < 0) {
			try {
				System.out.print("Please provide the post ID: ");
				ID = input.nextInt();

				if (ID < 0) {
					throw new InvalidParameterException();
				}
				input.nextLine(); // consume newline char

			}	catch (InputMismatchException | InvalidParameterException e) {
				System.out.println("Please enter a non-negative numerical value.");
				input.nextLine();
			}
		}

		return ID;
	}

	// Read user input for a post content
	public String readContent(Scanner input) {
		String content = null;

		while(true) {
			try {
				System.out.print("Please provide the post content: ");
				content = input.nextLine();

				if (content.contains(",")) {
					throw new InvalidParameterException();
				}
				break;
			} catch (InvalidParameterException e) {
				System.out.println("Content should not contain a comma.");
			}
		}
		
		return content;
	}

	// Read user input for a post author
	public String readAuthor(Scanner input) {
		String author = null;

		while(true) {
			try {
				System.out.print("Please provide the post author: ");
				author = input.nextLine();

				if (author.contains(",")) {
					throw new InvalidParameterException();
				}
				break;
			} catch (InvalidParameterException e) {
				System.out.println("Author should not contain a comma.");
			}
		}

		return author;
	}

	// Read user input for a post no. likes
	public int readLikes(Scanner input) {
		int likes = -1;

		while (likes < 0) {
			try {
				System.out.print("Please provide the number of likes of the post: ");
				likes = input.nextInt();

				if (likes < 0) {
					throw new InvalidParameterException();
				}
				input.nextLine(); // consume newline char

			} catch (InputMismatchException | InvalidParameterException e) {
				System.out.println("Please enter a non-negative numerical value.");
				input.nextLine();
			}
		}

		return likes;
	}

	// Read user input for a post no. shares
	public int readShares(Scanner input) {
		int shares = -1;
		
		while (shares < 0) {
			try {
				System.out.print("Please provide the number of shares of the post: ");
				shares = input.nextInt();
				
				if (shares < 0) {
					throw new InvalidParameterException();
				}
				input.nextLine(); // consume newline char

			} catch (InputMismatchException | InvalidParameterException e) {
				System.out.println("Please enter a non-negative numerical value.");
				input.nextLine();
			}
		}

		return shares;
	}

	// Read user input for a post date-time
	public String readDateTime(Scanner input) {
		String dateTime = null;
		
		while (dateTime == null) {
			try {
				System.out.print("Please provide the post date and time in the format DD/MM/YYYY HH:MM: ");
				dateTime = input.nextLine();
				Post.convertDateTime(dateTime);
			} catch (InvalidDateException e) {
				System.out.println(e.getMessage());
				dateTime = null;
			}
		}

		return dateTime;
	}

	// Read user input for number of posts to display for top-liked or top-shared.
	public int readCount(Scanner input) {
		int count = 0;

		while (count < 1) {
			try {
				System.out.print("Please specify the number of posts to display: ");
				count = input.nextInt();

				if (count < 1) {
					throw new InvalidParameterException();
				}
				input.nextLine(); // consume newline char
			}  catch (InputMismatchException | InvalidParameterException e) {
				System.out.println("Please enter a numerical value above 0.");
				input.nextLine();
			}
		}

		return count;
	}
}
