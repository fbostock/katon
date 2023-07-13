package fjdb.interviews.refactoring.gildedrose;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GildedRoseTest {

    @Test
    void foo() {
        Item[] items = new Item[] { new Item("foo", 0, 0) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals("fixme", app.items[0].name);
    }

    @Test
    public void test() {

        GildedRose mock = mock(GildedRose.class);

        mock.updateQuality();

        List<String> listMock = mock(List.class);
        when(listMock.size()).thenReturn(5);
        assertEquals(5, listMock.size());
//        String s = anyString();

        doThrow(new NullPointerException()).when(listMock).clear();//throw exception on void method.

//        when(listMock.get(anyInt())).thenCallRealMethod();//call real method on mock

        doAnswer(invocationOnMock -> "Always the same").when(listMock).get(anyInt());

        String element = listMock.get(1);
        assertEquals(element, "Always the same");


        //        assertThrows(IllegalArgumentException.class, () -> {
//        });


//        when(mock.updateQuality()).then

    }
}