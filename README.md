# Cosmic Canvas

A modern Android application that showcases NASA's Astronomy Picture of the Day (APOD) using the
latest Android development techniques and libraries.

<img src="https://github.com/user-attachments/assets/bab85264-c563-4aab-b89e-ddfda73ea096" width="300" alt="CosmicCanvas.Logo"/>

## Screen Saver Mode

### Settings > Screen Saver Timeout
Users can configure the screen saver timeout period from this menu. After the selected period of inactivity, the device will automatically activate the screen saver mode.

<img width="400" alt="image" src="https://github.com/user-attachments/assets/1dffd56e-78a7-4c7b-ac4a-43edf1849b36" />

### Automatic Screen Saver Display
Screen saver mode displays NASA’s Astronomy Photo of the Day (APOD) for the past 7 days, creating a visually engaging and educational experience.

https://github.com/user-attachments/assets/0c4b48bc-92bd-437a-ab90-7507a8fb42e1

## Features

- Display NASA's Astronomy Picture of the Day with details
- Browse through recent APOD entries (last 7 days)
- View APOD entries in fullscreen mode with gesture support
- Save favorite APOD entries for later viewing
- Share APOD entries with others
- Screen saver mode with animated transitions between recent APODs
- Dark mode support
- Offline support via local caching
- Notifications for new APODs and keyword matches
- Responsive design for different screen sizes and orientations
- Secure storage for NASA API keys with encryption

## Architecture

This application follows Clean Architecture principles with MVVM pattern:

- **Data Layer**: Repository implementation, API services, database access
- **Domain Layer**: Business logic, use cases, repository interfaces
- **Presentation Layer**: UI components, ViewModels, state management

## Tech Stack

- **Kotlin**: 100% Kotlin for app development
- **Jetpack Compose**: Modern declarative UI toolkit
- **Coroutines & Flow**: Asynchronous programming
- **Hilt**: Dependency injection
- **Room**: Database for local caching
- **Retrofit & OkHttp**: Networking
- **Coil**: Image loading
- **WorkManager**: Background processing
- **DataStore**: User preferences management
- **Material 3**: Modern Material Design components
- **Navigation Component**: In-app navigation

## Setup

1. Clone the repository
2. Obtain a NASA API key from [NASA API Portal](https://api.nasa.gov/)
3. Build and run the application

## Database Schema

### Data Tables

#### 1. APOD Table (apods)

```
+---------------+----------+-----------------------------------+
| Field         | Type     | Description                       |
+---------------+----------+-----------------------------------+
| date          | String   | Primary key, format YYYY-MM-DD    |
| title         | String   | APOD title                        |
| explanation   | String   | APOD explanation                  |
| url           | String   | Media URL                         |
| mediaType     | String   | Media type (image or video)       |
| thumbnailUrl  | String   | Thumbnail URL                     |
| copyright     | String   | Copyright information             |
| isFavorite    | Boolean  | Favorite status                   |
+---------------+----------+-----------------------------------+
```

#### 2. Translation Table (translations)

```
+---------------+----------+-----------------------------------+
| Field         | Type     | Description                       |
+---------------+----------+-----------------------------------+
| sourceText    | String   | Primary key 1, source text        |
| targetLanguage| String   | Primary key 2, target language    |
| translatedText| String   | Translated text                   |
| sourceLanguage| String   | Source language code              |
| timestamp     | Long     | Translation timestamp             |
+---------------+----------+-----------------------------------+
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
