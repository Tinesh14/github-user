# 📱 Github User

Android assessment project implementing **Clean Architecture + MVVM** using Jetpack Compose, Koin, Retrofit, Room, and Coroutines.  
The app allows searching GitHub users, viewing their details, and marking them as **Favorites** (cached locally).

---

## 🚀 Features
- 🔍 Search GitHub users in real time
- 👤 View user detail
- ⭐ Mark/unmark users as Favorite
- 💾 Offline cache with Room
- 🖼 Load avatars with Coil
- 🧩 Modularized: `domain`, `data`, `app`
- 📡 Networking with Retrofit + Moshi + OkHttp
- 🔐 GitHub Personal Access Token (PAT) support
- 🛠 Debugging with Chucker
- ✅ Unit, DAO, and UI tests

---

## 🏗 Architecture
This project follows **Clean Architecture + MVVM**:

### Layers
- **Domain (`:domain`)**
  - Pure Kotlin, no Android dependencies
  - Business rules, models, repository contracts
- **Data (`:data`)**
  - Repository implementations
  - Remote: Retrofit API
  - Local: Room Database
  - Mappers for data transformation
- **Presentation (`:app`)**
  - Jetpack Compose UI
  - ViewModel with state flows
  - Dependency Injection setup (Koin)

### Dependency Flow
```
App (Compose UI + ViewModel) 
   ↓
Domain (Contracts, Models) 
   ↓
Data (Repository, Retrofit, Room)
```

---

## 🛠 Tech Stack
- **Language:** Kotlin (2.0)
- **UI:** Jetpack Compose, Material3
- **DI:** Koin
- **Networking:** Retrofit, Moshi, OkHttp, Chucker
- **Image Loading:** Coil
- **Persistence:** Room Database
- **Async:** Kotlin Coroutines + Flow
- **Testing:** JUnit, Espresso, Compose UI Test

---

## 📦 Module Structure
```
:app      → Presentation (Compose UI, ViewModel, DI startup)
:data     → Data sources (Retrofit, Room, RepositoryImpl)
:domain   → Business logic (Models, Repository interfaces)
```

---

## 🔑 Setup

1. **Clone repo**
   ```bash
   git clone https://github.com/<your-username>/github-user-search.git
   ```

2. **Create `local.properties`** in project root and add your GitHub PAT:
   ```
   GITHUB_TOKEN=ghp_xxx...
   ```
   > This prevents hitting GitHub’s 60-requests/hour unauthenticated limit.

3. **Build & run**
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📱 Screens

### 🏠 Home Screen
- Search bar at top  
- If query entered → show filtered users from API + DB  
- If no query → show list of all users
  
### 👤 Detail Screen
- Shows selected user’s details:
  - Avatar  
  - Username  
  - Bio  

### ⭐ Favorites Screen
- Displays only users marked as Favorite  
- Works offline (persisted with Room)

---

## ✅ Testing

### Unit Tests
- Run:
  ```bash
  ./gradlew test
  ```
- Covers domain and repository logic

---

## 🖼 Favorite Feature
- Users can be marked as **Favorite** with a heart icon  
- Stored locally (`isFavorite = true` in Room)  
- Works offline — favorites remain even after app restart  

---

## 📂 Project Highlights
- Modularized for scalability
- Clear separation of concerns
- Offline-first design
- Secure handling of API tokens
- Bonus feature: **Favorites**

---

## 📋 Requirements Compliance
- ✅ Clean Architecture with MVVM
- ✅ Domain, Data, Presentation layers
- ✅ Retrofit + Room integration
- ✅ Compose UI (single screen)
- ✅ Koin DI
- ✅ Unit + UI tests
- ✅ Extra feature: Favorites

---

## 👨‍💻 Author
Built with ❤️ by **Tinesh Roy**
