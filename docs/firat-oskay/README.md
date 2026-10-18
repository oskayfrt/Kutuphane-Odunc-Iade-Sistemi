# Fırat Oskay - Kitap Yönetimi

Dal: `firat-oskay/feature-kitap`. Sorumluluk: `Book`, `BookService`, `BookPanel`,
`DynamicArray`. Paket adları korunur. MainFrame'de yalnızca kitap paneli bağlanır.

## Sprintler

Takvim, ekibin vize tarihine göre netleştirilecektir. Aşağıdaki aşamalar 18 Ekim
2026 tarihinde yerelde uygulanmıştır; ekip incelemesi ve main dalına birleştirme bekler.

| Sprint | Hedef | Yapılan işler | Durum |
| --- | --- | --- | --- |
| 1 | Veri yapısı ve model | Dizi büyütme, erişim, değiştirme, silme; değişmez kitap ve stok alanları | Kod hazır |
| 2 | Servis ve kurallar | Ekleme, silme, güncelleme, arama, kimlik/ISBN tekilliği, ödünç/iade stok API'si | Kod hazır |
| 3 | Ekran ve doğrulama | Swing formu/tablosu, menü bağlantısı, hata mesajları, otomatik testler | Yerel kontroller geçti |

## Kullanım

1. JDK 17 ile `mvn clean test` çalıştırın.
2. `mvn clean verify` ardından `java -jar target/kutuphane-sistemi-1.0-SNAPSHOT.jar` çalıştırın.
3. **Modüller > Kitap Yönetimi** seçin.
4. Kimlik, ad, yazar, ISBN ve toplam stok girip **Ekle**'ye basın.
5. Güncelleme/silme için tablodan kayıt seçin. Kimlik değiştirilemez.
6. **Yeni / Temizle** formu yeni kayıt için sıfırlar. Arama alanı yazıldıkça filtreler;
   **Tümünü göster** filtreyi kaldırır.

Örnek giriş: `K1`, `İnce Memed`, `Yaşar Kemal`, `9789750800001`, `3`.
Bu ISBN yalnızca biçim örneğidir; bibliyografik doğruluğu veya sağlama hanesi doğrulanmaz.

## Veri yapısı ve model sözleşmesi

- DynamicArray gerçek `Object[]` kullanır; kapasite dolunca iki katına çıkar.
- `get/set`: O(1), `add`: amortize O(1), `remove`: O(n). Silmede sıra korunur.
- Geçersiz indeks `IndexOutOfBoundsException` üretir; dizi null eleman kabul eder.
- Book değişmez record'dur: `id`, `title`, `author`, `isbn`, `totalStock`, `availableStock`.
- Eski dört parametreli Book oluşturucusu korunur; toplam/mevcut stok 1 olur.
- ISBN-10 veya ISBN-13 biçimi kabul edilir; boşluk/tire temizlenir. Sağlama hanesi
  doğrulaması bu kapsamda yoktur. Aynı ISBN yeni kayda eklenmez, mevcut stok artırılır.
- `0 <= availableStock <= totalStock`. Ödünçte adet = toplam - mevcut.
- Arama doğrusal ve kısmi eşleşmelidir; ad/yazar için Türkçe büyük/küçük harf
  dönüşümü kullanılır. Ortak SearchAlgorithms/SortAlgorithms dosyaları değiştirilmemiştir.

## Entegrasyon sözleşmesi

BookService tüm kitap değişiklikleri için tek giriş noktasıdır. Swing işlemleri
olay işleme iş parçacığında çalışır; servis eşzamanlı çoklu yazma için tasarlanmamıştır.

| Metot | Davranış |
| --- | --- |
| `add(Book)` | Yeni kaydın mevcut stoku toplam stokuna eşit olmalı; kimlik ve ISBN benzersiz |
| `update(id, title, author, isbn, totalStock)` | Kimlik sabit; ödünçteki adet korunur, mevcut stok hesaplanır |
| `remove(id)` | Ödünçte kopya varsa silmeyi reddeder |
| `findById(id)` | Yoksa null; varsa değişmez kitap kaydı |
| `findAll()` | Bağımsız DynamicArray kopyası; dışarıdan silme servisi değiştirmez |
| `search(query, SearchField)` | TITLE, AUTHOR veya ISBN; boş sorgu tüm kayıtlar |
| `borrowCopy(id)` | Mevcut stok pozitifse bir azaltır ve yeni kaydı döndürür |
| `returnCopy(id)` | Mevcut stok toplamdan azsa bir artırır ve yeni kaydı döndürür |

Geçersiz işlemler `IllegalArgumentException` üretir. LoanService sahibi üye/kitap ve
aktif ödünç kontrollerinden sonra stok API'sini çağırmalı; işlem geçmişiyle stok
değişikliğinin birlikte başarılı olmasını sağlamalıdır. `returnCopy` tek başına hangi
üyenin iade ettiğini doğrulamaz. Kuyruk ve dosya kaydı bu modülde uygulanmamıştır.
FileStorage entegrasyonunda altı Book alanı korunmalıdır; ödünçte stok içeren dosyalar
normal yeni kitap `add` akışından ayrı, tutarlı bir yükleme tasarımı gerektirir.

## Kontroller ve kalan işler

- Otomatik: dizi kapasitesi/sırası/sınırları, kayıt tekilliği, giriş doğrulama,
  Türkçe arama, ödünç/iade stok sınırları, başarısız işlemde veri korunması.
- Swing testi: formdan ekleme/güncelleme, arama filtresi, paylaşılan stokların yenilenmesi.
- Java 17 ile Maven `clean verify`: başarılı. 12 test, 0 hata, 0 başarısızlık.
- Paketlenmiş JAR üzerinden ana sınıfın açılması, altı menü geçişi, kitap panelinin
  görünmesi, ekleme, silmeyi iptal etme, onaylayarak silme ve çıkış ayrıca doğrulandı.
- Hazır koleksiyon kullanılmadığı ve target/önbellek çıktılarının Git dışında
  kaldığı kontrol edildi.
- Kalıcı kayıt, gerçek LoanService entegrasyonu ve ekip pull request incelemesi bekler.
- GitHub deposu: https://github.com/oskayfrt/Kutuphane-Odunc-Iade-Sistemi
- Çalışma mevcut main geçmişi temel alınarak `firat-oskay/feature-kitap` dalında
  kaydedilir. Sprintler için geçmişte yapılmış gibi commit üretilmemiştir.
- Başlangıç deposunda takip edilen target çıktıları bu dalda Git takibinden
  çıkarılmıştır; yeniden derleme çıktıları .gitignore ile dışarıda kalır.
