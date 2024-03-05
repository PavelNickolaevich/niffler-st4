package guru.qa.niffler.db.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.p6spy.engine.spy.appender.StdoutLogger;
import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;

import java.util.Objects;

public class JsonAllureAppender extends StdoutLogger {

    private final String templateName = "json-query.ftl";
    private final AttachmentProcessor<AttachmentData> attachmentProcessor = new DefaultAttachmentProcessor();


    public void logJson(String name, Object json) throws JsonProcessingException {
        if (Objects.nonNull(json)) {
            JsonAttachment attachment = new JsonAttachment(
                    name,
                    new  ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json)
            );
            attachmentProcessor.addAttachment(attachment, new FreemarkerAttachmentRenderer(templateName));
        }
    }

}
