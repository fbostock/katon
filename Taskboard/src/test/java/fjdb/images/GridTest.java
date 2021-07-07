package fjdb.images;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GridTest {

    @Test
    public void testPositions() {
        //TODO test adding an item out of order, and ensure previous elements are in their original positions
    }

    @Test
    public void test() {
        Grid<String> grid = new Grid<>(1, 4);
        grid.put(0, 1, "two");
        grid.put(0, 0, "one");
        grid.put(0, 2, "three");
        assertEquals("one", grid.get(0 ,0));
        assertEquals("two", grid.get(0 ,1));
        assertEquals("three", grid.get(0 ,2));
        assertNull(grid.get(0, 3));
        grid.put(0, 3, "four");
        assertEquals("four", grid.get(0 ,3));
    }


    @Test
    public void testIterator() {
        Grid<String> grid = new Grid<>(2, 4);
        int count = 0;
        Iterator<String> iterator = grid.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            count++;
            assertNull(next);
        }

        assertEquals(8, count);
    }

}