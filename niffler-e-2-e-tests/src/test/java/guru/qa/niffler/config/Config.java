package guru.qa.niffler.config;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.instance
        : LocalConfig.instance;
  }

  String frontUrl();

  String authUrl();

  String spendUrl();

  String userdataUrl();

  String gatewayUrl();

  String jdbcHost();

  String categoryUrl();

  String userUrl();

  String currencyGrpcHost();

  String spendGrpcHost();

  default String jdbcUser() {
    return "postgres";
  }

  default String jdbcPassword() {
    return "root";
  }

  default int jdbcPort() {
    return 5432;
  }

  default int currencyGrpcPort() {
    return 8092;
  }

  default int spendGrpcPort() {
    return 8095;
  }



}
