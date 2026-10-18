package tr.edu.kutuphane.datastructures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {
    @Test void growsAndPreservesOrder() {
        DynamicArray<Integer> array = new DynamicArray<>();
        assertTrue(array.isEmpty());
        for (int i = 0; i < 100; i++) array.add(i);
        assertEquals(100, array.size());
        for (int i = 0; i < 100; i++) assertEquals(i, array.get(i));
    }

    @Test void removesFirstMiddleAndLastAndCanBeReused() {
        DynamicArray<String> array = new DynamicArray<>();
        array.add("a"); array.add("b"); array.add("c"); array.add("d");
        assertEquals("a", array.remove(0));
        assertEquals("c", array.remove(1));
        assertEquals("d", array.remove(1));
        assertEquals("b", array.get(0));
        array.set(0, "updated");
        assertEquals("updated", array.remove(0));
        assertTrue(array.isEmpty());
        array.add(null);
        assertNull(array.get(0));
    }

    @Test void rejectsInvalidIndicesWithoutChangingContents() {
        DynamicArray<Integer> array = new DynamicArray<>();
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        array.add(7);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(1, 8));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
        assertEquals(7, array.get(0));
        assertEquals(1, array.size());
    }
}
