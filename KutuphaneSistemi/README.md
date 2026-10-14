# Kütüphane Ödünç ve İade Takip Sistemi

Java ve Veri Yapıları dersi için dört kişilik ekip başlangıç projesidir. Hedef;
kitap ve üye kayıtlarını yönetmek, ödünç/iade işlemlerini takip etmek, bekleme
kuyruğu oluşturmak ve kendi veri yapılarımızı kullanmaktır.

## Gereksinimler ve çalıştırma

- JDK 17 veya Java 17 hedefini destekleyen daha yeni bir JDK
- Maven 3.9+ (ilk derlemede eklentilerin indirilmesi için internet)
- Git; ekip deposu için GitHub hesabı
- Swing arayüzü için grafik masaüstü ortamı

Proje kökünde:

```sh
mvn clean verify
java -jar target/kutuphane-sistemi-1.0-SNAPSHOT.jar
```

NetBeans'te **Open Project** ile `pom.xml` bulunan klasörü açın. JDK 17 seçin;
ana sınıf `tr.edu.kutuphane.Main` olur. Uygulama ana pencereyle açılır; **Modüller**
menüsündeki seçimler ilgili başlangıç panelini gösterir.

## Mevcut kapsam

Ana pencere ve menü geçişleri çalışır. Modeller Java 17 `record` türleridir;
alanlara `id()`, `title()` gibi erişilir. Modeller değişmezdir; güncelleme yeni
kayıt oluşturularak yapılacaktır. `Loan.returnedDate()` iade öncesinde null olabilir.

Veri yapılarında yalnızca depolama alanları, boş durum ve boyut sorguları hazırdır.
Ekleme/silme gibi henüz geliştirilmeyen işlemler API'ye eklenmemiştir. Servislerde
başlangıç kayıt sayısı sıfırdır. Arama, sıralama, kayıt/yükleme ve iş kuralları TODO
olarak bırakılmıştır; kitap/üye işlemleri ve kalıcı veri kaydı henüz sunulmaz.

## Paket yapısı

Sabit kök paket: `tr.edu.kutuphane`. Tüm kaynaklar `src/main/java` altındadır.

```text
pom.xml
src/main/java/tr/edu/kutuphane/
├── Main.java
├── model/          Book.java, Member.java, Loan.java
├── datastructures/ DynamicArray.java, LinkedListCustom.java,
│                   QueueCustom.java, StackCustom.java, HashTableCustom.java
├── service/        BookService.java, MemberService.java, LoanService.java
├── algorithms/     SearchAlgorithms.java, SortAlgorithms.java
├── storage/        FileStorage.java
└── ui/             MainFrame.java
```

## Dört kişilik görev dağılımı

| Üye | Sorumluluk |
| --- | --- |
| 1. kişi | Kitap yönetimi, BookService, kitap paneli, DynamicArray |
| 2. kişi | Üye yönetimi, MemberService, üye paneli, HashTableCustom |
| 3. kişi | Ödünç/iade, LoanService, işlem paneli, LinkedListCustom |
| 4. kişi | MainFrame, QueueCustom, StackCustom, arama/sıralama, FileStorage, entegrasyon |

## Ekip çalışma kuralları

- Paket ve mevcut sınıf isimlerini değiştirmeyin. Yeni arayüz panelleri `ui` paketine eklenir.
- `LinkedList`, `Queue`, `Stack`, `HashMap` veya başka hazır koleksiyonlarla temel
  veri yapılarını taklit etmeyin. Diziler ve kendi düğüm sınıflarınızı kullanın.
- Yeni metot imzalarını birlikte kararlaştırın; sınır durumlarını ve hata
  davranışlarını belgeleyin. TODO'ları çalışan uygulama ve anlamlı testlerle tamamlayın.
- İş kuralları servislerde, kullanıcı etkileşimi Swing panellerinde yer alır.
  Ortak BookService/MemberService örnekleri LoanService'e aktarılır.
- Her kişi `feature/kitap`, `feature/uye`, `feature/odunc-iade` veya
  `feature/entegrasyon` dalında çalışsın. Küçük commit'ler ve pull request kullanın.
- Birleştirme öncesinde `mvn clean verify` çalıştırın; menüleri elle kontrol edin.
- `target/`, derlenmiş dosyalar, kişisel IDE ayarları, yerel yapılandırmalar ve
  `data/` altındaki kullanıcı kayıtları Git'e dahil edilmez.

## GitHub deposunu başlatma

GitHub'da boş depo oluşturduktan sonra proje kökünde (URL'yi kendi deponuzla değiştirin):

```sh
git init
git add .
git commit -m "Kütüphane sistemi başlangıç projesi"
git branch -M main
git remote add origin https://github.com/EKIP/KutuphaneSistemi.git
git push -u origin main
```

Gönderimden önce `git status` ile dosyaları inceleyin. Bu başlangıç hazırlığı
GitHub'a otomatik gönderim yapmaz.
