# 🌙 AI Story Generator — Choose Your Own Adventure  
**CS3560 – Assignment 3 (Option B)**  
Team Members:  
- Rebecca Smith  
- Viet Nguyen  
- Tanya  

## 📌 Overview  
The **AI Story Generator** is an interactive *Choose Your Own Adventure* storytelling application.  
Players create a **character**, define a **world**, choose a **genre**, and then experience a branching narrative where **every choice shapes the story**. Users can customize story length, complexity, and writing style. Finished stories can be **saved, tagged, favorited, and exported**.

This project is designed using:
- **MVC Architecture**
- **Strategy Pattern** (for genre behavior & story flow)
- **Factory Pattern** (for generating correct genre strategy)
- **Singleton Pattern** (for shared AI client instance)

Optional enhancements include:
- AI Image Generation (ex: DALL·E)
- Text-to-Speech narration

---

## 🎮 Example Program Flow  
### 1. Select a Genre  
Fantasy | Sci-Fi | Mystery | Romance | Horror

User selects **Fantasy** → clicks **Start**

### 2. Create a Character  
| Field | Example Input |
|-------|---------------|
| Name | Elowen |
| Trait | Brave |
| Backstory | Exiled princess of the Moon Kingdom |

### 3. Build the World  
| Field | Example Input |
|-------|---------------|
| Location | Whispering Forest |
| Rules | Magic is forbidden by the High Council |
| History | Ancient war between light and shadow |

### 4. Customize Story Style  
- Length: **Medium**
- Complexity: **Child-Friendly**
- Style: **Descriptive**

### 5. Story Appears  
Scene 1: Elowen steps into the Whispering Forest...
A) Follow a glowing trail
B) Call out into the trees


Player chooses → story branches → continues with new scenes.

---

## 👥 Team Roles  
| Team Member | Role | Responsibilities |
|------------|------|----------------|
| **Person A** | Story & Model Engineer | Story data structures, character/world models, genre strategies |
| **Person B** | UI/UX Engineer | All visual panels, layout flow, displaying story & choices |
| **Person C** | Controller & AI Engineer | Game logic, OpenAI integration, saving & exporting stories |

**Assigned Roles:**  
- Person A = *TBD*  
- Person B = *TBD*  
- Person C = *TBD*  

---

## 🧱 Proposed File Structure (WIP)
```
src/
├── Main.java
│
├── model/ // Person A
│ ├── story/
│ │ ├── Scene.java
│ │ ├── Choice.java
│ │ ├── StoryState.java
│ │ ├── StoryModel.java
│ │ ├── Character.java
│ │ └── World.java
│ │
│ ├── strategy/
│ │ ├── AIStrategy.java
│ │ ├── FantasyStrategy.java
│ │ ├── SciFiStrategy.java
│ │ ├── MysteryStrategy.java
│ │ ├── RomanceStrategy.java
│ │ ├── HorrorStrategy.java
│ │ └── StrategyFactory.java
│ │
│ └── library/
│ ├── StoryRecord.java
│ └── LibraryModel.java
│
├── controller/ // Person C
│ ├── IGameController.java
│ ├── GameController.java
│ ├── CharacterController.java
│ ├── WorldController.java
│ └── LibraryController.java
│
├── view/ // Person B
│ ├── MainFrame.java
│ ├── GenrePanel.java
│ ├── CharacterPanel.java
│ ├── WorldPanel.java
│ ├── ControlsPanel.java
│ ├── StoryPanel.java
│ ├── ChoicePanel.java
│ ├── LibraryPanel.java
│ └── components/
│ ├── LoadingIndicator.java
│ └── ErrorDialog.java
│
└── service/ // Person C
├── OpenAIClient.java // Singleton
├── OpenAIService.java
├── PromptBuilder.java
├── LibraryStorage.java
├── ExportService.java
├── ImageGenService.java // OPTIONAL
└── TTSService.java // OPTIONAL
```


---

## ⚙️ Core Design Patterns Used

| Pattern | Purpose in This Project |
|--------|-------------------------|
| **MVC** | Separates UI, logic, and data for clean development flow |
| **Strategy** | Each genre generates text differently without changing main game logic |
| **Factory** | Returns correct genre strategy based on player choice |
| **Singleton** | Ensures one shared OpenAI client instance |

---

## 📂 Story Saving & Export Features  
Users can:
- Save stories locally (JSON)
- Tag and favorite stored stories
- Export final stories as **.txt** or **.md**
- *(Optional)* Generate matching images
- *(Optional)* Listen to narration audio

---

## 🚧 Status  
📍 **In Development**  
UML and final API integration still in progress.

---

## 🔗 Repository Link  
**GitHub:** _TBD_

---

## 💡 Future Enhancements  
- Multi-choice branching (more than A/B)
- Visual story map tree
- Multiplayer co-op storytelling mode
- Style Mode: “Poetic”, “Comic”, “Dark Realism”

---

