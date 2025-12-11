package service;

import model.story.*;

import java.util.List;

/**
 * PromptBuilder
 *
 * Builds the full natural-language prompt sent to OpenAI for each chapter.
 * Responsibilities:
 *  - Enforce 10-chapter structure (with a special final chapter).
 *  - Inject character, world, and user-selected settings (length, complexity, style).
 *  - Encode the FULL choice history so the story feels continuous.
 *  - Strongly enforce GENRE behavior (Romance, Fantasy, SciFi, Mystery, Horror, etc.).
 *  - Clearly specify the JSON contract the model must return:
 *        {
 *          "chapter": 1–10,
 *          "isEnding": true/false,
 *          "story": "...",
 *          "choices": { "A": "...", "B": "...", "C": "..." }
 *        }
 *
 * The model is instructed to return ONLY valid JSON (no markdown, no commentary).
 */
public class PromptBuilder {

    public String buildStoryPrompt(StoryModel storyModel,
                                   String lastChoiceId,
                                   String length,
                                   String complexity,
                                   String style) {

        StoryStateModel state = storyModel.getState();
        int chapter = state.getChapter();
        boolean finalChapter = (chapter >= StoryStateModel.MAX_CHAPTERS);

        CharacterModel ch = storyModel.getCharacter();
        WorldModel world = storyModel.getWorld();
        List<SceneModel> history = storyModel.getAllScenes();
        List<ChoiceRecordModel> historyChoices = storyModel.getState().getChoiceHistory();

        // Genre from model (e.g., "Romance", "Fantasy", "Horror", etc.)
        String genre = storyModel.getGenre();

        StringBuilder sb = new StringBuilder();

        sb.append("You are an AI story engine. Produce ONLY a JSON object.\n");
        sb.append("Do NOT add markdown. Do NOT add commentary. Do NOT add explanations.\n");
        sb.append("Your entire reply MUST be valid JSON.\n\n");

        sb.append("=== STORY SETTINGS ===\n");
        sb.append("Chapter: ").append(chapter).append(" of 10\n");
        sb.append("Genre: ").append(genre).append("\n");
        sb.append("Length: ").append(length).append("\n");
        sb.append("Complexity: ").append(complexity).append("\n");
        sb.append("Style: ").append(style).append("\n\n");

        // ---------------------------------------------------------
        // GENRE ENFORCEMENT BLOCK
        // ---------------------------------------------------------
        sb.append("=== GENRE REQUIREMENTS ===\n");

        if ("Romance".equalsIgnoreCase(genre)) {
            sb.append("- This IS a ROMANCE story.\n");
            sb.append("- Every chapter MUST focus on emotional connection, romantic chemistry, and relationship development.\n");
            sb.append("- Include longing, tension, small gestures, romantic dialogue, and internal emotion.\n");
            sb.append("- Do NOT turn the story into a detached adventure unless it clearly supports the romance arc.\n");
            sb.append("- Choices MUST reflect emotional decisions or relationship directions.\n\n");

        } else if ("Fantasy".equalsIgnoreCase(genre)) {
            sb.append("- This is a FANTASY story with magic, myth, or supernatural elements.\n");
            sb.append("- Use vivid world building, mystical rules, legendary creatures, or enchanted locations.\n\n");

        } else if ("SciFi".equalsIgnoreCase(genre) || "Science Fiction".equalsIgnoreCase(genre)) {
            sb.append("- This is SCIENCE FICTION.\n");
            sb.append("- Use advanced technology, speculative science, futuristic societies, or space travel.\n");
            sb.append("- Keep internal logic consistent with your invented tech/rules.\n\n");

        } else if ("Mystery".equalsIgnoreCase(genre)) {
            sb.append("- This is a MYSTERY story.\n");
            sb.append("- Include clues, red herrings, secrets, and investigative tension.\n");
            sb.append("- Each chapter should push the reader closer to uncovering the truth.\n\n");

        } else if ("Horror".equalsIgnoreCase(genre)) {
            sb.append("- This is a HORROR story.\n");
            sb.append("- The primary goal is to create fear, dread, suspense, and unease.\n");
            sb.append("- Focus on atmosphere (shadows, sounds, isolation), psychological tension, and looming threats.\n");
            sb.append("- Avoid cheap random gore; instead, build creeping dread and escalating terror.\n");
            sb.append("- Choices MUST feel dangerous, risky, or unsettling, with real consequences.\n\n");

        } else {
            sb.append("- Follow the tone, expectations, and themes of the chosen genre.\n\n");
        }

        sb.append("=== CHARACTER ===\n");
        if (ch != null) {
            sb.append("Name: ").append(ch.getName()).append("\n");
            sb.append("Traits: ").append(String.join(", ", ch.getTraits())).append("\n");
            sb.append("Backstory: ").append(ch.getBackstory()).append("\n\n");
        }

        sb.append("=== WORLD ===\n");
        if (world != null) {
            sb.append("Location: ").append(world.getLocation()).append("\n");
            sb.append("Rule: ").append(world.getRule()).append("\n");
            sb.append("History: ").append(world.getHistory()).append("\n\n");
        }

        sb.append("=== PREVIOUS CHAPTER SUMMARY ===\n");
        if (!history.isEmpty()) {
            SceneModel prev = history.get(history.size() - 1);
            String text = prev.getStoryText();
            if (text.length() > 500) text = text.substring(0, 500) + "...";
            sb.append(text).append("\n\n");
        } else {
            sb.append("This is the beginning of the story.\n\n");
        }

        sb.append("=== PLAYER CHOICE HISTORY ===\n");
        if (!historyChoices.isEmpty()) {
            for (ChoiceRecordModel c : historyChoices) {
                sb.append("Chapter ").append(c.getChapter())
                        .append(" → Choice ").append(c.getChoiceId())
                        .append(": ").append(c.getChoiceDescription())
                        .append("\n");
            }
        } else {
            sb.append("No choices made yet.\n");
        }
        sb.append("\n");

        if (lastChoiceId != null) {
            sb.append("The user selected choice ").append(lastChoiceId)
                    .append(" in the previous chapter. Continue the story accordingly.\n\n");
        }

        // ---------------------------------------------------------
        // FINAL CHAPTER INSTRUCTIONS
        // ---------------------------------------------------------
        if (finalChapter) {
            sb.append("=== FINAL CHAPTER RULES ===\n");
            sb.append("This IS the final chapter.\n");
            sb.append("Create an emotional, powerful conclusion that ties together:\n");
            sb.append("- The chosen GENRE (").append(genre).append(")\n");
            sb.append("- The character's traits and backstory\n");
            sb.append("- The world's rules and history\n");
            sb.append("- EVERY choice the user made across all chapters\n");
            sb.append("Do NOT generate any further choices. This chapter ends the story.\n\n");

            sb.append("Return ONLY this JSON format:\n");
            sb.append("{\n");
            sb.append("  \"chapter\": ").append(chapter).append(",\n");
            sb.append("  \"isEnding\": true,\n");
            sb.append("  \"story\": \"Final ending text.\",\n");
            sb.append("  \"choices\": { \"A\": \"\", \"B\": \"\", \"C\": \"\" }\n");
            sb.append("}\n");
            return sb.toString();
        }

        // ---------------------------------------------------------
        // NORMAL CHAPTER RULES
        // ---------------------------------------------------------
        sb.append("=== NORMAL CHAPTER RULES ===\n");
        sb.append("Advance the story in a way that is strongly aligned with the GENRE.\n");
        sb.append("Each choice MUST meaningfully influence future chapters.\n");
        sb.append("Choices MUST be distinct, concrete actions the character can take.\n\n");

        sb.append("Return ONLY this JSON format:\n");
        sb.append("{\n");
        sb.append("  \"chapter\": ").append(chapter).append(",\n");
        sb.append("  \"isEnding\": false,\n");
        sb.append("  \"story\": \"Story text for this chapter.\",\n");
        sb.append("  \"choices\": {\n");
        sb.append("    \"A\": \"Choice A description\",\n");
        sb.append("    \"B\": \"Choice B description\",\n");
        sb.append("    \"C\": \"Choice C description\"\n");
        sb.append("  }\n");
        sb.append("}\n");

        return sb.toString();
    }
}
