package guru.qa.niffler.api;

import guru.qa.niffler.model.gql.GqlRequest;
import guru.qa.niffler.model.gql.GqlUpdateUser;
import guru.qa.niffler.model.gql.GqlUser;
import guru.qa.niffler.model.gql.GqlUsers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GraphQlGatewayApi {

  @POST("/graphql")
  Call<GqlUser> currentUser(@Header("Authorization") String bearerToken,
                            @Body GqlRequest gqlRequest);

  @POST("/graphql")
  Call<GqlUser> getFriends(@Header("Authorization") String bearerToken,
                        @Body GqlRequest gqlRequest);

  @POST("/graphql")
  Call<GqlUsers> users(@Header("Authorization") String bearerToken,
                       @Body GqlRequest gqlRequest);

  @POST("/graphql")
  Call<GqlUpdateUser> updateUser(@Header("Authorization") String bearerToken,
                                 @Body GqlRequest gqlRequest);
}
