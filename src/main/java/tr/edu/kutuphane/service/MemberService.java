package tr.edu.kutuphane.service;

import java.util.Objects;
import tr.edu.kutuphane.datastructures.DynamicArray;
import tr.edu.kutuphane.datastructures.HashTableCustom;
import tr.edu.kutuphane.model.Member;

public class MemberService {
    private final HashTableCustom<String, Member> members = new HashTableCustom<>();

    public int count() { return members.size(); }

    public void add(Member member) {
        Objects.requireNonNull(member, "Üye gerekli.");
        if (members.containsKey(member.id())) {
            throw new IllegalArgumentException("Bu üye numarası zaten kayıtlı.");
        }
        members.put(member.id(), member);
    }

    /** Üye numarası sabit kalır; Member değişmez olduğu için yeni kayıt üretilir. */
    public Member update(String id, String fullName, String email) {
        Member previous = requireMember(id);
        Member updated = new Member(previous.id(), fullName, email);
        members.put(updated.id(), updated);
        return updated;
    }

    public Member remove(String id) {
        Member existing = requireMember(id);
        return members.remove(existing.id());
    }

    /** Bulunamazsa null döner (LoanService bu davranışa güveniyor). */
    public Member findById(String id) {
        if (id == null || id.isBlank()) return null;
        return members.get(id.strip());
    }

    /** Bağımsız kopya; sıra hash'e bağlıdır, sabit değildir. */
    public DynamicArray<Member> findAll() {
        DynamicArray<Member> result = new DynamicArray<>();
        members.forEach((key, member) -> result.add(member));
        return result;
    }

    private Member requireMember(String id) {
        Member member = findById(id);
        if (member == null) throw new IllegalArgumentException("Üye bulunamadı.");
        return member;
    }
}