# Project Report

## Challenges We Faced

**Challenge 1: AI Response Parsing Inconsistency**
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

## Design Pattern Justifications

**MVC Pattern:** Essential for separating concerns in a GUI application. MainController handles business logic and coordinates between StoryModel (data) and MainFrame/panels (view). This separation makes the code maintainable and testable.

**Singleton Pattern:** OpenAIClient implements Singleton because we need exactly one HTTP client instance to manage API connections efficiently. Multiple instances would waste resources and complicate API key management.

**Builder Pattern:** PromptBuilder constructs complex AI prompts by combining character traits, world details, genre preferences, and story context. This pattern keeps prompt creation logic centralized and makes it easy to modify prompt templates.

**Strategy Pattern:** Different story generation approaches based on genre (Fantasy, Sci-Fi, Mystery, etc.) and style preferences. Although not fully implemented as separate strategy classes, the concept is present in how PromptBuilder adapts prompts based on genre selection.

**Observer Pattern:** UI components automatically update when story state changes. The controller notifies views when new scenes are generated, implementing loose coupling between model and view layers.

## AI Usage (BE HONEST!)
Used GitHub Copilot to handle errors and make project file structure.

**Verification:** All AI-generated suggestions were tested, modified to fit project needs, and validated through manual testing. No code was used without understanding its functionality.

## Key Accomplishments
- ✅ Fully functional interactive story generator with OpenAI integration
- ✅ Robust error handling with retry logic and graceful fallbacks
- ✅ Complete save/load system with story library management
- ✅ Clean MVC architecture following project specifications
- ✅ UI with loading indicators and error dialogs
- ✅ Comprehensive story customization (genre, length, style, complexity)
