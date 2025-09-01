# AIDA Android Application

This Android application, written in Kotlin, serves as a control interface for the AIDA robot. It utilizes Jetpack Compose for its user interface and Hilt for dependency injection, following modern Android development best practices.

## Table of Contents

1.  [Prerequisites](#prerequisites)
2.  [Installation](#installation)
3.  [Known Bugs](#known-bugs)
4.  [System overview](#system-overview)
    *   [Layered Architecture](#layered-architecture)
    *   [Unidirectional Data Flow (UDF) & State Management](#unidirectional-data-flow-udf--state-management)
    *   [Dependency Injection with Hilt](#dependency-injection-with-hilt)
5.  [Project Structure Breakdown](#project-structure-breakdown)
    * [Root Files](#root-files)
    * [di](#di-dependancy-injection)
    * [data](#data)
    * [domain](#domain)
    * [ui](#ui)
    * [generated](#datastoregenerated-generated-code)


6.  [Robot Communication](#robot-communication)
7.  [Data Persistence](#data-persistence)

## Prerequisites

For the best developing experience, we recommend using **Android Studio**.
Install it using this guide: [Installation guide for Android Studio](https://developer.android.com/studio/install).

## Installation

1.  Clone the git repository:
    ```bash
    git clone git@gitlab.liu.se:denab905/AIDA.git
    ```
2.  Open Android Studio and select `File > Open`.
3.  Navigate to the cloned repository and select the `android` folder. If prompted, choose `Open as project`.
4.  Build the project. In Android Studio, click the `Build` menu: `Build > Make project`. Ensure the build completes without errors.
5.  Run the application on an emulator or a physical device. Connect the device via USB or start an Android Virtual Device (AVD). Click the run button (green play icon) in Android Studio. The app will install and run automatically.

## Known Bugs

Current known bugs can be found in the `docs/bugs` directory at the root of the repository.

## System Overview

Documentation can be found in: `app/docs/html/index.html`

### Layered Architecture

The application is divided into layers, with specific responsibilities:

*   **UI Layer :** Handles all user interaction and display. Built with Jetpack Compose and ViewModels to manage UI state and expose it to Composables.  
*   **Domain Layer :** Contains core business logic, use-case interfaces and data models. Fully independent of Android framework and data source details.  
*   **Data Layer :** Implements the Domain’s interfaces, abstracts data sources (network, local storage) and provides data to the ViewModels via Repositories.  


### Unidirectional Data Flow (UDF) & State Management

The application follows a UDF pattern, primarily within the UI and ViewModel layers:

1.  **State:** ViewModels constructs UI states by using flows/state-flows available in the repositories and then exposte them to be used in the UI (pages and components).
2.  **Events:** UI elements (Composables) send events (user interactions) to the ViewModels.
3.  **Logic:** ViewModels process these events, potentially interact with Repositories in the Domain/Data layers, and update their internal state.
4.  **UI Update:** The UI observes the state changes from the ViewModels and recomposes to reflect the new state.

### Dependency Injection with Hilt

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is used for dependency injection. This simplifies managing dependencies between different parts of the application, promotes loose coupling, and makes components easier to test.

*   `@HiltAndroidApp` on `AidaApplication` initializes Hilt.
*   `@AndroidEntryPoint` annotates `MainActivity` to enable field injection.
*   `@HiltViewModel` allows ViewModels to receive dependencies via constructor injection.
*   Hilt Modules (e.g., `NetworkModule`, `RepositoryModule`, `StorageModule`) define how to provide instances of interfaces or classes that cannot be constructor-injected.

## Project Structure Breakdown

The application's source code is organized into different directories under `src/main/java/com/example/aida/`.

---

### Root Files:

*   `AidaApplication.kt`: The entry point of the application, responsible for initializing Hilt and other global configurations.
*   `MainActivity.kt`: The sole Activity in the app, serving as the host for all Jetpack Compose UI screens and navigation.  

---

### di (Dependancy Injection):  

This directory Hilt modules (`NetworkModule.kt`, `RepositoryModule.kt`, `StorageModule.kt`). These modules instruct Hilt on how to provide dependencies (like network clients, repositories, and data storage instances) throughout the application.

---

### data:  

This layer is responsible for all data handling and provides concrete implementations of the repository interfaces defined in the domain layer.

*   **`local`**:  

    Contains components related to local data persistence.


*   **`remote`**:

    Manages communication with the AIDA robot.
*   `RobotSocketApi.kt`: Implements the high-level `RobotApi` interface, handling the specific communication protocol with the robot.

*   **`network`** (sub-package): Contains lower-level networking components like `SocketManagerImpl.kt` (for managing multiple socket connections) and `SocketConnection.kt` (for individual socket operations).
*   **`protocol`** (sub-package): Defines the communication protocol specifics, such as `Instructions.kt` and `MessageType.kt` enums.

*   **`repository`**:

    Contains concrete implementations of the repository interfaces (e.g., `SequenceRepositoryImpl.kt`, `AppPrefsRepositoryImpl.kt`). These classes fetch data from local or remote sources and provide it to the ViewModels.

---

### domain:

This is the core of the application, containing business logic definitions (as interfaces) and data models. It's independent of Android frameworks and UI.

*   **`model`**:
    
    Defines the fundamental data structures and enums used across the application (e.g., `RobotAction.kt`, `RobotActionType.kt`, `Settings.kt`).

*   **`remote`**:
    
    Contains interfaces and enums related to remote communication, abstracting the specifics of how the robot is contacted (e.g., `RobotApi.kt` interface, `ConnectionId.kt` enum).

*   **`repository`**:
    
    Defines interfaces for data repositories (e.g., `SequenceRepository.kt`, `AppPrefsRepository.kt`). These interfaces dictate how data should be accessed and managed, abstracting the underlying data sources.

---

### ui:    
This package encompasses all user interface elements and logic, built entirely with Jetpack Compose.

*   **`component`**:
    
    Contains reusable, smaller UI building blocks (e.g., custom buttons, display elements like `SequenceBar.kt`, `JoystickUI.kt`). `ActionsData.kt` is a key file here, defining how robot actions are represented in the UI.

*   **`constants`**:
    
    Holds UI-specific constant values like colors, dimensions, and fixed strings.

*   **`navigation`**:
    
    Manages navigation between different screens using Jetpack Navigation Compose. `NavGraph.kt` defines the navigation map, and `NavRoute.kt` provides type-safe route definitions.

*   **`page`**:
    
    Contains the top-level Composable functions that represent full screens or pages within the application (e.g., `CameraPage.kt`, `SequenceTabPage.kt`).

*   **`popups`**:
    
    Defines various dialogs and pop-up windows used for user input or displaying specific information (e.g., action configuration popups, QR scanner).

*   **`theme`**:
    
    Includes application-wide theming, color palettes, and typography settings for Jetpack Compose.

*   **`viewmodel`**:
    
    Houses the ViewModel classes. ViewModels are responsible for preparing and managing UI-related data, handling user interactions, and communicating with the `domain` and `data` layers.

---

### datastore.generated (Generated Code):  

This directory contains classes automatically generated by Protobuf from the `src/main/proto/schema.proto` file. These classes are used by Proto DataStore for persisting application data.

---

## Robot Communication

Communication with the robot is handled over **TCP/IP sockets**.
*   `SocketManagerImpl.kt` manages individual socket connections for different functionalities (defined by `ConnectionId.kt`).
*   `RobotSocketApi.kt` implements the application-level protocol:
    *   Messages are typically structured with a **header** (message type ID, payload size) and a **payload**.
    *   `MessageType.kt` defines various message types (e.g., `CAMERA`, `JOYSTICK`, `SEQUENCE`).
    *   `Instructions.kt` defines common instruction codes (e.g., `ON`, `OFF`).
*   Video and Lidar frames are transmitted as byte arrays and converted to `ImageBitmap` on the client.
*   Speech-to-Text (STT) data is received as UTF-8 strings.
*   Action sequences are serialized into a specific byte format by `RobotSocketApi.sendSequence()` before transmission.

## Data Persistence

*   Application settings (IP, port) and robot-action sequence are persisted locally using **[Proto-Datastore](https://developer.android.com/codelabs/android-proto-datastore#4)**.
*   The data-structure/schema is defined in `src/main/proto/schema.proto`.
*   `AppPreferencesSerializer.kt` handles serialization/deserialization of `AppPreferences` data.
*   `AppPrefsRepositoryImpl.kt` abstracts DataStore interactions, providing flows (`currentSettings`) and functions for retrieving and saving data (`loadSavedSequence`, `saveSequence`, `updateSettings`).
*   Action sequences are explicitly saved to DataStore when the app is stopped (in `MainActivity.onStop()`) via `SequenceViewModel.saveActions()` which calls `SequenceRepository.saveSequence()`.