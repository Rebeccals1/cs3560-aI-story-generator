# Project Report

---

## Challenges We Faced

### Challenge 1:AI Response Parsing Inconsistency

Problem: OpenAI API sometimes returned responses in different formats - sometimes with structured SCENE/CHOICE_A/CHOICE_B format, sometimes with numbered lists, sometimes unstructured text

Solution: Implemented multiple parsing strategies with fallback mechanisms in OpenAIService.extractSections() method
Learned: Always handle AI API responses defensively with multiple parsing approaches and meaningful fallbacks

### Challenge 2: Ensuring Exactly Two Choices**

Problem: AI would sometimes generate only one choice or no clear choices, breaking the interactive story flow

Solution: Added robust validation in parseSceneAndChoices() with context-aware fallback choice generation based on scene content
Learned: AI outputs need validation and business rule enforcement - can't rely on AI to always follow format instructions

### Challenge 3: Story Persistence Without External Libraries

Problem: Needed to save/load complex story objects without using JSON libraries to keep project lightweight

Solution: Implemented custom text-based serialization in StoryLibrary with structured format parsing
Learned: Simple text formats can be more maintainable than complex serialization, especially for human-readable data

### Challenge 4: Fixing Invalid JSON Errors From the AI

Problem: The OpenAI API frequently returned malformed JSON when prompt formatting was even slightly off. There were a few crashes because quotes weren’t escaped, newlines broke JSON structure or the model included commentary instead of pure JSON.

Solution: Wrote a strict toJsonString() escape method and added genre-specific JSON instructions and validated the response format inside OpenAIService.

This dramatically improved story generation stability.

### Challenge 5: Replacing the Save/Load System by storing data in JSON

Problem: JSON serialization with Jackson turned out to be more complicated than expected, especially because:
- some models required no-arg constructors,
- type mismatches caused deserialization errors,
- restoring scene history required careful ordering and the chapter number had to reflect the number of saved scenes.

Solution: This was resolved by restructuring SavedStoryModel, rewriting StoryModel.setChoiceHistory(), and adding helper methods like restoreCurrentSceneAfterLoad().

### Challenge 6: Testing Non-Deterministic Behavior

Problem: Testing was challenging because the API produces unpredictable results.
We couldn’t call the real OpenAI service in unit tests

Solution: We had to use fake API clients, override internal methods like sendHttp(). We also simulated retries and exceptions and test prompt generation without hitting the network.

This led to a more professional testing structure and increased confidence in our core logic.

### Challenge 7: Graceful Failure When AI Is Unavailable
Problem: If the API key was missing or invalid, the application initially failed at startup or during story generation.

Solution:
We introduced a **Factory-based fallback system**:
- `StoryGeneratorFactory` selects the generator at runtime  
- `OpenAIService` is used when AI is enabled  
- `OfflineStoryGenerator` provides a safe placeholder when AI is unavailable  

The UI displays a clear popup explaining the situation instead of crashing.

### Challenge 8: Implementing AI Image generation
Problem: We experimented with AI image generation but encountered frequent rate-limit and token-usage issues.

Solution: The feature was removed to preserve reliability and stay within API usage limits.

---

## Design Pattern Justifications

### MVC Architecture

We used MVC to separate concerns clearly:

- **Model:** `StoryModel`, `StoryStateModel`, `SceneModel`, `CharacterModel`, `WorldModel`
- **View:** Swing panels (`GenrePanel`, `StoryPanel`, `ChoicePanel`, etc.)
- **Controller:** `MainController` orchestrates flow, async calls, and UI updates

This separation improved maintainability, testability, and clarity.

### Singleton Pattern

`OpenAIClient` is implemented as a Singleton to ensure:
- One shared HTTP client
- Centralized API configuration
- Consistent retry and timeout behavior

A test-only reset method allows clean unit testing without stale state.

### Builder Pattern

`PromptBuilder` encapsulates the complex logic of constructing AI prompts from:
- Genre rules
- Character and world summaries
- Previous scene context
- User-selected mode and style

This prevents bloated controller logic and allows prompt evolution without architectural changes.

### Strategy Pattern

`StoryModeStrategy` defines pluggable storytelling rules.  
Concrete implementations (`AdultMode`, `ChildFriendlyMode`) inject tone and vocabulary constraints dynamically.

This allows new modes to be added without modifying existing logic.

### Factory Pattern  **(UPDATED)**

Factories are used to **centralize default handling and object construction**, keeping domain models simple and preventing null-state bugs.

- **`CharacterFactory`**
    - Responsible for creating `CharacterModel` instances
    - Ensures:
        - Traits list is never null
        - Backstory defaults are consistently applied
    - Removes validation and default logic from the controller and model

- **`WorldFactory`**
    - Responsible for creating `WorldModel` instances
    - Enforces:
        - Default rules when none are provided
        - Default historical context for the world
    - Guarantees a fully valid world state before story generation begins

Factories are invoked by `MainController` during setup, ensuring:
- Controllers remain clean and focused on flow control
- Domain models remain lightweight data holders
- Defaults are applied consistently across UI input, loading, and testing

This approach improved test reliability and eliminated multiple null-related bugs discovered during JUnit testing.

### Observer-Style Updates

The controller updates the UI when async operations complete using `SwingWorker`, creating loose coupling between logic and presentation without blocking the UI thread.

---

## OOP Four Pillars (Where & Why)

### Encapsulation

Encapsulation is used extensively throughout the project to protect internal state and enforce correctness rules.

All domain models—such as `StoryModel`, `StoryStateModel`, `SceneModel`, `CharacterModel`, and `WorldModel`—store their fields as **private** and expose behavior only through controlled public methods. This prevents external layers (UI or controller) from mutating state in unsafe or unintended ways.

Examples:
- `StoryStateModel` controls chapter progression internally and prevents skipping chapters or exceeding the maximum chapter limit.
- Choice history is stored internally and exposed as an **unmodifiable list**, ensuring external code cannot alter it directly.
- Default enforcement is handled externally via factories, keeping models free of conditional logic.

This approach:
- Prevents invalid state
- Makes debugging easier
- Improves testability
- Keeps responsibilities clearly separated

Encapsulation ensures that each class is responsible for maintaining its own invariants.

### Inheritance

Inheritance is primarily used to model **behavioral specialization**.

The `StoryModeStrategy` interface defines a shared contract for applying storytelling rules. Concrete implementations—`AdultMode` and `ChildFriendlyMode`—provide specialized behavior.

This avoids rigid class hierarchies while still enabling extensibility.

### Polymorphism

Polymorphism appears in multiple runtime decisions:

- `StoryModeStrategy` → `AdultMode` or `ChildFriendlyMode`
- The controller interacts only with abstractions: `modeStrategy.applyModeRules(builder, storyModel, length, complexity, style);`

Java determines the correct implementation at runtime without conditional logic.

### Abstraction

Abstraction hides complex subsystems behind stable interfaces:

- `StoryModeStrategy` – hides tone and vocabulary logic
- `PromptBuilder` – hides prompt construction
- `OpenAIService` – hides API calls, retries, parsing, and validation
- Factories – hide default enforcement and construction rules

The controller does not need to know:
- How defaults are applied
- How prompts are structured
- How API failures are handled
- How internal models are initialized safely

This abstraction allows the system to evolve without breaking existing code.

---

## AI Usage (BE HONEST!)
Used GitHub Copilot to handle errors and make project file structure.

AI (ChatGPT) was used to:
- Help refactor OpenAI error handling and retry logic
- Assist with writing clear prompt-generation logic
- Improve code readability and documentation
- Suggest improvements to code structure and applying design patterns
- JUnit test scaffolding
- Assist in producing the final written REPORT.md and README.md

**Verification:** All AI-generated suggestions were tested, modified to fit project needs, and validated through manual testing. All code was manually written, reviewed, debugged, and integrated into the final codebase by the developer.
No code was used without understanding its functionality.

## Key Accomplishments
- ✅ Fully functional interactive story generator with OpenAI integration
- ✅ Centralized default enforcement using factories (prevents null-state bugs)
- ✅ Robust error handling with retry logic and graceful failure modes
- ✅ Complete save/load system with story library management
- ✅ Clean MVC architecture following project specifications
- ✅ UI with loading indicators and consistent error dialogs
- ✅ Comprehensive story customization (genre, length, style, complexity)
