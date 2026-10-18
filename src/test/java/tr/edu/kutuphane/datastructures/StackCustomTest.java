
package tr.edu.kutuphane.datastructures;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class StackCustomTest {

    @Test
    void lifoTest() {
        StackCustom<String> stack = new StackCustom<>();

        stack.push("Kitap eklendi");
        stack.push("Uye eklendi");
        stack.push("Kitap odunc verildi");

        assertEquals(3, stack.size());

        assertEquals("Kitap odunc verildi", stack.peek());
        assertEquals("Kitap odunc verildi", stack.pop());
        assertEquals("Uye eklendi", stack.pop());
        assertEquals("Kitap eklendi", stack.pop());

        assertTrue(stack.isEmpty());
    }

    @Test
    void growTest() {
        StackCustom<Integer> stack = new StackCustom<>();

        for (int i = 0; i < 20; i++) {
            stack.push(i);
        }

        assertEquals(20, stack.size());

        for (int i = 19; i >= 0; i--) {
            assertEquals(i, stack.pop());
        }

        assertTrue(stack.isEmpty());
    }

    @Test
    void emptyStackTest() {
        StackCustom<String> stack = new StackCustom<>();

        assertTrue(stack.isEmpty());

        assertThrows(
                NoSuchElementException.class,
                stack::pop
        );

        assertThrows(
                NoSuchElementException.class,
                stack::peek
        );
    }
}
