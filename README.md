# AI Story Generator — Choose Your Own Adventure

## Setup
1. Get API key from [OpenAI](https://platform.openai.com/api-keys)
2. Set environment variable: `export OPENAI_API_KEY="your-key-here"`
3. Or add to `src/main/resources/config.properties`: `OPENAI_API_KEY=your-key-here`
4. Run `Main.java` to launch the application

## Features
- [x] Interactive character creation (name, traits, backstory)
- [x] Dynamic world building (location, rules, history)
- [x] Genre-adaptive storytelling (Fantasy, Sci-Fi, Mystery, Romance, Horror)
- [x] AI-powered chapter generation with branching A/B/C choices
- [x] 10-chapter structured narrative with final ending
- [x] Save/load system using JSON files stored in /saves
- [x] Consistent UI with Swing panels, vertical choice buttons, loading overlay
- [x] Robust error handling and JSON validation
- [x] Configurable story length, complexity, and style
- [x] Async AI calls using SwingWorker (non-blocking UI)

## Design Patterns
- **MVC architecture**: Clear separation between UI panels, controller logic, and model data.
- **Singleton**: OpenAIClient provides a single shared HTTP client and config loader.
- **Builder Pattern**: PromptBuilder constructs complex AI prompts from characters, worlds, genres, and choice history.
- **Strategy Pattern**: 
  - **StoryModeStrategy** interface defines a pluggable set of storytelling rules. 
  - **AdultMode** and **ChildFriendlyMode** implement these rules with different tones, vocabulary levels and content guidelines. 
  - Together, they allow the story generator to dynamically adjust writing style and narrative complexity based on the user’s selected mode without changing any core logic. 
- **Observer-Style UI Updates**: The controller pushes new scenes to the view, which updates dynamically.

## Main Architecture
```
src/main/java/
├── controller/
│   └── MainController.java                  # Core application logic, scene flow, async API calls
│
├── model/
│   ├── story/                               # Domain models (pure data + logic)
│   │   ├── CharacterModel.java
│   │   ├── ChoiceModel.java
│   │   ├── ChoiceRecordModel.java
│   │   ├── SavedStoryModel.java
│   │   ├── SceneModel.java
│   │   ├── StoryModel.java
│   │   ├── StoryStateModel.java
│   │   └── WorldModel.java
│   │
│   ├── strategy/                            # STRATEGY PATTERN
│   │   ├── StoryModeStrategy.java           # Strategy Interface
│   │   ├── AdultMode.java                   # Adult story rules
│   │   └── ChildFriendlyMode.java           # Child-friendly story rules
│   │
│   └── OpenAIClient.java                    # Singleton HTTP client with retry/backoff
│
├── service/
│   ├── OpenAIService.java                   # Maps prompt ↔ response, calls OpenAIClient
│   ├── PromptBuilder.java                   # Builds full prompt (uses Strategy)
│   ├── StoryLibrary.java                    # Loads saved stories for library view
│   └── StorySaveSystem.java                 # Saves/loads JSON story files
│
├── view/
│   ├── components/
│   │   ├── ErrorDialog.java                 # Error pop-up
│   │   └── LoadingIndicator.java            # Loading spinner overlay
│   │
│   ├── panels/
│   │   ├── CharacterPanel.java              # Character creation UI
│   │   ├── ChoicePanel.java                 # A/B/C choice buttons
│   │   ├── ControlsPanel.java               # Length/complexity/style options
│   │   ├── GenrePanel.java                  # Genre selection
│   │   ├── LibraryPanel.java                # Saved story list
│   │   ├── StoryPanel.java                  # Displays story text + buttons
│   │   └── WorldPanel.java                  # World-building input
│   │
│   └── MainFrame.java                       # Top-level JFrame, screen switching
│
└── Main.java                                # Application entry point

```
## JUnit Testing

The JUnit testing structure mirrors the main project layout and validates the most important parts of the application.
- Model tests check that characters, scenes, story state, and saved files behave correctly.
- Controller tests use a simple FakeMainFrame to confirm that the story flow and user choices work as intended without opening the Swing interface.
- Service tests verify that story files save and load correctly.

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


