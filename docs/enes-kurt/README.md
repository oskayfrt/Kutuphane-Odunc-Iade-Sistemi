# 3. Kişi — Ödünç Alma ve İade Modülü (Enes Kurt)

Bu modül; üyelerin kitap ödünç almasını, iade işlemlerini, tarih/gecikme takibini ve tüm işlem geçmişinin özel tek yönlü bağlı listede (`LinkedListCustom`) tutulmasını sağlar.

## Sprint 1: Tek Yönlü Bağlı Liste (`LinkedListCustom`) ve `Loan` Modeli
- `LinkedListCustom.java`: Hazır Java koleksiyonları kullanılmadan düğüm (`Node`) tabanlı generic bağlı liste yazıldı. `head`, `tail` ve `size` tutarlılığını koruyan ekleme (`addFirst`, `addLast`), silme (`remove`, `removeIfFirst`, `removeAt`), arama (`find`), güncelleme (`replaceFirst`) ve filtreleme (`filter`) metotları tamamlandı.
- `Loan.java`: Değişmez (`record`) yapı korunarak iade durumu (`isReturned`), yeni iade kaydı üretme (`withReturnedDate`), gecikme kontrolü (`isOverdue`) ve gecikme gün sayısı hesaplama (`overdueDays`) metotları eklendi.
- `LinkedListCustomTest.java`: Bağlı liste sınır durumları ve düğüm silme/ekleme tutarlılığı için JUnit 5 testleri yazıldı.

## Sprint 2: Ödünç ve İade Servisi (`LoanService`) ve Birim Testleri
- `LoanService.java`: `BookService` ve `MemberService` ile entegre çalışacak şekilde ödünç verme (`borrowBook`), stok düşürme/artırma (`borrowCopy` / `returnCopy`), mükerrer ödünç engelleme, iade alma (`returnBook`) ve geciken kitapları filtreleme (`findOverdueLoans`) iş kuralları kodlandı.
- `LoanServiceTest.java`: Ödünç alma, stok kontrolü, iade ve gecikme tespiti senaryolarını doğrulayan JUnit 5 testleri eklendi.

## Sprint 3: Kullanıcı Arayüzü (`LoanPanel`) ve Ana Pencere Entegrasyonu
- `LoanPanel.java`: Swing tabanlı ödünç verme ve iade alma ekranı, görünüm filtreleri (Tüm geçmiş, Aktif ödünçler, Geciken kitaplar, İade edilenler) ve arama çubuğu ile birlikte tasarlandı.
- `MainFrame.java`: "Ödünç / İade" menüsü `LoanPanel` ile bağlandı.