# Cosmic Canvas

Cosmic Canvas, NASA'nın Günün Astronomi Fotoğrafı (Astronomy Picture of the Day - APOD) içeriğini modern Android geliştirme teknikleri ve kütüphaneleri kullanarak sergileyen bir mobil uygulamadır.

## Özellikler

- NASA'nın Günün Astronomi Fotoğraflarını detaylarıyla görüntüleme
- Son 7 güne ait APOD içeriklerini gezinme
- APOD içeriklerini tam ekran modunda hareket destekli görüntüleme
- Favori APOD içeriklerini daha sonra görüntülemek üzere kaydetme
- APOD içeriklerini başkalarıyla paylaşma
- Son APOD içerikleri arasında animasyonlu geçişlerle ekran koruyucu modu
- Karanlık mod desteği
- Yerel önbelleğe alma ile çevrimdışı destek
- Yeni APOD içerikleri ve anahtar kelime eşleşmeleri için bildirimler
- Farklı ekran boyutları ve yönlendirmeleri için duyarlı tasarım
- NASA API anahtarları için şifreleme ile güvenli depolama
- Entegre çeviri özelliği ile içerikleri istediğiniz dilde görüntüleme

## Mimari

Bu uygulama, MVVM deseni ile Clean Architecture prensiplerine uymaktadır:

- **Veri Katmanı (Data Layer)**: Repository uygulamaları, API servisleri, veritabanı erişimi
- **Alan Katmanı (Domain Layer)**: İş mantığı, kullanım durumları, repository arayüzleri
- **Sunum Katmanı (Presentation Layer)**: UI bileşenleri, ViewModels, durum yönetimi

## Teknoloji Yığını

- **Kotlin**: %100 Kotlin ile uygulama geliştirme
- **Jetpack Compose**: Modern deklaratif UI kiti
- **Coroutines & Flow**: Asenkron programlama
- **Hilt**: Bağımlılık enjeksiyonu
- **Room**: Yerel önbelleğe alma için veritabanı
- **Retrofit & OkHttp**: Ağ iletişimi
- **Coil**: Görüntü yükleme
- **WorkManager**: Arka plan işleme
- **DataStore**: Kullanıcı tercihleri yönetimi
- **Material 3**: Modern Material Design bileşenleri
- **Navigation Component**: Uygulama içi navigasyon

## Nasıl Çalışır?

### NASA APOD Entegrasyonu

1. Uygulama, NASA'nın APOD API'sini kullanarak günlük astronomi fotoğraflarını çeker
2. Veriler, çevrimdışı erişim için Room veritabanında önbelleğe alınır
3. Kullanıcılar son 7 güne ait içerikleri kaydırarak gezinebilir
4. İçerikler hem resim hem de video formatında olabilir ve uygulama her ikisini de destekler

### Çeviri Özellikleri

1. Uygulama, harici API anahtarları gerektirmeden Google Translate'i kullanan özel bir çeviri sistemi içerir
2. İçerikler (başlık ve açıklama) istenilen dile çevrilebilir
3. Çeviriler yerel veritabanında önbelleğe alınarak internet kullanımı optimize edilir
4. Rate limiting sorunlarını çözmek için yeniden deneme mekanizması ve üstel geri çekilme algoritması uygulanır
5. Kullanıcılar orijinal metin ve çeviri arasında kolayca geçiş yapabilir

### Veri Akışı

1. **NASA API**: APOD verileri NASA'nın resmi API'sinden alınır
2. **Yerel Önbellek**: Veriler Room veritabanında saklanır
3. **Repository Katmanı**: Veri kaynakları arasındaki iletişimi yönetir
4. **Use Cases**: İş mantığını encapsule eder
5. **ViewModels**: UI durumunu yönetir ve kullanım durumlarını çağırır
6. **UI (Compose)**: Kullanıcı arayüzünü state'e göre render eder

## Kurulum

1. Depoyu klonlayın
2. [NASA API Portalı](https://api.nasa.gov/)'ndan bir NASA API anahtarı edinin
3. API anahtarını `app/build.gradle.kts` dosyasına ekleyin ("DEMO_KEY" yerine gerçek anahtarınızı koyun):
   ```kotlin
   buildConfigField("String", "NASA_API_KEY", "\"API_ANAHTARINIZ\"")
   ```
4. Uygulamayı derleyin ve çalıştırın

## Geliştirme Komutları

```bash
# Projeyi derle
./gradlew build

# Debug versiyonunu kur
./gradlew installDebug

# Testleri çalıştır
./gradlew test

# Lint kontrolünü çalıştır
./gradlew lint
```

## Mimari Bileşenler

### Veri Katmanı

- **API Servisleri**: NASA APOD verilerini çeker ve çeviri hizmetleriyle iletişim kurar
- **DAO'lar**: Room veritabanı için veri erişim nesneleri
- **Repository Implementations**: Veri kaynaklarını birleştirir ve domain katmanına sunar
- **Entities ve DTOs**: Veri modelleri

### Alan Katmanı

- **Use Cases**: `GetRecentApodsUseCase`, `TranslateTextUseCase` gibi iş mantığı fonksiyonları
- **Repository Interfaces**: Veri erişimi için sözleşmeler
- **Domain Models**: İş mantığı için saf Kotlin veri sınıfları

### Sunum Katmanı

- **ViewModels**: `HomeViewModel`, `DetailsViewModel` gibi UI durum yöneticileri
- **Composables**: Jetpack Compose ile oluşturulan UI bileşenleri
- **Screen States**: UI'ın çeşitli durumlarını temsil eden sınıflar (loading, success, error)

## Önemli Bileşenler

### SimpleTranslateService

Google Translate API'sine doğrudan URL tabanlı bağlantı kurar, API anahtarı gerektirmez. Rate limiting sorunlarını aşmak için gelişmiş stratejiler uygular.

### TranslatedText Composable

Orijinal veya çevrilmiş metni göstermek için kullanılan UI bileşeni. Kullanıcılar orijinal ve çevrilmiş içerik arasında geçiş yapabilir.

### APOD Veritabanı

Room kullanarak APOD verilerini ve çevirileri yerel olarak saklar. Bu, çevrimdışı kullanımı ve daha hızlı yükleme sürelerini mümkün kılar.

## Veritabanı Şeması

### Veri Tabloları

#### 1. APOD Tablosu (apods)

```
+---------------+----------+-----------------------------------+
| Alan          | Tür      | Açıklama                         |
+---------------+----------+-----------------------------------+
| date          | String   | Birincil anahtar, YYYY-MM-DD     |
| title         | String   | APOD başlığı                     |
| explanation   | String   | APOD açıklaması                  |
| url           | String   | Medya URL'si                     |
| mediaType     | String   | Medya türü (image veya video)    |
| thumbnailUrl  | String   | Küçük resim URL'si               |
| copyright     | String   | Telif hakkı bilgisi              |
| isFavorite    | Boolean  | Favori durumu                    |
+---------------+----------+-----------------------------------+
```

#### 2. Çeviri Tablosu (translations)

```
+---------------+----------+-----------------------------------+
| Alan          | Tür      | Açıklama                         |
+---------------+----------+-----------------------------------+
| sourceText    | String   | Birincil anahtar 1, kaynak metin |
| targetLanguage| String   | Birincil anahtar 2, hedef dil    |
| translatedText| String   | Çevrilmiş metin                  |
| sourceLanguage| String   | Kaynak dil kodu                  |
| timestamp     | Long     | Çeviri zaman damgası             |
+---------------+----------+-----------------------------------+
```

### İlişkiler ve Sorgular

CosmicDatabase iki ana DAO (Veri Erişim Nesnesi) içerir:

#### ApodDao

- `getApodByDate`: Belirli bir tarihe göre APOD arar
- `getRecentApods`: Son eklenen APOD'ları belirli bir sayıyla sınırlı olarak getirir
- `getApodsBetweenDates`: Belirli tarih aralığındaki APOD'ları getirir
- `getFavoriteApods`: Favori işaretlenmiş APOD'ları getirir
- `searchApods`: Başlık veya açıklamada belirli bir anahtar kelimeyi arar
- `updateFavoriteStatus`: Favori durumunu günceller

#### TranslationDao

- `insertTranslation`: Yeni çeviri ekler veya varsa günceller
- `getTranslation`: Kaynak metin ve hedef dil için çeviri getirir
- `deleteExpiredTranslations`: Belirli bir süreden eski çevirileri temizler
- `clearAllTranslations`: Tüm çevirileri siler

## Lisans

Bu proje MIT Lisansı altında lisanslanmıştır - detaylar için LICENSE dosyasına bakın.

## Teşekkürler

- Harika içeriği sağladığı için [NASA APOD API](https://api.nasa.gov/)
- Bu projeyi mümkün kılan tüm açık kaynak kütüphaneler ve araçlar