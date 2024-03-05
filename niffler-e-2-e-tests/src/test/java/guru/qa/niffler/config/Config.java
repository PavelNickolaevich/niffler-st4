package guru.qa.niffler.config;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.instance
        : LocalConfig.instance;
  }

  String frontUrl();

  String authUrl();

  String jdbcHost();

  String categoryUrl();

  String spendUrl();

  String currencyGrpcHost();

  default String jdbcUser() {
    return "postgres";
  }

  default String jdbcPassword() {
    return "root";
  }

  default int jdbcPort() {
    return 5432;
  }
}
