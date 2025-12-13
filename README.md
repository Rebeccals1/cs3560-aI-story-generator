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
- **MVC Architecture**  
  Clear separation between `model/` (story state and domain logic), `view/` (Swing panels and UI components), and `controller/` (application flow and user interaction handling).

- **Singleton**  
  `OpenAIClient` provides a single shared HTTP client and configuration loader with retry and timeout handling.

- **Builder Pattern**  
  `PromptBuilder` constructs compact, token-safe AI prompts from character, world, genre, and story state data while enforcing strict JSON output.

- **Strategy Pattern**
  - `StoryModeStrategy` defines interchangeable storytelling rule sets.
  - `AdultMode` and `ChildFriendlyMode` implement different tone, vocabulary, and content constraints.
  - Strategies are selected at runtime based on user controls without modifying controller logic.

- **Factory Pattern**
  - `CharacterFactory` centralizes construction of `CharacterModel` objects, ensuring default traits and backstory values are consistently applied.
  - `WorldFactory` centralizes construction of `WorldModel` objects, enforcing default rules and history while keeping domain models simple.
  - Factories remove conditional logic from `MainController` and improve testability.

- **Observer-Style UI Updates**
  - Asynchronous tasks notify the controller upon completion.
  - The controller updates views dynamically (story text, choices, loading state) without tight coupling.

## Main Architecture
```
src/main/java/
├── controller/
│ └── MainController.java
│ # MVC Controller: handles UI events, async API calls, and story flow
│
├── model/
│ ├── story/
│ │ ├── CharacterModel.java
│ │ │ # Encapsulated character data (OOP Encapsulation)
│ │ │
│ │ ├── ChoiceModel.java
│ │ │ # Immutable choice value object
│ │ │
│ │ ├── ChoiceRecordModel.java
│ │ │ # Records user decisions for persistence and continuity
│ │ │
│ │ ├── SavedStoryModel.java
│ │ │ # DTO for save/load system (separates persistence from domain)
│ │ │
│ │ ├── SceneModel.java
│ │ │ # Represents a chapter with choices (used polymorphically)
│ │ │
│ │ ├── StoryModel.java
│ │ │ # Core domain aggregate managing character, world, scenes, and state
│ │ │
│ │ ├── StoryStateModel.java
│ │ │ # Tracks chapter progression and completion rules
│ │ │
│ │ └── WorldModel.java
│ │ # Encapsulated world-building data (location, rules, history)
│ │
│ ├── strategy/
│ │ ├── StoryModeStrategy.java
│ │ │ # Strategy interface defining pluggable storytelling rules
│ │ │
│ │ ├── AdultMode.java
│ │ │ # Concrete strategy for mature tone and complex narratives
│ │ │
│ │ └── ChildFriendlyMode.java
│ │ # Concrete strategy for simplified language and safe content
│ │
│ └── OpenAIClient.java
│ # Singleton API client handling config loading, retries, and HTTP calls
│
├── service/
│ ├── OpenAIService.java
│ │ # AI-backed story generator mapping prompts to SceneModel
│ │
│ ├── CharacterFactory.java
│ │ # Factory for safe, default-enforced CharacterModel creation
│ │
│ ├── WorldFactory.java
│ │ # Factory for safe, default-enforced WorldModel creation
│ │
│ ├── PromptBuilder.java
│ │ # Builder that constructs compact, token-safe AI prompts
│ │
│ ├── StoryLibrary.java
│ │ # Service for loading saved stories into the library view
│ │
│ └── StorySaveSystem.java
│ # Repository-style service for JSON save/load
│
├── view/
│ ├── components/
│ │ ├── ErrorDialog.java
│ │ │ # Reusable UI component for consistent error popups
│ │ │
│ │ └── LoadingIndicator.java
│ │ # Visual feedback for async operations
│ │
│ ├── panels/
│ │ ├── CharacterPanel.java
│ │ │ # UI panel for character creation (MVC View)
│ │ │
│ │ ├── ChoicePanel.java
│ │ │ # Displays A/B/C choices and reacts to scene updates
│ │ │
│ │ ├── ControlsPanel.java
│ │ │ # UI for length, complexity, and strategy selection
│ │ │
│ │ ├── GenrePanel.java
│ │ │ # UI for genre selection influencing prompt construction
│ │ │
│ │ ├── LibraryPanel.java
│ │ │ # Displays saved stories
│ │ │
│ │ ├── StoryPanel.java
│ │ │ # Displays story text and embeds choice controls
│ │ │
│ │ └── WorldPanel.java
│ │ # UI panel for world-building input
│ │
│ └── MainFrame.java
│ # Top-level JFrame managing screen switching (MVC View)
│
└── Main.java
# Application entry point bootstrapping MVC on the Swing EDT
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


