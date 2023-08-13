package smanalyzer.model;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;
import smanalyzer.exception.*;

public class Post {
	
	private static String COMMA_DELIMITER = ",";

	private int ID;
	private String content;
	private String author;
	private int likes;
	private int shares;
	private LocalDateTime dateTime;

	// Constructor requires all parameters to avoid creation of partially-initialised posts.
	// InvalidPostException should only throw when reading from a CSV; date-time is checked 
	// upon user input when entering manually through the menu.
	public Post(int ID, String content, String author, int likes, int shares, String dateString) throws InvalidPostException {
		this.ID = ID;
		this.content = content;
		this.author = author;
		this.likes = likes;
		this.shares = shares;

		try {
			this.dateTime = convertDateTime(dateString);
		} catch (InvalidDateException e) {
			throw new InvalidPostException(e.getMessage());
		}
	}

	// Converts the date-time string into a LocalDateTime to ensure it is of the correct format.
	public static LocalDateTime convertDateTime(String dateString) throws InvalidDateException {
		LocalDateTime dateTime;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

		try {
			dateTime = LocalDateTime.parse(dateString, formatter);
			return dateTime;
		} catch (DateTimeParseException e) {
			throw new InvalidDateException(e.getMessage());
		}
	}

	// Formats the post to a nice readable format reminiscent of a particular blue bird:
	// -------------------------------------------------------------------------------- 
	// | ID | AUTHOR | DATE-TIME
	// |
	// | POST CONTENT
	// |
	// | Likes: ## | Shares: ##
	// --------------------------------------------------------------------------------
	public String formatPost() {
		String formattedContent = formatContent(this.content);
		String formattedDateTime = this.dateTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));

		String formattedPost = String.format("--------------------------------------------------------------------------------\n" 
										   + "| %d | %s | %s\n"
										   + "|\n"
									   	   + "| %s\n"
										   + "|\n"
										   + "| Likes: %d | Shares: %d\n"
										   + "--------------------------------------------------------------------------------\n", 
											this.ID, this.author, formattedDateTime, formattedContent, this.likes, this.shares);

		return formattedPost;
	}

	// Formats the content to multiple lines if it's over 70 characters. Adds the vertical bar for 
	// consistency with the output of formatPost
	private String formatContent(String content) {
		String formattedContent = "";
		int maxLength = 70;

		while(content.length() > 0) {
			if(content.length() < maxLength) {
				formattedContent += content;
				content = "";
			} else if (content.charAt(maxLength) == ' ') {
				formattedContent += content.substring(0, maxLength + 1) + "\n| ";
				content = content.substring(maxLength + 1);
			} else {
				formattedContent += content.substring(0, content.lastIndexOf(' ', maxLength)) + "\n| ";
				content = content.substring(content.lastIndexOf(' ', maxLength));
			}
		}
		return formattedContent;
	}

	// Print the post to standard output after formatting
	public void print() {
		System.out.print(this.formatPost());
	}

	// Converts a single comma-separated string to a post. Expects all post values to be present
	// and of the correct type, otherwise throws an InvalidPostException.
	public static Post convertFromCSV(String importedPost) throws InvalidPostException {
		List<String> postValues = new ArrayList<String>();
		int ID;
		String content;
		String author;
		int likes;
		int shares;
		String dateTime;

		try (Scanner rowScanner = new Scanner(importedPost)) {
			rowScanner.useDelimiter(COMMA_DELIMITER);

			while(rowScanner.hasNext()) {
				postValues.add(rowScanner.next());
			}
			ID = Integer.valueOf(postValues.get(0));
			content = postValues.get(1);
			author = postValues.get(2);
			likes = Integer.valueOf(postValues.get(3));
			shares = Integer.valueOf(postValues.get(4));
			dateTime = postValues.get(5);
			
			return new Post(ID, content, author, likes, shares, dateTime);
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			throw new InvalidPostException(e.getMessage());
		}

	}

	public int getID() {
		return ID;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public int getLikes() {
		return likes;
	}
	
	public int getShares() {
		return shares;
	}
	
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
}
