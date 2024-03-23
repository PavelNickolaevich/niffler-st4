package guru.qa.niffler.api.register;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.api.cookie.ThreadSafeCookieManager;

import java.io.IOException;

public class RegisterApiClient extends RestClient {

    private RegisterApi registerApi;

    public RegisterApiClient() {
        super(
                "http://127.0.0.1:9000/"
        );
        this.registerApi = retrofit.create(RegisterApi.class);

    }

    public void register(String username, String password) throws IOException {
        registerApi.getRegister().execute();
        final String XSRF_TOKEN = ThreadSafeCookieManager.INSTANCE.getCookieValue("XSRF-TOKEN");
        registerApi.postRegister(
                "XSRF-TOKEN=" + XSRF_TOKEN,
                XSRF_TOKEN,
                username,
                password,
                password).
                execute();
    }

}
