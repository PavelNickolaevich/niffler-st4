package guru.qa.niffler.test_hw.grphql_hw;

import guru.qa.niffler.api.GatewayGqlApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.GqlTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.jupiter.extension.ApiLoginExtensionLesson18;
import org.junit.jupiter.api.extension.RegisterExtension;


@GqlTest
public abstract class BaseGraphQLTestHw {

  @RegisterExtension
  protected final ApiLoginExtensionLesson18 apiLoginExtension = new ApiLoginExtensionLesson18(false);

  protected static final Config CFG = Config.getInstance();

  protected final GatewayGqlApiClient gatewayGqlApiClient = new GatewayGqlApiClient();

}
