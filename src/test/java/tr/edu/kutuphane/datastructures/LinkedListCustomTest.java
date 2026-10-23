package tr.edu.kutuphane.datastructures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinkedListCustomTest {

    @Test
    void newListShouldBeEmpty() {
        LinkedListCustom<String> list = new LinkedListCustom<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addFirstAndAddLastShouldMaintainOrder() {
        LinkedListCustom<String> list = new LinkedListCustom<>();
        list.addLast("B");
        list.addFirst("A");
        list.addLast("C");

        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void removeFirstMiddleAndLastShouldKeepHeadAndTailConsistent() {
        LinkedListCustom<Integer> list = new LinkedListCustom<>();
        list.add(10);
        list.add(20);
        list.add(30);

        // Ortadaki düğümü sil
        assertTrue(list.remove(20));
        assertEquals(2, list.size());
        assertEquals(10, list.get(0));
        assertEquals(30, list.get(1));

        // Son düğümü (tail) sil ve sonrasında yeni eleman ekleyerek tail bağlantısını doğrula
        assertTrue(list.remove(30));
        assertEquals(1, list.size());
        list.add(40);
        assertEquals(2, list.size());
        assertEquals(40, list.get(1));

        // İlk düğümleri silerek listeyi tamamen boşalt
        assertTrue(list.remove(10));
        assertTrue(list.remove(40));
        assertTrue(list.isEmpty());
    }

    @Test
    void findFilterAndReplaceFirstShouldWorkCorrectly() {
        LinkedListCustom<String> list = new LinkedListCustom<>();
        list.add("Kitap-1");
        list.add("Kitap-2");
        list.add("Dergi-1");

        assertEquals("Kitap-2", list.find(s -> s.endsWith("2")));

        LinkedListCustom<String> filtered = list.filter(s -> s.startsWith("Kitap"));
        assertEquals(2, filtered.size());

        assertTrue(list.replaceFirst(s -> s.equals("Kitap-1"), "Kitap-1-Guncel"));
        assertEquals("Kitap-1-Guncel", list.get(0));
    }
}