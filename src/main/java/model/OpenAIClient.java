package model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

/**
 * OpenAIClient (Singleton)
 *
 *  • Loads API key from config.properties
 *  • Optional test constructor allows injecting fake API keys
 *  • Throws IllegalStateException when API key is missing
 *  • Safe JSON escaping
 *  • Retry logic
 */
public class OpenAIClient {

    private static OpenAIClient instance;

    private final HttpClient client;
    private final String apiKey;
    private final String model;

    /* ============================================================
       MAIN PRODUCTION CONSTRUCTOR (loads config.properties)
       ============================================================ */
    private OpenAIClient() {

        Properties props = new Properties();

        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (in == null) {
                throw new IllegalStateException("""
                    Could not find config.properties.
                    It must be located in:
                      src/main/resources/config.properties
                    """);
            }

            props.load(in);

        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load config.properties", ex);
        }

        this.apiKey = props.getProperty("OPENAI_API_KEY");
        this.model = props.getProperty("OPENAI_TEXT_MODEL", "gpt-4o-mini");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("""
                OPENAI_API_KEY is missing in config.properties.

                You must provide:
                  OPENAI_API_KEY=your_key_here
                  OPENAI_TEXT_MODEL=gpt-4o-mini
                """);
        }

        this.client = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(15))
                .build();
    }

    /* ============================================================
       TESTING CONSTRUCTOR (bypasses config file)
       ============================================================ */
    public OpenAIClient(String apiKey, String model) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("API key is missing for test instance");
        }
        this.apiKey = apiKey;
        this.model = model;

        this.client = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(15))
                .build();
    }

    /* ============================================================
       SINGLETON ACCESS + TEST RESET
       ============================================================ */
    public static synchronized OpenAIClient getInstance() {
        if (instance == null) instance = new OpenAIClient();
        return instance;
    }

    /** For unit tests ONLY — allows a clean instance */
    public static synchronized void resetForTests() {
        instance = null;
    }

    /* ============================================================
       PUBLIC REQUEST METHOD WITH RETRIES
       ============================================================ */
    public String sendRequest(String prompt) throws Exception {
        int retries = 2;
        int attempt = 0;
        long backoff = 1000;

        while (true) {
            try {
                return sendHttp(prompt);
            } catch (IOException ex) {
                if (attempt >= retries) throw ex;
                Thread.sleep(backoff);
                backoff *= 2;
                attempt++;
            }
        }
    }

    /* ============================================================
       INTERNAL HTTP CALL
       ============================================================ */
    protected String sendHttp(String prompt) throws IOException, InterruptedException {

        String safePrompt = toJsonString(prompt);

        String json = """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "user",
                      "content": %s
                    }
                  ]
                }
                """.formatted(model, safePrompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .timeout(java.time.Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {
            throw new IOException("""
                API error: HTTP %d
                Response:
                %s
                """.formatted(response.statusCode(), response.body()));
        }

        return response.body();
    }

    /* ============================================================
       SAFE JSON STRING ESCAPER
       ============================================================ */
    private static String toJsonString(String text) {
        if (text == null) return "\"\"";

        String escaped = text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        return "\"" + escaped + "\"";
    }
}
