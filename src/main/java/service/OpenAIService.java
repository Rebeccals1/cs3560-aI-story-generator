package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.OpenAIClient;
import model.story.ChoiceModel;
import model.story.SceneModel;

/**
 * OpenAIService — Correct JSON Parsing for OpenAI Chat API
 * Updated to support:
 *   - Missing choices on final chapter ("isEnding": true)
 *   - Cleaner error handling
 */
public class OpenAIService {

    private final OpenAIClient client = OpenAIClient.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    public SceneModel generateScene(String prompt) throws Exception {
        String raw = client.sendRequest(prompt);
        return parseOpenAIResponse(raw);
    }

    /**
     * Extracts the assistant JSON response, then parses the fields into SceneModel.
     */
    private SceneModel parseOpenAIResponse(String raw) throws Exception {

        JsonNode root = mapper.readTree(raw);

        // ------------------------------------------
        // 1. Extract assistant message content
        // ------------------------------------------
        JsonNode choicesNode = root.get("choices");
        if (choicesNode == null || !choicesNode.isArray() || choicesNode.size() == 0) {
            throw new Exception("OpenAI returned no choices:\n" + raw);
        }

        JsonNode message = choicesNode.get(0).get("message");
        if (message == null || message.get("content") == null) {
            throw new Exception("Missing assistant message content.\nRaw response:\n" + raw);
        }

        String content = message.get("content").asText();

        // ------------------------------------------
        // 2. The content ITSELF is the JSON story
        // ------------------------------------------
        JsonNode storyJson = mapper.readTree(content);
        if (storyJson == null) {
            throw new Exception("Assistant message did not contain valid JSON:\n" + content);
        }

        // Required for ALL chapters
        require(storyJson, "story");
        require(storyJson, "isEnding");

        String storyText = storyJson.get("story").asText("");
        boolean isEnding = storyJson.get("isEnding").asBoolean(false);

        // ------------------------------------------
        // 3. FINAL CHAPTER — NO CHOICES REQUIRED
        // ------------------------------------------
        if (isEnding) {
            // Create dummy choices so SceneModel + UI don't crash
            ChoiceModel A = new ChoiceModel("A", "The End");
            ChoiceModel B = new ChoiceModel("B", "The End");
            ChoiceModel C = new ChoiceModel("C", "The End");

            return new SceneModel(storyText, A, B, C, true);
        }

        // ------------------------------------------
        // 4. NON-FINAL CHAPTER — CHOICES REQUIRED
        // ------------------------------------------
        require(storyJson, "choices");
        JsonNode choiceJson = storyJson.get("choices");

        require(choiceJson, "A");
        require(choiceJson, "B");
        require(choiceJson, "C");

        ChoiceModel A = new ChoiceModel("A", choiceJson.get("A").asText(""));
        ChoiceModel B = new ChoiceModel("B", choiceJson.get("B").asText(""));
        ChoiceModel C = new ChoiceModel("C", choiceJson.get("C").asText(""));

        return new SceneModel(storyText, A, B, C, false);
    }

    /**
     * Helper Throws Clean Errors Instead of NullPointer Exceptions
     */
    private void require(JsonNode node, String fieldName) throws Exception {
        if (node == null || node.get(fieldName) == null || node.get(fieldName).isNull()) {
            throw new Exception("Missing required field \"" + fieldName + "\" in story JSON.");
        }
    }
}
