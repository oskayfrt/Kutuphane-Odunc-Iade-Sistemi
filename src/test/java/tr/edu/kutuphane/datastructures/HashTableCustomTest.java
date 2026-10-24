package tr.edu.kutuphane.datastructures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashTableCustomTest {

    @Test void ayniAnahtarGuncellenirBoyutArtmaz() {
        HashTableCustom<String, Integer> table = new HashTableCustom<>();
        assertNull(table.put("a", 1));
        assertEquals(1, table.put("a", 2));   // put eski değeri döndürür
        assertEquals(1, table.size());
        assertEquals(2, table.get("a"));
    }

    @Test void cakisanAnahtarlarBagimsizSilinir() {
        HashTableCustom<String, Integer> table = new HashTableCustom<>();
        table.put("Aa", 1);                   // "Aa" ve "BB" aynı hashCode'a sahip
        table.put("BB", 2);
        assertEquals(1, table.remove("Aa"));
        assertNull(table.get("Aa"));
        assertEquals(2, table.get("BB"));
        assertEquals(1, table.size());
    }

    @Test void olmayanAnahtarNullDoner() {
        HashTableCustom<String, Integer> table = new HashTableCustom<>();
        assertNull(table.get("yok"));
        assertNull(table.remove("yok"));
        assertFalse(table.containsKey("yok"));
    }

    @Test void yenidenHashSonrasiHicKayitKaybolmaz() {
        HashTableCustom<String, Integer> table = new HashTableCustom<>();
        for (int i = 0; i < 200; i++) table.put("k" + i, i);
        assertEquals(200, table.size());
        for (int i = 0; i < 200; i++) assertEquals(i, table.get("k" + i));
    }

    @Test void nullAnahtarReddedilir() {
        HashTableCustom<String, Integer> table = new HashTableCustom<>();
        assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
        assertThrows(IllegalArgumentException.class, () -> table.get(null));
        assertThrows(IllegalArgumentException.class, () -> table.remove(null));
    }
}