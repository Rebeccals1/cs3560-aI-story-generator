package model.strategy;

import model.story.StoryModel;
import model.story.StoryStateModel;

/**
 * AdultMode
 *
 * Strategy implementation for more mature stories:
 *   - Richer emotional depth and nuance
 *   - Higher stakes and more complex conflicts
 *   - Mature themes allowed (but still within classroom-safe bounds)
 */
public class AdultMode implements StoryModeStrategy {

    @Override
    public void applyModeRules(StringBuilder sb,
                               StoryModel story,
                               String length,
                               String complexity,
                               String style) {

        int chapter = story.getState().getChapter();
        boolean finalChapter = (chapter >= StoryStateModel.MAX_CHAPTERS);

        sb.append("=== MODE RULES: ADULT / GENERAL AUDIENCE ===\n");
        sb.append("- Write for an older teen/adult audience.\n");
        sb.append("- You may use more complex vocabulary and layered emotions.\n");
        sb.append("- You may include tension, moral dilemmas, and darker moments,\n");
        sb.append("  but avoid explicit sexual content and extreme graphic violence.\n");
        sb.append("- Focus on character growth, internal conflict, and consequences\n");
        sb.append("  that feel meaningful in the chosen genre.\n\n");

        sb.append("Length preference: ").append(length).append("\n");
        sb.append("- 'Short' → tight pacing, minimal digressions.\n");
        sb.append("- 'Medium' → balance of action, dialogue, and description.\n");
        sb.append("- 'Long' → allow richer description and more complex scenes.\n\n");

        sb.append("Style preference: ").append(style).append("\n");
        sb.append("- 'Descriptive' → vivid imagery and sensory detail.\n");
        sb.append("- 'Neutral' → straightforward prose with occasional description.\n");
        sb.append("- 'Terse' → sharp, efficient prose focusing on action & dialogue.\n\n");

        if (finalChapter) {
            sb.append("FINAL CHAPTER (ADULT MODE):\n");
            sb.append("- The ending may be happy, bittersweet, or tragic,\n");
            sb.append("  but it MUST feel earned and thematically satisfying.\n");
            sb.append("- Explicitly connect the outcome to the player's choices\n");
            sb.append("  across all previous chapters.\n\n");
        }
    }
}
