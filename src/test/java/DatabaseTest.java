package test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import smanalyzer.java.exception.InvalidPostException;
import smanalyzer.java.exception.PostAlreadyExistsException;
import smanalyzer.java.exception.PostNotExistException;
import smanalyzer.java.model.Post;
import smanalyzer.java.service.Database;

public class DatabaseTest {

    Database db;
    Post post;
    static int DATABASE_SIZE = 1;

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
        int ID = 9999;
        String message = String.format("Post of ID %d does not exist in the collection.", ID);

        assertThrows(message, PostNotExistException.class, () -> db.get(ID));
    }

    @Test
    public void Put_Success() throws PostAlreadyExistsException, InvalidPostException, PostNotExistException {
        Post post = new Post(2,"test content 2","test author 2", 20, 20, "02/02/2000 12:00");

        db.put(post);
        assertEquals("Database should now have +1 entries", db.size(), 2);
        assertEquals("Database should now contain the new post", db.get(post.getID()), post);
    }

    @Test
    public void Put_Fail_PostAlreadyExists() throws PostAlreadyExistsException, InvalidPostException, PostNotExistException {
        Post newPost = new Post(1,"test content","test author", 10, 10, "01/01/2000 12:00");
        String message = String.format("Post of ID %d already exists in the collection.", newPost.getID());

        assertThrows(message, PostAlreadyExistsException.class, () -> db.put(newPost));
        assertEquals(db.size(), 1);
    }

    @Test
    public void Delete_Success() throws PostNotExistException {
        String message = String.format("Post of ID %d does not exist in the collection.", post.getID());

        db.delete(post.getID());

        assertThrows(message, PostNotExistException.class, () -> db.get(post.getID()));
        assertEquals(db.size(), 0);
    }

    @Test
    public void Delete_Fail_PostNotExist() throws PostNotExistException {
        int ID = 9999;
        String message = String.format("Post of ID %d does not exist in the collection.", ID);

        assertThrows(message, PostNotExistException.class, () -> db.delete(ID));
        assertEquals(db.size(), 1);
    }

    @Test
    public void GetMulti_Success_SortOnLikes() throws PostNotExistException, PostAlreadyExistsException, InvalidPostException {
        db = databaseForSorting();
        int count = 3;
        List<Post> actualList = db.getMulti("likes", count);
        List<Post> expectedList = new ArrayList<Post>(Arrays.asList(db.get(1), db.get(2), db.get(3), db.get(4)));

        expectedList.sort(SORT_ON_LIKES);
        expectedList = expectedList.subList(0, count);
        assertEquals("List should be sorted on likes", expectedList, actualList);
    }

    @Test
    public void GetMulti_Success_SortOnShares() throws PostNotExistException, PostAlreadyExistsException, InvalidPostException {
        db = databaseForSorting();
        int count = 3;
        List<Post> actualList = db.getMulti("shares", count);
        List<Post> expectedList = new ArrayList<Post>(Arrays.asList(db.get(1), db.get(2), db.get(3), db.get(4)));

        expectedList.sort(SORT_ON_SHARES);
        expectedList = expectedList.subList(0, count);
        assertEquals("List should be sorted on shares", expectedList, actualList);
    }

    //GetMulti should just give us the full list when the given count is over the database size
    @Test
    public void GetMulti_Success_SortOnLikes_OverMax() throws PostNotExistException, PostAlreadyExistsException, InvalidPostException {
        db = databaseForSorting();
        int count = 5;
        List<Post> actualList = db.getMulti("likes", count);
        List<Post> expectedList = new ArrayList<Post>(Arrays.asList(db.get(1), db.get(2), db.get(3), db.get(4)));

        expectedList.sort(SORT_ON_LIKES);
        assertEquals("List should be sorted on likes", expectedList, actualList);
    }

    //GetMulti should just give us the full list when the given count is over the database size
    @Test
    public void GetMulti_Success_SortOnShares_OverMax() throws PostNotExistException, PostAlreadyExistsException, InvalidPostException {
        db = databaseForSorting();
        int count = 5;
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
    public void ReadCSV_Success() throws InvalidPostException, PostNotExistException, FileNotFoundException {
        final Post POST_FROM_CSV = new Post(1,"a","a",1,1,"12/12/2000 12:30");

        db = new Database();
        db.init("csv", "src/test/testCSV/success.csv");

        assertEquals("Retrieved post should equal POST_FROM_CSV", db.get(POST_FROM_CSV.getID()), POST_FROM_CSV);
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