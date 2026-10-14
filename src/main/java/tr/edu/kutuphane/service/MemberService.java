package tr.edu.kutuphane.service;

import tr.edu.kutuphane.datastructures.HashTableCustom;
import tr.edu.kutuphane.model.Member;

/** 2. kişi: Üyeleri kimlikleri üzerinden yönetir. */
public class MemberService {
    private final HashTableCustom<String, Member> members = new HashTableCustom<>();

    public int count() { return members.size(); }
    // TODO: Hash tablosu API'si sonrası kayıt, güncelleme, silme ve kimlikle sorgulama.
    // TODO: Yinelenen üye kimliğini ve geçersiz alanları kontrol edin.
}
