package test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import smanalyzer.java.exception.InvalidPostException;
import smanalyzer.java.exception.PostAlreadyExistsException;
import smanalyzer.java.exception.PostNotExistException;
import smanalyzer.java.model.Post;
import smanalyzer.java.service.Database;

public class DatabaseTest {

    Database db;
    Post post;
    static int DATABASE_SIZE = 1;
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUpDatabase() throws PostAlreadyExistsException, InvalidPostException {
        db = new Database();
        post = new Post(1,"test content","test author", 10, 10, "01/01/2000 12:00");

        db.put(post);
    }

    @Test
    public void Get_Success() throws PostNotExistException {
       assertEquals("Database should return the same post", db.get(post.getID()), post);
    }

    @Test
    public void Get_Fail_PostNotExist() throws PostNotExistException {
        final int ID = 9999;

        assertThrows("Get should fail as post not exist", PostNotExistException.class, () -> db.get(ID));
    }

    @Test
    public void Put_Success() throws PostAlreadyExistsException, InvalidPostException, PostNotExistException {
        Post post = new Post(2,"test content 2","test author 2", 20, 20, "02/02/2000 12:00");

        db.put(post);
        assertEquals("Databasesize should increase by 1", db.size(), 2);
        assertEquals("Database should now contain the new post", db.get(post.getID()), post);
    }

    @Test
    public void Put_Fail_PostAlreadyExists() throws PostAlreadyExistsException, InvalidPostException, PostNotExistException {

        assertThrows("Put should fail as post already exists", PostAlreadyExistsException.class, () -> db.put(post));
        assertEquals("Database size should stay the same", db.size(), 1);
    }

    @Test
    public void Delete_Success() throws PostNotExistException {
        db.delete(post.getID());

        assertThrows("Post should no longer exist", PostNotExistException.class, () -> db.get(post.getID()));
        assertEquals("Database size should decrease by one", db.size(), 0);
    }

    @Test
    public void Delete_Fail_PostNotExist() throws PostNotExistException {
        final int ID = 9999;

        assertThrows("Delete should fail as post does not exist", PostNotExistException.class, () -> db.delete(ID));
        assertEquals("Database size should stay the same", db.size(), 1);
    }

    @Test
    public void GetMulti_Success_SortOnLikes() throws PostNotExistException, PostAlreadyExistsException, InvalidPostException {
        final int count = 3;
        db = databaseForSorting();
        List<Post> actualList = db.getMulti("likes", count);
        List<Post> expectedList = new ArrayList<Post>(Arrays.asList(db.get(1), db.get(2), db.get(3), db.get(4)));

        expectedList.sort(SORT_ON_LIKES);
        expectedList = expectedList.subList(0, count);
        assertEquals("List should be sorted on likes", expectedList, actualList);
    }

    @Test
    public void GetMulti_Success_SortOnShares() throws PostNotExistException, PostAlreadyExistsException, InvalidPostException {
        final int count = 3;
        db = databaseForSorting();
        List<Post> actualList = db.getMulti("shares", count);
        List<Post> expectedList = new ArrayList<Post>(Arrays.asList(db.get(1), db.get(2), db.get(3), db.get(4)));

        expectedList.sort(SORT_ON_SHARES);
        expectedList = expectedList.subList(0, count);
        assertEquals("List should be sorted on shares", expectedList, actualList);
    }

    //GetMulti should just give us the full list when the given count is over the database size
    @Test
    public void GetMulti_Success_SortOnLikes_OverMax() throws PostNotExistException, PostAlreadyExistsException, InvalidPostException {
        final int count = 5;
        db = databaseForSorting();
        List<Post> actualList = db.getMulti("likes", count);
        List<Post> expectedList = new ArrayList<Post>(Arrays.asList(db.get(1), db.get(2), db.get(3), db.get(4)));

        expectedList.sort(SORT_ON_LIKES);
        assertEquals("List should be sorted on likes", expectedList, actualList);
    }

    //GetMulti should just give us the full list when the given count is over the database size
    @Test
    public void GetMulti_Success_SortOnShares_OverMax() throws PostNotExistException, PostAlreadyExistsException, InvalidPostException {
        final int count = 5;
        db = databaseForSorting();
        List<Post> actualList = db.getMulti("shares", count);
        List<Post> expectedList = new ArrayList<Post>(Arrays.asList(db.get(1), db.get(2), db.get(3), db.get(4)));

        expectedList.sort(SORT_ON_SHARES);
        assertEquals("List should be sorted on shares", expectedList, actualList);
    }

    @Test
    public void Size_Success() {
        assertEquals("Should return the correct size", DATABASE_SIZE, db.size());
    }

    @Test
    public void ReadCSV_Success() throws PostNotExistException, IOException {
        final String formattedDateTime = post.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
        final String postString = String.format("%d,%s,%s,%d,%d,%s", 
            post.getID(), post.getContent(), post.getAuthor(), post.getLikes(), post.getShares(), formattedDateTime);
        final File csv = createCSV("ID,content,author,likes,shares,date-time\r\n" + //
                    postString);

        // Using a fresh database
        db = new Database();
        db.init("csv", csv);

        assertEquals("Retrieved ID should equal post ID", 
            db.get(post.getID()).getID(), post.getID());
        assertEquals("Retrieved content should equal post content", 
            db.get(post.getID()).getContent(), post.getContent());
        assertEquals("Retrieved author should equal post author", 
            db.get(post.getID()).getAuthor(), post.getAuthor());
        assertEquals("Retrieved likes should equal post likes", 
            db.get(post.getID()).getLikes(), post.getLikes());
        assertEquals("Retrieved shares should equal post shares", 
            db.get(post.getID()).getShares(), post.getShares());
        assertEquals("Retrieved date-time should equal post date-time", 
            db.get(post.getID()).getDateTime(), post.getDateTime());
    }

    @Test
    public void ReadCSV_Fail_InvalidPost() throws InvalidPostException, IOException {
        final String invalidPostString = "invalidID,content,author,likes,shares,date";
        final File csv = createCSV("ID,content,author,likes,shares,date-time\r\n" + //
                                    invalidPostString);

        // Using a fresh database
        db = new Database();
        db.init("csv", csv);

        assertEquals("Database should not contain invalid post", 0, db.size());
    }

    @Test
    public void ReadCSV_Fail_PostAlreadyExists() throws InvalidPostException, IOException {
        final String formattedDateTime = post.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
        final String postString = String.format("%d,%s,%s,%d,%d,%s", 
            post.getID(), post.getContent(), post.getAuthor(), post.getLikes(), post.getShares(), formattedDateTime);
        final File csv = createCSV("ID,content,author,likes,shares,date-time\r\n" + //
                    postString);

        db.init("csv", csv);

        assertEquals("Database should not add a post that already exists", 1, db.size());
    }

    private File createCSV(String content) throws IOException{
        File csv = tempFolder.newFile("temp.csv");
        FileWriter writer = new FileWriter(csv);
        writer.write(content);
        writer.close();

        return csv;
    }

    private Database databaseForSorting() throws PostAlreadyExistsException, InvalidPostException {
        Post p1 = new Post(1, "Highest likes, lowest shares", null, 999, 0, "12/12/1212 12:30");
        Post p2 = new Post(2, "Second highest likes, second lowest shares", null, 777, 222, "12/12/1212 12:30");
        Post p3 = new Post(3, "Second lowest likes, second highest shares", null, 222, 777, "12/12/1212 12:30");
        Post p4 = new Post(4, "Lowest likes, highest shares", null, 0, 999, "12/12/1212 12:30");

        Database dbForSorting = new Database();
        dbForSorting.put(p3);
        dbForSorting.put(p1);
        dbForSorting.put(p4);
        dbForSorting.put(p2);

        return dbForSorting;
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