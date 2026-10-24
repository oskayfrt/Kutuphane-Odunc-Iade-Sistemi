# Mehmet Okan Yazıcı - Üye Yönetimi

Dal: `mehmet-okan-yazici/feature-uye-yonetimi`. Sorumluluk: `Member`, `MemberService`,
`MemberPanel`, `HashTableCustom`. MainFrame'de yalnızca üye paneli bağlanır.

## Sprintler

| Sprint | Hedef | Yapılan işler | Durum |
| --- | --- | --- | --- |
| 1 | Veri yapısı | `HashTableCustom`: ayrı zincirleme, `put/get/containsKey/remove/forEach`, yük faktörüne göre yeniden hash; `HashTableCustomTest` | Kod ve testler hazır |
| 2 | Servis ve kurallar | `Member` alan doğrulaması; `MemberService`: ekleme, güncelleme, silme, numarayla arama, listeleme, tekrarlı numara reddi; `MemberServiceTest` | Kod ve testler hazır |
| 3 | Ekran ve entegrasyon | `MemberPanel` (form, tablo, numarayla arama, üyenin ödünç kitapları), menü bağlantısı | `main`'e Pull Request #6 ile birleşti |

## Kullanım

1. JDK 17 ile `mvn clean verify` ardından `java -jar target/kutuphane-sistemi-1.0-SNAPSHOT.jar` çalıştırın.
2. **Modüller > Üye Yönetimi** seçin.
3. Üye numarası, ad soyad ve e-posta girip **Ekle**'ye basın.
4. Güncelleme/silme için tablodan üye seçin. Üye numarası değiştirilemez; **Yeni / Temizle** formu yeni kayıt için sıfırlar.
5. **Üye numarasıyla ara** alanına numarayı yazıp **Ara**'ya basın; **Tümünü göster** listeyi geri getirir.
6. Tablodan üye seçip **Ödünç kitapları**'na basınca üyenin ödünç geçmişi (iade edilmiş ve edilmemiş) listelenir.

Örnek giriş: `U1`, `Ayşe Yılmaz`, `ayse@gmail.com`.

## Veri yapısı

- Üyeler `HashTableCustom<String, Member>` içinde üye numarasıyla tutulur. Üye modülündeki en sık işlem numarayla arama/kayıt kontrolüdür; hash tablosunda bu işlem ortalama O(1)'dir (dizide doğrusal arama O(n) olurdu).
- Çakışma ayrı zincirleme ile çözülür: her hücre bir `Entry` zincirinin başını tutar, yeni kayıt zincirin başına eklenir. Anahtarlar `equals` ile karşılaştırılır.
- Dizi 16 hücreyle başlar. Kayıt sayısı hücre sayısının 0,75 katını aşacaksa dizi iki katına çıkar ve tüm kayıtlar yeni uzunluğa göre yeniden yerleştirilir (indeks `Math.floorMod(hashCode, uzunluk)` olduğu için eski indeksler geçerli kalmaz).
- `put/get/remove`: ortalama O(1), en kötü durumda (hepsi aynı zincirde) O(n). Yeniden hash O(n) sürer, ama seyrek olduğu için ekleme amortize O(1)'dir.
- Aynı anahtarla `put` yeni kayıt eklemez, değeri günceller ve eski değeri döndürür; `size` artmaz. Olmayan anahtarda `get`/`remove` `null` döndürür.
- Null anahtar `IllegalArgumentException` ile reddedilir.
- `Member` değişmez bir record'dur: `id`, `fullName`, `email`. Boş/null alanlar ve geçersiz e-posta biçimi `IllegalArgumentException` üretir; alanların baş/son boşlukları temizlenir.

## Metotların Davranışları

`MemberService`, üye kayıtları için tek giriş noktasıdır. Servis eşzamanlı çoklu yazma için tasarlanmamıştır.

| Metot | Davranış |
| --- | --- |
| `add(Member)` | Üye numarası zaten kayıtlıysa `IllegalArgumentException` |
| `update(id, fullName, email)` | Numara sabit kalır; ad/e-posta yeni kayıtla değiştirilir; üye yoksa `IllegalArgumentException` |
| `remove(id)` | Silinen kaydı döndürür; üye yoksa `IllegalArgumentException` |
| `findById(id)` | Bulunamazsa, boş veya `null` numarada `null` döner; numaranın baş/sonundaki boşluk yok sayılır |
| `findAll()` | Bağımsız `DynamicArray` kopyası; sırası hash'e bağlıdır, sabit değildir |
| `count()` | Kayıtlı üye sayısı |

Üye silme kuralı: iade edilmemiş kitabı olan üye silinemez. Bu kontrol `MemberPanel`'de, `LoanService.findByMemberId` ile yapılır; `MemberService` ödünç bilgisini bilmez (döngüsel bağımlılığı önlemek için). `MemberService.remove` doğrudan çağrılırsa bu kontrolü yapmaz.

`FileStorage` için: üyeleri kaydederken `findAll()`, yüklerken `add()` kullanılmalıdır. `findAll()` sırası sabit olmadığı için dosya içeriği her kayıtta farklı sırada yazılabilir.

## Kontroller
- `mvn clean verify`: başarılı. Birleştirme sonrası projenin tamamında 36 test, 0 hata, 0 başarısızlık.