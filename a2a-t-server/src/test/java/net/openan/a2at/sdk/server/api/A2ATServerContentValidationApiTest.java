package net.openan.a2at.sdk.server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.openan.a2at.sdk.core.exception.A2ATErrorCodes;
import net.openan.a2at.sdk.core.model.NegotiationContext;
import net.openan.a2at.sdk.core.model.NegotiationPerformative;
import net.openan.a2at.sdk.core.validation.ContentValidationException;
import net.openan.a2at.sdk.negotiation.content.InformationProposeContent;
import net.openan.a2at.sdk.negotiation.content.NegotiationItem;
import net.openan.a2at.sdk.negotiation.content.NegotiationProposeData;
import net.openan.a2at.sdk.server.A2ATServer;
import org.junit.jupiter.api.Test;

class A2ATServerContentValidationApiTest {

    private static final String UUID = "3dbc13b5-bd57-4c2b-b503-24e381b6c8d3";

    @Test
    void validateTaskPromptRejectsNullTemplateUriWithValidationInvalidInput() throws IOException {
        A2ATServer server = new A2ATServer(writeEnv());

        ContentValidationException error = assertThrows(
                ContentValidationException.class,
                () -> server.validateTaskPromptAndDataFilling("test prompt", Map.of(), null));

        assertEquals(A2ATErrorCodes.VALIDATION_INVALID_INPUT, error.getCode());
    }

    @Test
    void validateNotificationPromptRejectsNullTemplateUriWithValidationInvalidInput() throws IOException {
        A2ATServer server = new A2ATServer(writeEnv());

        ContentValidationException error = assertThrows(
                ContentValidationException.class,
                () -> server.validateNotificationPromptAndDataFilling("test prompt", Map.of(), null));

        assertEquals(A2ATErrorCodes.VALIDATION_INVALID_INPUT, error.getCode());
    }

    @Test
    void validateAuthPromptRejectsNullTemplateUriWithValidationInvalidInput() throws IOException {
        A2ATServer server = new A2ATServer(writeEnv());

        ContentValidationException error = assertThrows(
                ContentValidationException.class,
                () -> server.validateAuthPromptAndDataFilling("test prompt", Map.of(), null));

        assertEquals(A2ATErrorCodes.VALIDATION_INVALID_INPUT, error.getCode());
    }

    @Test
    void generateNegotiationProposePromptFromDataStillThrowsOnNullTemplateUri() throws IOException {
        A2ATServer server = new A2ATServer(writeEnv());

        assertThrows(
                NullPointerException.class,
                () -> server.generateNegotiationProposePromptFromData(
                        new NegotiationProposeData(
                                new NegotiationContext(UUID, 1, 5, NegotiationPerformative.PROPOSE),
                                new InformationProposeContent(List.of(new NegotiationItem("节能区域", "松山湖")), null)),
                        null));
    }

    @Test
    void getPromptStillThrowsOnNullTemplateUri() throws IOException {
        A2ATServer server = new A2ATServer(writeEnv());

        assertThrows(NullPointerException.class, () -> server.getPrompt(null));
    }

    private static Path writeEnv() throws IOException {
        Path tempDir = Files.createTempDirectory("a2at-server-content-validation-env");
        Path envFile = tempDir.resolve("server.env");
        Files.writeString(
                envFile,
                """
                A2AT_LANGUAGE=zh-CN
                A2AT_PROMPT_SOURCE_TYPE=classpath
                A2AT_PROMPT_RESOURCE_LOCAL_ROOT_DIR=
                A2AT_LLM_PROVIDER=openai
                A2AT_LLM_MODEL=example-model
                A2AT_LLM_BASE_URL=https://llm.example.test/v1
                A2AT_LLM_API_KEY=test-key
                A2AT_NEGOTIATION_STATE_STORE_TYPE=in_memory
                A2AT_PROMPT_COMPLIANCE_ENABLED=false
                """);
        return envFile;
    }
}
