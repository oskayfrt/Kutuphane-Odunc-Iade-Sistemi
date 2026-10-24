# Sprint 00-03 — StackCustom (Yığın Veri Yapısı)

## 1. Sprint Bilgileri

| Bilgi | Açıklama |
|---|---|
| Proje | Kütüphane Ödünç–İade Sistemi |
| Geliştirici | Yunus Emre Kul |
| Tarih | 18.10.2026 |
| Branch | `yunusemrekul/feature` |
| Sprint | 00-03 |
| Durum | Tamamlandı |
| Test Sonucu | 3 başarılı, 0 başarısız |

## 2. Sprint Amacı

Bu sprint kapsamında, kütüphane otomasyon sisteminde son gerçekleştirilen işlemlerin sıralı biçimde tutulabilmesi amacıyla **LIFO (Last In, First Out)** prensibiyle çalışan generic bir yığın veri yapısı geliştirilmiştir.

Java'nın hazır `Stack` veya `Deque` koleksiyonları kullanılmamış, veri yapısı `Object[]` dizisi üzerinden sıfırdan uygulanmıştır.

Bu sprintte geliştirilen `StackCustom<T>` sınıfı, ilerleyen entegrasyon aşamasında sistemde gerçekleştirilen son işlemlerin takip edilmesi için kullanılabilecektir.

## 3. Geliştirilen Dosyalar

| Dosya | Açıklama |
|---|---|
| `../../../src/main/java/tr/edu/kutuphane/datastructures/StackCustom.java` | Generic yığın veri yapısı |
| `../../../src/test/java/tr/edu/kutuphane/datastructures/StackCustomTest.java` | JUnit 5 birim testleri |
| `docs/sprint-00-03/README.md` | Sprint dokümantasyonu |

## 4. Kullanılan Veri Yapısı

Stack, **son giren ilk çıkar (LIFO)** prensibiyle çalışan doğrusal bir veri yapısıdır.

Örnek:

1. Kitap eklendi.
2. Üye eklendi.
3. Kitap ödünç verildi.

Bu işlemler yığına sırasıyla eklendiğinde ilk çıkarılacak eleman `Kitap ödünç verildi` olacaktır.

### Temel değişkenler

- `elements`: Elemanların saklandığı dizi.
- `size`: Yığındaki mevcut eleman sayısı.
- `size - 1`: Yığının tepe elemanının indeksi.

Başlangıç kapasitesi 8 elemandır. Kapasite dolduğunda dizi iki katına çıkarılır.

## 5. Geliştirilen Metotlar

| Metot | Görevi |
|---|---|
| `push(T item)` | Yığının tepesine eleman ekler |
| `pop()` | Tepedeki elemanı çıkarır ve döndürür |
| `peek()` | Tepedeki elemanı çıkarmadan döndürür |
| `isEmpty()` | Yığının boş olup olmadığını kontrol eder |
| `size()` | Eleman sayısını döndürür |
| `grow()` | Dizi kapasitesini iki katına çıkarır |

### Push işlemi

Yeni eleman `elements[size]` konumuna eklenir ve eleman sayısı artırılır.

Dizi tamamen dolduğunda `grow()` çağrılır.

### Pop işlemi

Önce eleman sayısı azaltılır, ardından tepedeki eleman alınarak ilgili hücre `null` yapılır.

Bu işlem kullanılmayan nesne referanslarının bellekte tutulmasını önler.

### Peek işlemi

`elements[size - 1]` konumundaki eleman döndürülür. Eleman yığından silinmez.

### Boş yığın kontrolü

Boş yığında `pop()` veya `peek()` çağrılırsa `NoSuchElementException` fırlatılır.

## 6. Zaman Karmaşıklığı

| İşlem | Karmaşıklık |
|---|---|
| Push | O(1) amortize |
| Pop | O(1) |
| Peek | O(1) |
| isEmpty | O(1) |
| Size | O(1) |
| Grow | O(n) |

`push()` işlemi kapasite artırımı gerektirdiğinde O(n) zaman alabilir. Ancak kapasite iki katına çıkarıldığı için amortize karmaşıklık O(1)'dir.

Yığının bellek karmaşıklığı kullanılan dizi kapasitesine bağlı olarak O(n)'dir.

## 7. Birim Testleri

JUnit 5 kullanılarak üç farklı test gerçekleştirilmiştir.

### Test 1 — lifoTest()

- Üç işlem yığına eklendi.
- Eleman sayısı kontrol edildi.
- `peek()` ile tepe elemanı doğrulandı.
- Elemanlar ters eklenme sırasıyla çıkarıldı.
- Yığının boşaldığı doğrulandı.

**Sonuç: Başarılı**

### Test 2 — growTest()

- Yığına 20 eleman eklendi.
- Başlangıç kapasitesi olan 8 elemanın aşılması sağlandı.
- Dinamik kapasite artırımı kontrol edildi.
- Elemanlar ters sırayla çıkarılarak veri bütünlüğü doğrulandı.

**Sonuç: Başarılı**

### Test 3 — emptyStackTest()

- Boş yığın kontrol edildi.
- Boş yığında `pop()` çağrısının hata fırlattığı doğrulandı.
- Boş yığında `peek()` çağrısının hata fırlattığı doğrulandı.

**Sonuç: Başarılı**

### Genel test sonucu

**3 test başarılı — 0 test başarısız.**

Testler IntelliJ IDEA üzerinde JUnit 5 kullanılarak çalıştırılmıştır.

## 8. Sprint Kazanımları

- LIFO veri yapısının Java ile uygulanması.
- Generic sınıf kullanımı.
- Dizi tabanlı yığın tasarımı.
- Dinamik kapasite artırımı.
- Bellek yönetimi ve nesne referanslarının temizlenmesi.
- Hata yönetimi.
- JUnit 5 ile otomatik test.
- Algoritmaların zaman karmaşıklığının incelenmesi.

## 9. Entegrasyon Notları

`StackCustom<T>` bağımsız ve tekrar kullanılabilir bir veri yapısıdır.

İlerleyen sprintlerde kütüphane sistemindeki son işlemlerin saklanması amacıyla kullanılabilecektir.

Bu sprintte yalnızca veri yapısı geliştirilmiş ve test edilmiştir. Kullanıcı arayüzü ve iş servisleriyle entegrasyon sonraki sprintlerde yapılacaktır.

## 10. Sonraki Sprint

**Sprint 00-04 — Arama ve Sıralama Algoritmaları**

Planlanan geliştirmeler:

- `SearchAlgorithms.java`
- `SortAlgorithms.java`
- Doğrusal ve ikili arama algoritmalarının incelenmesi.
- Sıralama algoritmalarının uygulanması.
- Zaman karmaşıklığı karşılaştırması.
- JUnit testleri.
- Sprint dokümantasyonu.

---

**Geliştirici:** Yunus Emre Kul  
**Sprint:** 00-03  
**Durum:** Tamamlandı
