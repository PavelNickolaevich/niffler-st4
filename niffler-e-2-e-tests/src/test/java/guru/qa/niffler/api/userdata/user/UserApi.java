package guru.qa.niffler.api.userdata.user;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserApi {

    @POST("/updateUserInfo")
    Call<UserJson> updateUserInfo(@Body UserJson user);

    @GET("/currentUser")
    Call<UserJson> currentUser(@Query("username") String username);

    @GET("/allUsers")
    Call<List<UserJson>> allUsers(@Query("username") String username);

}
