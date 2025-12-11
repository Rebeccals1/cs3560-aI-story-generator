package model.strategy;

import model.story.StoryModel;
import model.story.StoryStateModel;

/**
 * ChildFriendlyMode
 *
 * Strategy implementation for kid-safe stories:
 *   - Simple language
 *   - Positive / hopeful tone
 *   - No graphic violence, no gore, no explicit romance
 *   - Clear moral framing and gentle consequences
 */
public class ChildFriendlyMode implements StoryModeStrategy {

    @Override
    public void applyModeRules(StringBuilder sb,
                               StoryModel story,
                               String length,
                               String complexity,
                               String style) {

        int chapter = story.getState().getChapter();
        boolean finalChapter = (chapter >= StoryStateModel.MAX_CHAPTERS);

        sb.append("=== MODE RULES: CHILD-FRIENDLY ===\n");
        sb.append("- Write for a younger audience (middle-grade / early teen).\n");
        sb.append("- Use clear, simple sentences and avoid complex jargon.\n");
        sb.append("- Keep the tone hopeful, adventurous, and emotionally safe.\n");
        sb.append("- NO graphic violence, gore, explicit romance, or mature content.\n");
        sb.append("- Any danger should feel like a fantasy adventure, not realistic trauma.\n");
        sb.append("- Conflicts should be solvable through courage, kindness, or cleverness.\n");

        sb.append("- If scary elements exist (especially in Horror or Mystery),\n");
        sb.append("  they must be balanced with safety, friendship, and comfort.\n");

        sb.append("\n");

        sb.append("Length preference: ").append(length).append("\n");
        sb.append("- For 'Short', keep chapters concise and easy to read.\n");
        sb.append("- For 'Medium' or 'Long', you may add more description,\n");
        sb.append("  but still keep paragraphs manageable for young readers.\n\n");

        sb.append("Style preference: ").append(style).append("\n");
        sb.append("- If 'Descriptive', focus on sensory details that feel magical and fun.\n");
        sb.append("- If 'Neutral', keep a balanced mix of action and description.\n");
        sb.append("- If 'Terse', be brief but still warm and friendly.\n\n");

        if (finalChapter) {
            sb.append("FINAL CHAPTER (CHILD-FRIENDLY):\n");
            sb.append("- The ending MUST be emotionally satisfying and hopeful.\n");
            sb.append("- Resolve major conflicts in a reassuring, optimistic way.\n");
            sb.append("- Emphasize friendship, growth, and lessons learned.\n\n");
        }
    }
}
