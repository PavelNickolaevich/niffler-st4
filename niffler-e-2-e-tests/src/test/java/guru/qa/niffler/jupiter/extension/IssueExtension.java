package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.github.GhApiClient;
import guru.qa.niffler.jupiter.annotations.DisabledByIssue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class IssueExtension implements ExecutionCondition {


//    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
//    private static final Retrofit retrofit = new Retrofit.Builder()
//            .client(httpClient)
//            .baseUrl("https://api.github.com")
//            .addConverterFactory(JacksonConverterFactory.create())
//            .build();
//
//    private final GhApi ghApi = retrofit.create(GhApi.class);

    private final GhApiClient ghApiClient = new GhApiClient();

    @SneakyThrows
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        DisabledByIssue disabledByIssue = AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                DisabledByIssue.class
        ).orElse(
                AnnotationSupport.findAnnotation(
                        context.getRequiredTestClass(),
                        DisabledByIssue.class
                ).orElse(null)
        );

        if (disabledByIssue != null) {

//            JsonNode responseBody = ghApi.issue(
//                    "Bearer " + System.getenv("GH_TOKEN"),
//                    disabledByIssue.value()
//            ).execute().body();

            return "open".equals(ghApiClient.getIssueState(disabledByIssue.value()))
                    ? ConditionEvaluationResult.disabled("Disabled by issue #" + disabledByIssue.value())
                    : ConditionEvaluationResult.enabled("Issue closed");
        }
        return ConditionEvaluationResult.enabled("Annotation not found");
    }
}
