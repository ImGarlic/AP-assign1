package test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.junit.Before;
import org.junit.Test;

import smanalyzer.java.exception.InvalidDateException;
import smanalyzer.java.exception.InvalidPostException;
import smanalyzer.java.model.Post;

public class PostTest {

    Post post;

    @Before
    public void setUp() throws InvalidPostException{
        post = new Post(1, "test content", "test author", 10, 10, "01/01/2000 12:00");
    }
    
    @Test
    public void ConvertDateTime_Success() throws InvalidDateException {
        final String validDateTimeString = "12/12/2000 12:30";
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
        final LocalDateTime validDateTime = LocalDateTime.parse(validDateTimeString, formatter);
        
        assertEquals("Dates should be equal of the correct pattern", validDateTime, Post.convertDateTime(validDateTimeString));
    }

    @Test
    public void ConvertDateTime_Fail_InvalidDate() {
        final String invalidDateTimeString = "99/99/2000 12:30";

        assertThrows("Exception should be thrown for invalid date", 
            InvalidDateException.class, () -> Post.convertDateTime(invalidDateTimeString));
    }

    @Test
    public void FormatPost_Success() throws InvalidPostException {
        final String formattedPost = String.format("--------------------------------------------------------------------------------\n" 
										   + "| %d | %s | %s\n"
										   + "|\n"
									   	   + "| %s\n"
										   + "|\n"
										   + "| Likes: %d | Shares: %d\n"
										   + "--------------------------------------------------------------------------------\n", 
											1, "test author", "01/01/2000 12:00", "test content", 10, 10);
        
        assertEquals("Formatted post should be replicated correctly", formattedPost, post.formatPost());
    }

    @Test
    public void FormatPost_Success_FormattedContent() throws InvalidPostException {
        final Post post = new Post(1, "This is a pointless message about nothing purely designed to waste our time.",
        "test author", 10, 10, "01/01/2000 12:00");
        final String formattedContent = "This is a pointless message about nothing purely designed to waste our \n| time.";
        final String formattedPost = String.format("--------------------------------------------------------------------------------\n" 
										   + "| %d | %s | %s\n"
										   + "|\n"
									   	   + "| %s\n"
										   + "|\n"
										   + "| Likes: %d | Shares: %d\n"
										   + "--------------------------------------------------------------------------------\n", 
											1, "test author", "01/01/2000 12:00", formattedContent, 10, 10);

        assertEquals("Content should be formatted properly", formattedPost, post.formatPost());
    }

    @Test
    public void ConvertFromCSV_Success() throws InvalidPostException {
        final String validPostString = "1,test content,test author,10,10,01/01/2000 12:00";
        final Post convertedPost = Post.convertFromCSV(validPostString);

        assertEquals("ID of posts should be equal", post.getID(), convertedPost.getID());
        assertEquals("Content of posts should be equal", post.getContent(), convertedPost.getContent());
        assertEquals("Author of posts should be equal", post.getAuthor(), convertedPost.getAuthor());
        assertEquals("Likes of posts should be equal", post.getLikes(), convertedPost.getLikes());
        assertEquals("Shares of posts should be equal", post.getShares(), convertedPost.getShares());
        assertEquals("DateTime of posts should be equal", post.getDateTime(), convertedPost.getDateTime());
    }

    @Test
    public void ConvertDateTime_Fail_InvalidFormat() {
        final String invalidFormatPost = "not a post";

        assertThrows("Exception should be thrown for invalid ID",
            InvalidPostException.class, () -> Post.convertFromCSV(invalidFormatPost));
    }

    @Test
    public void ConvertFromCSV_Fail_InvalidID() {
        final String invalidIDPost = "invalidID,test content,test author,10,10,01/01/2000 12:00";

        assertThrows("Exception should be thrown for invalid ID",
            InvalidPostException.class, () -> Post.convertFromCSV(invalidIDPost));
    }

    @Test
    public void ConvertFromCSV_Fail_InvalidLikes() {
        final String invalidIDPost = "1,test content,test author,invalidLikes,10,01/01/2000 12:00";

        assertThrows("Exception should be thrown for invalid likes",
            InvalidPostException.class, () -> Post.convertFromCSV(invalidIDPost));
    }

    @Test
    public void ConvertFromCSV_Fail_InvalidShares() {
        final String invalidIDPost = "1,test content,test author,10,invalidShares,01/01/2000 12:00";

        assertThrows("Exception should be thrown for invalid shares",
            InvalidPostException.class, () -> Post.convertFromCSV(invalidIDPost));
    }

    @Test
    public void ConvertFromCSV_Fail_InvalidDateTime() {
        final String invalidIDPost = "1,test content,test author,10,10,not a date";

        assertThrows("Exception should be thrown for invalid date-time",
            InvalidPostException.class, () -> Post.convertFromCSV(invalidIDPost));
    }
}
