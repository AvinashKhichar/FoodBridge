
# 🍱 FoodBridge

FoodBridge is a food donation Android application developed in **Kotlin**. It enables users to list and discover surplus food donations in real-time using map-based interactions. The app connects donors and receivers to reduce food waste and help communities.

## ✨ Features

- 📍 **Map Integration with OsmDroid**  
  Displays real-time nearby donation pickup points on an interactive OpenStreetMap view.

- 🧭 **MVVM Architecture**  
  Clean and maintainable codebase using the Model-View-ViewModel pattern with Fragment-based UI.

- 🔐 **Firebase Authentication**  
  Secure login and registration for users to safely manage their donation activities.

- 🔄 **Firebase Realtime Database**  
  Real-time sync of food donation listings, ensuring live updates across all devices.

- ⚙️ **Hilt Dependency Injection**  
  Lifecycle-aware and efficient dependency injection with minimal boilerplate.

## 🛠️ Tech Stack

| Tech             | Description                                  |
|------------------|----------------------------------------------|
| Kotlin           | Primary development language                 |
| XML              | UI layout definitions                        |
| MVVM             | Architecture for scalable codebase           |
| Firebase         | Authentication & Realtime Database backend   |
| Hilt             | Dependency injection                         |
| OsmDroid         | OpenStreetMap-based map display              |

## 🚀 Getting Started

### Prerequisites

- Android Studio LadyBug or later
- Firebase project setup with Authentication and Realtime Database enabled

### Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/FoodBridge.git
   ```

2. Open the project in Android Studio.

3. Configure Firebase:
   - Add your `google-services.json` to the `app/` directory.
   - Ensure Firebase Auth and Realtime Database are enabled.

4. Build and run the project on an emulator or device.


## 🙌 Contributing

Feel free to fork this repo, raise issues, or contribute improvements via PRs.

## 📃 License

MIT License - see [LICENSE](LICENSE) file for details.
