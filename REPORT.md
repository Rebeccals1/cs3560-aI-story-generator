# Project Report

## Challenges We Faced

**Challenge 1:AI Response Parsing Inconsistency**

Problem: OpenAI API sometimes returned responses in different formats - sometimes with structured SCENE/CHOICE_A/CHOICE_B format, sometimes with numbered lists, sometimes unstructured text

Solution: Implemented multiple parsing strategies with fallback mechanisms in OpenAIService.extractSections() method
Learned: Always handle AI API responses defensively with multiple parsing approaches and meaningful fallbacks

**Challenge 2: Ensuring Exactly Two Choices**

Problem: AI would sometimes generate only one choice or no clear choices, breaking the interactive story flow

Solution: Added robust validation in parseSceneAndChoices() with context-aware fallback choice generation based on scene content
Learned: AI outputs need validation and business rule enforcement - can't rely on AI to always follow format instructions

**Challenge 3: Story Persistence Without External Libraries**

Problem: Needed to save/load complex story objects without using JSON libraries to keep project lightweight

Solution: Implemented custom text-based serialization in StoryLibrary with structured format parsing
Learned: Simple text formats can be more maintainable than complex serialization, especially for human-readable data

**Challenge 4: Fixing Invalid JSON Errors From the AI**

Problem: The OpenAI API frequently returned malformed JSON when prompt formatting was even slightly off. There were a few crashes because quotes weren’t escaped, newlines broke JSON structure or the model included commentary instead of pure JSON.

Solution: Wrote a strict toJsonString() escape method and added genre-specific JSON instructions and validated the response format inside OpenAIService.

This dramatically improved story generation stability.

**Challenge 5: Replacing the Save/Load System by storing data in JSON**

Problem: JSON serialization with Jackson turned out to be more complicated than expected, especially because:
- some models required no-arg constructors,
- type mismatches caused deserialization errors,
- restoring scene history required careful ordering and the chapter number had to reflect the number of saved scenes.

Solution: This was resolved by restructuring SavedStoryModel, rewriting StoryModel.setChoiceHistory(), and adding helper methods like restoreCurrentSceneAfterLoad().

**Challenge 6: Testing Non-Deterministic Behavior**

Problem: Testing was challenging because the API produces unpredictable results.
We couldn’t call the real OpenAI service in unit tests

Solution: We had to use fake API clients, override internal methods like sendHttp(). We also simulated retries and exceptions and test prompt generation without hitting the network.

This led to a more professional testing structure and increased confidence in our core logic.


## Design Pattern Justifications

**MVC Pattern:**
We structured our application using the MVC architecture because it allowed us to separate concerns clearly and it allowed for better maintainability. 

- **The Model layer** (StoryModel, StoryStateModel, SceneModel, CharacterModel, WorldModel) holds all story-related data and state. 
- **The View layer** includes our Swing panels (GenrePanel, CharacterPanel, WorldPanel, StoryPanel, ChoicePanel), which handle user interaction and display. 
- **The Controller layer** (MainController) orchestrates the entire flow by interpreting user input, updating models, triggering the OpenAI API calls, and refreshing the UI. 

Using MVC made our codebase easier to manage, debug, and extend, and ensured that UI and logic remained cleanly separated.

**Singleton Pattern:** 

We used the Singleton pattern for OpenAIClient because we only needed one fully configured HTTP client throughout the application. This client manages timeouts, retries, authentication, and JSON escaping for all API calls. 
By ensuring a single instance, we avoided inconsistencies in request handling and prevented unnecessary resource usage. 

The singleton also simplified debugging and testing, as it centralized all API communication in one place. 
We added a reset method strictly for test environments so that our JUnit tests could run without stale state.

**Builder Pattern:** 

We applied the Builder pattern in PromptBuilder due to the complexity of constructing AI prompts. 
A full prompt includes character information, world details, genre-specific rules, user-selected story settings (length, complexity, style), previous scene summaries, and full choice history. 

Instead of assembling this in the controller—which would have made the code messy and error-prone—we encapsulated the entire process inside a dedicated builder. 
This allowed us to maintain consistent JSON formatting, easily adjust prompt logic, and add new genres or rules without modifying core application flow.

**Strategy Pattern:**

To support different storytelling modes, we introduced a StoryModeStrategy interface with two implementations: AdultMode and ChildFriendlyMode. The Strategy pattern lets us swap writing rules dynamically based on user preferences such as complexity level. 

Each strategy injects tone-appropriate adjustments into the prompt—simplified language for child-friendly mode, richer detail or mature themes for adult mode. 
By structuring tone control this way, our system becomes more flexible and extensible, allowing us to add new modes (e.g., “Poetic Mode,” “Comedic Mode,” “Dark Mode”) without altering the rest of the architecture.

**Observer Pattern:** 

UI components automatically update when story state changes. The controller notifies views when new scenes are generated, implementing loose coupling between model and view layers.

## OOP Four Pillars (Where & Why)
### Encapsulation
Throughout our project, we strongly relied on encapsulation to protect internal state and maintain clear boundaries between components. 
All model classes—such as StoryModel, StoryStateModel, CharacterModel, WorldModel and SceneModel—use private fields with public getters and carefully controlled setters. 

This prevents outside code from modifying the story state incorrectly or bypassing validation. For example, StoryStateModel manages chapter progression and choice history internally, ensuring no external class can jump chapters or corrupt the record list.
We also encapsulated API logic within OpenAIClient and OpenAIService, ensuring that raw HTTP details and JSON construction never leak into the controller or UI. This made the system more robust, testable, and maintainable.

### Inheritance
We incorporated inheritance primarily within our Strategy subsystem. 
We created an abstract interface StoryModeStrategy and our two concrete strategies—AdultMode and ChildFriendlyMode—extend this abstraction by providing different implementations of applyToneRules(). 

Even though we used interfaces rather than abstract superclasses, this structure still leverages inheritance by defining a shared contract and allowing child classes to specialize behavior.
This approach allowed us to add new storytelling modes without altering existing code, demonstrating clean extensibility and proper use of an inheritance hierarchy in a real-world context.

### Polymorphism
Polymorphism appears across our codebase wherever we use the Strategy pattern.
When the controller calls:
- storyModeStrategy.applyToneRules(promptSections, storyModel);

it does not need to know whether the active strategy is AdultMode, ChildFriendlyMode, or any future mode we might add. The correct method execution is chosen at runtime, giving us flexible, interchangeable storytelling behaviors.

This runtime polymorphism simplifies the controller logic and prevents large conditional blocks or duplicated code. 
It also promotes clean software evolution—new modes require only a new class implementing the strategy interface.

### Abstraction
We applied abstraction to hide complexity behind clear, stable interfaces. The StoryModeStrategy interface abstracts away the details of how tonal rules are generated or applied. The controller simply delegates tone modification to whichever strategy is active. Similarly, OpenAIService abstracts the process of contacting the API, parsing responses, and converting raw JSON into SceneModel objects. 

This prevents the controller or UI from dealing with lower-level logic. Abstraction also appears in the PromptBuilder, which hides the complexity of constructing long prompt structures and formatting user inputs, world details, previous choices, genre logic, and mode-specific adjustments. 
By isolating these responsibilities, we ensured that the rest of the application interacts only with clean, simplified interfaces.

## AI Usage (BE HONEST!)
Used GitHub Copilot to handle errors and make project file structure.

AI (ChatGPT) was used to:
- Help refactor OpenAI error handling and retry logic
- Assist with writing clear prompt-generation logic
- Provide examples for UML diagrams
- Improve code readability and documentation
- Suggest improvements to code structure and applying design patterns
- JUnit test scaffolding
- Assist in producing the final written REPORT.md and README.md

**Verification:** All AI-generated suggestions were tested, modified to fit project needs, and validated through manual testing. All code was manually written, reviewed, debugged, and integrated into the final codebase by the developer.
No code was used without understanding its functionality.

## Key Accomplishments
- ✅ Fully functional interactive story generator with OpenAI integration
- ✅ Robust error handling with retry logic and graceful fallbacks
- ✅ Complete save/load system with story library management
- ✅ Clean MVC architecture following project specifications
- ✅ UI with loading indicators and error dialogs
- ✅ Comprehensive story customization (genre, length, style, complexity)
