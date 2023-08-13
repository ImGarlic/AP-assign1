package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import smanalyzer.service.Database;
import smanalyzer.exception.InvalidPostException;
import smanalyzer.exception.PostAlreadyExistsException;
import smanalyzer.exception.PostNotExistException;
import smanalyzer.model.Post;

public class DatabaseTest {

    Database db;
    Post post;

    @Before
    public void setUpDatabase() throws PostAlreadyExistsException, InvalidPostException {
        db = new Database();
        post = new Post(1,"test content","test author", 10, 10, "01/01/2000 12:00");

        db.put(post);
    }

    @Test
    public void Get_Success() throws PostNotExistException {
       assertEquals(db.get(post.getID()), post);
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
        assertEquals(db.size(), 2);
        assertEquals(db.get(post.getID()), post);
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

    // We want to test getMulti for when :
    // count < database size
    // count == database size
    // count > database size
    @ParameterizedTest
    @ValueSource(ints = {3,4,5})
    public void GetMulti_Success_SortOnLikes(int count) throws PostAlreadyExistsException, InvalidPostException {
        Post highest = new Post(1, "Highest likes", null, 999, 0, "12/12/1212 12:30");
        Post secondHighest = new Post(2, "Second highest likes", null, 700, 0, "12/12/1212 12:30");
        Post secondLowest = new Post(3, "Second lowest likes", null, 200, 0, "12/12/1212 12:30");
        Post lowest = new Post(4, "Lowest likes", null, 0, 0, "12/12/1212 12:30");

        db = new Database();
        db.put(secondHighest);
        db.put(lowest);
        db.put(highest);
        db.put(secondLowest);

        List<Post> actualList = db.getMulti("likes", count);
        List<Post> expectedList = new ArrayList<Post>(Arrays.asList(highest, secondHighest, secondLowest, lowest));
        expectedList.sort(SORT_ON_LIKES);
        if (count < expectedList.size()) {
            expectedList = expectedList.subList(0, count);
        }

        assertEquals("List should be sorted on likes", expectedList, actualList);
    }

    private static Comparator<Post> SORT_ON_LIKES = new Comparator<Post>() {
		public int compare(Post p1, Post p2) {
			return p2.getLikes() - p1.getLikes();
		}
	};
	
	// private static Comparator<Post> SORT_ON_SHARES = new Comparator<Post>() {
	// 	public int compare(Post p1, Post p2) {
	// 		return p2.getShares() - p1.getShares();
	// 	}
	// };

}