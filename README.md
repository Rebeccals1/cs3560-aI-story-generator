# AI Story Generator — Choose Your Own Adventure

## Setup
1. Get API key from [OpenAI](https://platform.openai.com/api-keys)
2. Set environment variable: `export OPENAI_API_KEY="your-key-here"`
3. Or add to `src/main/resources/config.properties`: `OPENAI_API_KEY=your-key-here`
4. Run `Main.java` to launch the application

## Features
- [x] Interactive character creation (name, traits, backstory)
- [x] Dynamic world building (location, rules, history)
- [x] Genre-adaptive storytelling (Fantasy, Sci-Fi, Mystery, Romance, Horror) with explicit genre constraints enforced in prompt construction
- [x] AI-powered chapter generation with branching A/B/C choices
- [x] 5-chapter structured narrative with a clearly defined final ending (token-safe for free/low-credit accounts)
- [x] Save/load system using JSON files stored in `/saves`
- [x] Consistent Swing UI with modular panels, vertical choice buttons, and loading overlay
- [x] Robust error handling with graceful AI-disabled messaging and strict JSON validation
- [x] Configurable story length, complexity, and writing style
- [x] Async AI calls using `SwingWorker` to ensure a non-blocking UI
- [x] Centralized default handling for character and world creation via factories (prevents null state bugs)

## Design Patterns
**MVC Architecture**
  The project follows a clear Model–View–Controller (MVC) structure to separate concerns and improve maintainability.

- **Model:** Domain classes such as `StoryModel`, `StoryStateModel`, `SceneModel`, `CharacterModel`, and `WorldModel` represent the story state and business rules.
These classes are UI-agnostic and focused solely on data and behavior.

- **View:** Swing UI components (`MainFrame`, `StoryPanel`, `ChoicePanel`, `CharacterPanel`, etc.) are responsible only for rendering information and collecting user input.
Views do not contain application logic.

- **Controller:** `MainController` coordinates user actions, story progression, asynchronous AI calls, and view updates.
This keeps application flow centralized and testable.

This separation allows each layer to evolve independently and enables controller logic to be tested without launching the Swing UI.

**Singleton**
  - `OpenAIClient` provides a single shared HTTP client and configuration loader with retry and timeout handling.

**Builder Pattern**
  - `PromptBuilder` constructs compact, token-safe AI prompts from character, world, genre, and story state data while enforcing strict JSON output.

**Strategy Pattern**
  - `StoryModeStrategy` defines interchangeable storytelling rule sets.
  - `AdultMode` and `ChildFriendlyMode` implement different tone, vocabulary, and content constraints.

Strategies are selected at runtime based on user controls without modifying controller logic.

**Factory Pattern**
  - `CharacterFactory` centralizes the creation of `CharacterModel` objects, ensuring default trait lists and backstory values are consistently applied.
  - `WorldFactory` centralizes the creation of `WorldModel objects`, enforcing default rules and history while keeping domain models simple and free of construction logic.

These factories remove conditional logic from `MainController`, reduce null-related bugs, and improve testability by isolating object creation concerns.

**Observer-Style UI Updates**
  - Asynchronous tasks notify the controller upon completion.
  - The controller updates views dynamically (story text, choices, loading state) without tight coupling.

## Main Architecture
```
src/main/java/
├── controller/
│   └── MainController.java
│       # MVC controller: UI events, async AI calls, story flow
│
├── model/
│   ├── story/
│   │   ├── CharacterModel.java        # Character data
│   │   ├── ChoiceModel.java           # Immutable choice value object
│   │   ├── ChoiceRecordModel.java     # Persisted user decisions
│   │   ├── SceneModel.java            # Chapter content + choices
│   │   ├── StoryModel.java            # Core domain aggregate
│   │   ├── StoryStateModel.java       # Chapter progression rules
│   │   ├── SavedStoryModel.java       # Save/load DTO
│   │   └── WorldModel.java            # World-building data
│   │
│   ├── strategy/
│   │   ├── StoryModeStrategy.java     # Pluggable storytelling rules
│   │   ├── AdultMode.java             # Mature narrative strategy
│   │   └── ChildFriendlyMode.java     # Safe narrative strategy
│   │
│   └── OpenAIClient.java
│       # Singleton API client (config, retries, HTTP)
│
├── service/
│   ├── OpenAIService.java             # AI-backed scene generation
│   ├── PromptBuilder.java             # Token-safe prompt construction
│   ├── CharacterFactory.java          # Default-safe character creation
│   ├── WorldFactory.java              # Default-safe world creation
│   ├── StoryLibrary.java              # Saved story library loader
│   └── StorySaveSystem.java           # JSON save/load repository
│
├── view/
│   ├── components/
│   │   ├── ErrorDialog.java            # Consistent error popups
│   │   └── LoadingIndicator.java       # Async loading feedback
│   │
│   ├── panels/
│   │   ├── GenrePanel.java             # Genre selection
│   │   ├── CharacterPanel.java         # Character creation
│   │   ├── WorldPanel.java             # World-building input
│   │   ├── ControlsPanel.java          # Length / mode / style
│   │   ├── StoryPanel.java             # Story display
│   │   ├── ChoicePanel.java            # A/B/C choices
│   │   └── LibraryPanel.java           # Saved stories view
│   │
│   └── MainFrame.java
│       # Top-level JFrame (screen switching)
│
└── Main.java
    # Application entry point (Swing EDT bootstrap)
```
## JUnit Testing

The JUnit testing structure mirrors the main project layout and validates the most important parts of the application.

- Model tests verify default enforcement, immutability assumptions, and state transitions.
- Controller tests use a lightweight `FakeMainFrame` to validate story flow, user choices, and save/load restoration without launching Swing.
- Service tests ensure JSON serialization, persistence, and file handling behave correctly.

## JUnit Testing Architecture

```
src/test/java/
├── controller/
│   ├── FakeMainFrame.java          # UI test double (no real Swing UI)
│   └── MainControllerTest.java     # Tests controller logic, choice flow, loading, etc.
│
├── model/
│   ├── story/
│   │   ├── CharacterModelTest.java
│   │   ├── ChoiceRecordModelTest.java
│   │   ├── SavedStoryModelTest.java
│   │   ├── SceneModelTest.java
│   │   ├── StoryModelTest.java
│   │   ├── StoryStateModelTest.java
│   │   └── WorldModelTest.java
│   │
│   └── OpenAIClientTest.java        # Tests JSON escaping, retry logic, and config validation
│
└── service/
    └── StorySaveSystemTest.java     # Tests save/load serialization and file handling

```

## Demo
[[Video demonstration](https://www.youtube.com/watch?v=u6xPhQpRZ9Y)]


