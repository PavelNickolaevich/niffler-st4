package guru.qa.niffler.api.register;

import retrofit2.Call;
import retrofit2.http.*;

public interface RegisterApi {
    @GET("/register")
    Call<Void> getRegister();

    @POST("/register")
    @FormUrlEncoded
    Call<Void> postRegister(
            @Header("Cookie")String cookie,
            @Field("_csrf") String _csrfToken,
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSbt
    );
}
