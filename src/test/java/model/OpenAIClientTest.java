package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OpenAIClient.
 *
 * Uses the testing constructor so no config.properties is required.
 * Singleton is reset before each test to ensure clean state.
 */
public class OpenAIClientTest {

    @BeforeEach
    void resetSingleton() {
        OpenAIClient.resetForTests();
    }

    /* ---------------------------------------------------------
       1. API KEY VALIDATION
       --------------------------------------------------------- */

    @Test
    void testApiKeyMissingThrowsError() {
        // Your real class throws RuntimeException when missing API key
        assertThrows(RuntimeException.class, () ->
                new OpenAIClient("", "gpt-4o-mini")
        );

        assertThrows(RuntimeException.class, () ->
                new OpenAIClient(null, "gpt-4o-mini")
        );
    }

    @Test
    void testApiKeyValidCreatesClient() {
        OpenAIClient client = new OpenAIClient("fake_key", "test-model");
        assertNotNull(client);
    }


    /* ---------------------------------------------------------
       2. JSON ESCAPING (via reflection to call private method)
       --------------------------------------------------------- */

    @Test
    void testJsonEscaping() throws Exception {

        var method = OpenAIClient.class.getDeclaredMethod("toJsonString", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(null, "He said \"Hello\"");
        assertEquals("\"He said \\\"Hello\\\"\"", result);

        result = (String) method.invoke(null, "Line1\nLine2");
        assertEquals("\"Line1\\nLine2\"", result);
    }


    /* ---------------------------------------------------------
       3. sendRequest() RETRY LOGIC — using override class
       --------------------------------------------------------- */

    @Test
    void testSendRequestRetriesAndSucceeds() throws Exception {

        class FakeClient extends OpenAIClient {

            int callCount = 0;

            FakeClient() {
                super("testkey", "test-model");
            }

            @Override
            protected String sendHttp(String prompt)
                    throws IOException, InterruptedException {

                callCount++;

                if (callCount == 1) {
                    throw new IOException("Simulated failure");
                }
                return "SUCCESS";
            }
        }

        FakeClient client = new FakeClient();
        String result = client.sendRequest("hello");

        assertEquals("SUCCESS", result);
        assertEquals(2, client.callCount); // retried once
    }


    /* ---------------------------------------------------------
       4. sendRequest() throws after exhausting retries
       --------------------------------------------------------- */

    @Test
    void testSendRequestFailsAfterRetries() {

        class AlwaysFailClient extends OpenAIClient {

            AlwaysFailClient() {
                super("testkey", "test-model");
            }

            @Override
            protected String sendHttp(String prompt)
                    throws IOException, InterruptedException {

                throw new IOException("Always failing");
            }
        }

        AlwaysFailClient client = new AlwaysFailClient();

        assertThrows(IOException.class, () ->
                client.sendRequest("hello")
        );
    }
}
