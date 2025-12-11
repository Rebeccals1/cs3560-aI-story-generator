# AI Story Generator — Choose Your Own Adventure

## Setup
1. Get API key from [OpenAI](https://platform.openai.com/api-keys)
2. Set environment variable: `export OPENAI_API_KEY="your-key-here"`
3. Or add to `src/main/resources/config.properties`: `OPENAI_API_KEY=your-key-here`
4. Run `Main.java` to launch the application

## Features
- [x] Interactive character creation with traits and backstory
- [x] Dynamic world building with custom locations and rules
- [x] Multiple genre support (Fantasy, Sci-Fi, Mystery, Romance, Horror)
- [x] AI-powered story generation with branching choices
- [x] Save/load story sessions to personal library
- [x] Robust error handling and AI response parsing
- [x] Story export functionality
- [x] Customizable story length, style, and complexity

## Design Patterns
- **MVC**: Separates UI (Swing panels), controller logic, and data models
- **Singleton**: OpenAI client instance shared across all API calls
- **Builder**: PromptBuilder constructs AI prompts from story context
- **Strategy**: Different story generation approaches based on genre/style
- **Observer**: UI components update automatically when story state changes

## Architecture
```
src/main/java/
├── Main.java                          # Application entry point
├── controller/
│   └── MainController.java           # Main controller connecting UI to services
├── model/
│   ├── OpenAIClient.java              # HTTP client singleton for OpenAI API
│   └── story/
│       ├── Character.java             # Player character with traits
│       ├── World.java                 # Story world with rules and history  
│       ├── Scene.java                 # Individual story scenes
│       ├── Choice.java                # Player choice options (A/B)
│       ├── StoryModel.java            # Complete story state
│       ├── StoryState.java            # Current position in story
│       └── SavedStory.java            # Serializable story for persistence
├── service/
│   ├── OpenAIService.java             # Story generation with AI
│   ├── PromptBuilder.java             # Constructs prompts for AI
│   └── StoryLibrary.java              # Save/load story persistence
└── view/
    ├── MainFrame.java                 # Main application window
    ├── components/
    │   ├── ErrorDialog.java           # Error message display
    │   └── LoadingIndicator.java      # Loading spinner
    └── panels/
        ├── CharacterPanel.java        # Character creation UI
        ├── ChoicePanel.java           # Choice selection buttons
        ├── ControlsPanel.java         # Story settings and controls
        ├── GenrePanel.java            # Genre selection
        ├── LibraryPanel.java          # Saved stories browser
        ├── StoryPanel.java            # Story text display
        └── WorldPanel.java            # World building UI
```

## Demo
[[Video demonstration](https://www.youtube.com/watch?v=u6xPhQpRZ9Y)]


