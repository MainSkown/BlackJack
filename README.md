
# 🃏Blackjack


The Blackjack app allows users to play a classic game with customizable card designs and backgrounds. Players can track their high scores and records. 

---

## 🧩 Features
- 🎴 Customizable cards and backgrounds  
- 🧑‍💻 User-friendly interface using Jetpack Compose  
- 📊 Ability to view high scores  
- 🧱 Room Database integration  

---

## 📂Project Structure

```plaintext
📁app/
├── 📁kotlin+java/
│   └── 📁com.mainskown.blackjack/          
│       ├── 📄MainActivity.kt               
│       ├── 📁models/
│       └── 📁ui/
│           ├── 📁pages/
│           ├── 📁theme/
│           └── 📁components/
├── 📁assets/
├── 📁res/
└── 📄AndroidManifest.xml
```
---
### 📁 kotlin+java
Source code folder with Kotlin files under the package com.mainskown.blackjack
### 📄MainActivity.kt
Main entry point of the app handling the primary UI and navigation.
### 📁models
The models folder contains core data structures, state management classes, and business logic essential for the game’s functionality. It handles game data, user preferences, sound management, UI view models, and score tracking, integrating closely with the app’s data storage and presentation layers.
### 📁ui
The ui folder contains the app’s user interface code, organized into screens, reusable components, and theming elements to create a consistent and interactive visual experience.
### 📁pages
Composable screens built with Jetpack Compose that represent different app views, such as the game screen, high scores, and settings.
### 📁theme
The theme folder holds files that set the app’s colors, fonts, shapes, and overall look to keep the design consistent and easy to change.
### 📁components
The components folder has reusable parts of the UI like cards showing the dealer’s and player’s hands, buttons, and other small pieces used in different screens.
### 📁assets
The assets folder contains custom cards, backgrounds, and intro media used in the app.
### 📁res
The res folder holds app resources like translations (Polish and English), app icons and mipmaps, fonts, and rules used in the app.
### 📄AndroidManifest.xml
AndroidManifest.xml defines the app’s main activity, icon, theme, backup rules, and launch behavior. It also includes settings for Android 12+ compatibility.

---
## 🧩 Libraries Used

* **Jetpack Compose** – for building a modern, declarative UI.
* **Room** – for local data storage and database access.
* **Material 3** – for applying modern Material Design components.
* **Navigation Compose** – to handle in-app screen navigation.
* **Media3 ExoPlayer** – for audio playback and media handling.
* **Compose Colorful Sliders** – custom sliders for color selection.
* **JUnit & Espresso** – for unit and UI testing.
* **AppCompat & Core KTX** – for backward compatibility and Kotlin extensions.

---
##  📄 Licenses
This project uses the following third-party assets:
- **Playing Cards** by ryan.dansie – https://opengameart.org/content/playing-cards-0
- **Space Ambient** by DELOSound – https://pixabay.com/music/ambient-space-ambient-351305/
- **Classic Game Action Negative 19** by floraphonic – https://pixabay.com/sound-effects/classic-game-action-negative-19-224578/
- **Card Flip** by f4ngy – https://freesound.org/people/f4ngy/sounds/240776/
- **collect.wav** – by Wagna - https://freesound.org/people/Wagna/sounds/325805/
- **Background themes** – AI-generated exclusively for this project (no license required)   
