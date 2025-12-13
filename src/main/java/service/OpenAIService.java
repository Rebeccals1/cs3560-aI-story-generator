package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.OpenAIClient;
import model.story.ChoiceModel;
import model.story.SceneModel;

/**
 * OpenAIService
 *
 * - Wraps OpenAIClient safely
 * - Disables AI gracefully if API key/config is missing
 */
public class OpenAIService {

    private OpenAIClient client;
    private boolean enabled = true;

    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAIService() {
        try {
            client = OpenAIClient.getInstance();
        } catch (IllegalStateException ex) {
            enabled = false;
            client = null;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public SceneModel generateScene(String prompt) throws Exception {

        if (!enabled) {
            throw new IllegalStateException(
                    "AI story generation is disabled.\n\n" +
                            "Missing OPENAI_API_KEY in config.properties."
            );
        }

        String raw = client.sendRequest(prompt);
        return parseResponse(raw);
    }

    /* =========================================================
       RESPONSE PARSING
       ========================================================= */

    private SceneModel parseResponse(String raw) throws Exception {

        JsonNode root = mapper.readTree(raw);
        JsonNode choicesNode = root.get("choices");

        if (choicesNode == null || choicesNode.isEmpty()) {
            throw new Exception("OpenAI returned no choices.");
        }

        JsonNode message = choicesNode.get(0).get("message");
        String content = message.get("content").asText();

        JsonNode storyJson = mapper.readTree(content);

        require(storyJson, "story");
        require(storyJson, "isEnding");

        String storyText = storyJson.get("story").asText();
        boolean isEnding = storyJson.get("isEnding").asBoolean();

        if (isEnding) {
            return new SceneModel(
                    storyText,
                    new ChoiceModel("A", "The End"),
                    new ChoiceModel("B", "The End"),
                    new ChoiceModel("C", "The End"),
                    true
            );
        }

        JsonNode c = storyJson.get("choices");
        require(c, "A");
        require(c, "B");
        require(c, "C");

        return new SceneModel(
                storyText,
                new ChoiceModel("A", c.get("A").asText()),
                new ChoiceModel("B", c.get("B").asText()),
                new ChoiceModel("C", c.get("C").asText()),
                false
        );
    }

    private void require(JsonNode node, String field) throws Exception {
        if (node == null || node.get(field) == null) {
            throw new Exception("Missing required field: " + field);
        }
    }
}
