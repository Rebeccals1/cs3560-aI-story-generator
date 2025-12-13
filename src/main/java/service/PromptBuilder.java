package service;

import model.strategy.StoryModeStrategy;
import model.story.*;

import java.util.List;

/**
 * PromptBuilder
 *
 * Builds a compact, token-safe prompt for each chapter.
 *
 * Key design changes:
 *  - Uses summaries instead of full history
 *  - Enforces strict JSON-only output
 *  - Caps scene length explicitly
 *  - Keeps total tokens per request < 1000
 *  - STRONGLY enforces genre correctness
 *
 * This is intentional and documented behavior.
 */
public class PromptBuilder {

    /** STRATEGY PATTERN: injected at runtime (Child-Friendly vs Adult) */
    private StoryModeStrategy modeStrategy;

    public PromptBuilder() {}

    public PromptBuilder(StoryModeStrategy modeStrategy) {
        this.modeStrategy = modeStrategy;
    }

    public void setModeStrategy(StoryModeStrategy strategy) {
        this.modeStrategy = strategy;
    }

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

        String genre = storyModel.getGenre();

        StringBuilder sb = new StringBuilder();

        /* ---------------------------------------------------------
           CORE INSTRUCTIONS (COMPACT + STRICT)
           --------------------------------------------------------- */
        sb.append("""
        Return ONLY valid JSON in this format:
        {
          "chapter": %d,
          "isEnding": true/false,
          "story": "...",
          "choices": { "A": "...", "B": "...", "C": "..." }
        }

        Do not include markdown, commentary, or extra text.
        Write 2–3 short paragraphs (120–180 words max).
        """.formatted(chapter));

        /* ---------------------------------------------------------
           GENRE ENFORCEMENT (STRICT)
           --------------------------------------------------------- */
        sb.append("GENRE: ").append(genre).append("\n");

        switch (genre.toLowerCase()) {

            case "scifi", "science fiction" -> sb.append("""
            This is SCIENCE FICTION.
            All unusual phenomena MUST have a technological, scientific, or extraterrestrial explanation.
            Include at least ONE of the following:
            - advanced technology
            - futuristic science
            - alien biology
            - artificial intelligence
            - experimental devices
            DO NOT include magic, spells, mythical creatures, or unexplained fantasy elements.
            DO NOT describe events as magical or enchanted.
            """);

            case "fantasy" -> sb.append("""
            This is FANTASY.
            Magic, supernatural forces, or mythical creatures are REQUIRED.
            The world does NOT require scientific explanations.
            Avoid modern technology unless it is explicitly magical.
            """);

            case "horror" -> sb.append("""
            This is HORROR.
            The primary goal is fear, dread, or unease.
            Focus on atmosphere, tension, isolation, or psychological terror.
            DO NOT make the tone whimsical, cute, or adventurous.
            """);

            case "mystery" -> sb.append("""
            This is a MYSTERY.
            The story must involve clues, investigation, unanswered questions, or hidden truths.
            Events must be explainable through logic or human action.
            DO NOT resolve events using magic or coincidence.
            """);

            case "romance" -> sb.append("""
            This is ROMANCE.
            Emotional connection, attraction, or relationship development MUST be central.
            Each chapter must focus on feelings, tension, or interpersonal choices.
            DO NOT shift focus to action-heavy or world-saving plots.
            """);

            default -> sb.append("""
            Follow the conventions of the selected genre carefully.
            """);
        }

        sb.append("\n");
        sb.append("Before responding, verify that the story clearly matches the specified genre.\n");

        /* ---------------------------------------------------------
           MODE RULES (STRATEGY PATTERN)
           --------------------------------------------------------- */
        if (modeStrategy != null) {
            sb.append("Mode rules: ");
            modeStrategy.applyModeRules(sb, storyModel, length, complexity, style);
            sb.append("\n");
        }

        /* ---------------------------------------------------------
           CHARACTER SUMMARY (COMPRESSED)
           --------------------------------------------------------- */
        if (ch != null) {
            sb.append("Character: ")
                    .append(ch.getName());

            if (ch.getTraits() != null && !ch.getTraits().isEmpty()) {
                sb.append(" — ").append(String.join(", ", ch.getTraits()));
            }

            sb.append(".\n");
        }

        /* ---------------------------------------------------------
           WORLD SUMMARY (COMPRESSED)
           --------------------------------------------------------- */
        if (world != null) {
            sb.append("World: ")
                    .append(world.getLocation());

            if (world.getRule() != null && !world.getRule().isBlank()) {
                sb.append(". Rule: ").append(world.getRule());
            }

            sb.append(".\n");
        }

        /* ---------------------------------------------------------
           PREVIOUS SCENE SUMMARY (1–2 SENTENCES)
           --------------------------------------------------------- */
        if (!history.isEmpty()) {
            SceneModel prev = history.get(history.size() - 1);
            String prevText = prev.getStoryText();

            if (prevText.length() > 200) {
                prevText = prevText.substring(0, 200) + "...";
            }

            sb.append("Previously: ").append(prevText).append("\n");
        } else {
            sb.append("Previously: This is the beginning of the story.\n");
        }

        /* ---------------------------------------------------------
           LAST PLAYER CHOICE
           --------------------------------------------------------- */
        if (lastChoiceId != null) {
            sb.append("Last choice selected: ").append(lastChoiceId).append(".\n");
        }

        /* ---------------------------------------------------------
           FINAL VS NORMAL CHAPTER RULES
           --------------------------------------------------------- */
        if (finalChapter) {
            sb.append("""
            This is the FINAL chapter.
            Resolve the story conclusively.
            Do NOT introduce new conflicts.
            Do NOT generate further choices.
            """);

            sb.append("""
            {
              "chapter": %d,
              "isEnding": true,
              "story": "Final ending text.",
              "choices": { "A": "", "B": "", "C": "" }
            }
            """.formatted(chapter));

            return sb.toString();
        }

        /* ---------------------------------------------------------
           NORMAL CHAPTER RULES
           --------------------------------------------------------- */
        sb.append("""
        Advance the story naturally.
        Each choice must be a distinct, meaningful action
        that can influence future chapters.
        """);

        sb.append("""
        {
          "chapter": %d,
          "isEnding": false,
          "story": "Story text for this chapter.",
          "choices": {
            "A": "Choice A description",
            "B": "Choice B description",
            "C": "Choice C description"
          }
        }
        """.formatted(chapter));

        return sb.toString();
    }
}
