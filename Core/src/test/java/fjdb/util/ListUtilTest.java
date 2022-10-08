package fjdb.util;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Frankie Bostock on 12/08/2017.
 */
public class ListUtilTest {
    @Test
    public void first_on_list_returns_first_element() throws Exception {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(0);
        list.add(5);
        assertEquals((Integer)2, ListUtil.first(list));
    }

    @Test
    public void last_on_list_returns_last_element() throws Exception {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(0);
        list.add(5);
        assertEquals((Integer)5, ListUtil.last(list));
    }

    @Test
    public void first_and_last_on_empty_list_throws_exception() {

        ArrayList<Object> list = new ArrayList<>();
            try {
                ListUtil.first(list);
                fail();
            } catch (IndexOutOfBoundsException ignore) {
            }

        try {
            ListUtil.last(list);
            fail();
        } catch (IndexOutOfBoundsException ignore) {
        }


    }

    @Test
    public void try_first_and_tryLast_returns_null() {
        ArrayList<Object> list = new ArrayList<>();
        ArrayList<String> list2 = Lists.newArrayList("first", "second");
        assertNull(ListUtil.tryFirst(list));
        assertNull(ListUtil.tryLast(list));
        assertEquals("first", ListUtil.tryFirst(list2));
        assertEquals("second", ListUtil.tryLast(list2));
    }


}