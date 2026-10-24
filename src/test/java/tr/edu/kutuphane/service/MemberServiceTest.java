package tr.edu.kutuphane.service;

import org.junit.jupiter.api.Test;
import tr.edu.kutuphane.model.Member;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {
    private Member sample() {
        return new Member("U1", "Ayşe Yılmaz", "ayse@mail.com");
    }

    @Test void uyeEklenirVeBulunur() {
        MemberService service = new MemberService();
        service.add(sample());
        assertEquals("Ayşe Yılmaz", service.findById("U1").fullName());
        assertEquals(1, service.count());
        assertNull(service.findById("yok"));
    }

    @Test void ayniNumaraIkinciKezEklenemez() {
        MemberService service = new MemberService();
        service.add(sample());
        assertThrows(IllegalArgumentException.class, () -> service.add(sample()));
        assertEquals(1, service.count());
    }

    @Test void gecersizUyeOlusturulamaz() {
        assertThrows(IllegalArgumentException.class, () -> new Member("U2", " ", "a@b.com"));
        assertThrows(IllegalArgumentException.class, () -> new Member(null, "Ad", "a@b.com"));
        assertThrows(IllegalArgumentException.class, () -> new Member("U2", "Ad", "bozuk"));
    }

    @Test void guncellemeNumarayiKorurAdiDegistirir() {
        MemberService service = new MemberService();
        service.add(sample());
        service.update("U1", "Ayşe Demir", "ayse@mail.com");
        assertEquals("Ayşe Demir", service.findById("U1").fullName());
        assertEquals(1, service.count());
    }

    @Test void silinenUyeBulunamaz() {
        MemberService service = new MemberService();
        service.add(sample());
        service.remove("U1");
        assertNull(service.findById("U1"));
        assertEquals(0, service.count());
    }

    @Test void olmayanUyeIslemleriHataVerir() {
        MemberService service = new MemberService();
        assertThrows(IllegalArgumentException.class, () -> service.remove("yok"));
        assertThrows(IllegalArgumentException.class, () -> service.update("yok", "Ad", "a@b.com"));
    }
}