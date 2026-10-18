# Sprint 00-02 — QueueCustom (Dairesel Kuyruk)

## 1. Sprint Bilgileri

- **Proje:** Kütüphane Ödünç–İade Sistemi
- **Geliştirici:** Yunus Emre Kul
- **Branch:** `yunusemrekul/feature`
- **Tarih:** 18.10.2026
- **Durum:** Tamamlandı

## 2. Sprint Amacı

Bu sprint kapsamında kütüphane sisteminde kitap bekleme taleplerini FIFO (First In, First Out) prensibiyle yönetmek amacıyla generic bir kuyruk veri yapısı geliştirilmiştir.

Hazır Java Queue koleksiyonları yerine, dairesel dizi (Circular Array) yaklaşımıyla `QueueCustom<T>` sınıfı uygulanmıştır.

## 3. Geliştirilen Dosyalar

| Dosya | Açıklama |
|---|---|
| `QueueCustom.java` | Generic dairesel kuyruk veri yapısı |
| `QueueCustomTest.java` | JUnit 5 birim testleri |

## 4. Kullanılan Veri Yapısı

Kuyruk, FIFO mantığıyla çalışır. İlk eklenen eleman ilk çıkarılır.

Kuyruk başlangıçta 8 elemanlık bir dizi kullanır.

Temel değişkenler:

- `elements`: Elemanların saklandığı dizi
- `front`: İlk elemanın indeksini tutar
- `size`: Kuyruktaki eleman sayısını belirtir

Yeni elemanın indeksi şu formülle hesaplanır:

`rear = (front + size) % elements.length`

Dairesel yapı sayesinde dizinin başındaki boş alanlar tekrar kullanılabilir.

Dizi tamamen dolduğunda `grow()` metodu kapasiteyi iki katına çıkarır ve FIFO sırasını koruyarak elemanları yeni diziye aktarır.

## 5. Geliştirilen Metotlar

| Metot | Açıklama |
|---|---|
| `enqueue(T item)` | Kuyruğun sonuna eleman ekler |
| `dequeue()` | İlk elemanı çıkarır ve döndürür |
| `peek()` | İlk elemanı silmeden döndürür |
| `isEmpty()` | Kuyruğun boş olup olmadığını kontrol eder |
| `size()` | Kuyruktaki eleman sayısını döndürür |
| `grow()` | Dizi kapasitesini iki katına çıkarır |

Boş kuyrukta `dequeue()` veya `peek()` çağrıldığında `NoSuchElementException` fırlatılır.

## 6. Zaman Karmaşıklığı

| İşlem | Karmaşıklık |
|---|---|
| Enqueue | O(1) amortize |
| Dequeue | O(1) |
| Peek | O(1) |
| isEmpty | O(1) |
| Size | O(1) |
| Grow | O(n) |

## 7. Birim Testleri

JUnit 5 kullanılarak aşağıdaki testler gerçekleştirilmiştir.

**fifoTest()**

- Kuyruğa üç eleman eklenmesi
- İlk elemanın `peek()` ile kontrol edilmesi
- Elemanların FIFO sırasıyla çıkarılması
- Kalan eleman sayısının doğrulanması

**circularAndGrowTest()**

- Başlangıç kapasitesinin doldurulması
- Elemanların kuyruktan çıkarılması
- Dairesel indeksleme kontrolü
- Kapasite artışının sınanması
- FIFO sırasının korunması
- Boş kuyrukta hata yönetiminin kontrol edilmesi

**Test sonucu: 2 başarılı, 0 başarısız.**

## 8. Sprint Kazanımları

- Generic veri yapısı geliştirme
- FIFO çalışma mantığının uygulanması
- Dairesel dizi ve modüler indeksleme
- Dinamik kapasite artırımı
- JUnit ile birim testlerinin gerçekleştirilmesi
- Zaman karmaşıklığı analizi

## 9. Sonraki Sprint

**Sprint 00-03 — StackCustom**

Son işlemleri LIFO (Last In, First Out) prensibine göre yönetmek amacıyla özel yığın veri yapısı geliştirilecektir.
