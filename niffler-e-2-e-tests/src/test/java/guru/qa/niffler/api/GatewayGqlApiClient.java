package guru.qa.niffler.api;

import guru.qa.niffler.model.gql.GqlRequest;
import guru.qa.niffler.model.gql.GqlUpdateUser;
import guru.qa.niffler.model.gql.GqlUser;
import guru.qa.niffler.model.gql.GqlUsers;

public class GatewayGqlApiClient extends RestClient {

    private final GraphQlGatewayApi graphQlGatewayApi;

    public GatewayGqlApiClient() {
        super(
                CFG.gatewayUrl()
        );
        graphQlGatewayApi = retrofit.create(GraphQlGatewayApi.class);
    }

    public GqlUser currentUser(String bearerToken, GqlRequest request) throws Exception {
        return graphQlGatewayApi.currentUser(bearerToken, request).execute()
                .body();
    }

    public GqlUser getFriends(String bearerToken, GqlRequest request) throws Exception {
        return graphQlGatewayApi.getFriends(bearerToken, request).execute()
                .body();
    }

    public GqlUsers users(String bearerToken, GqlRequest request) throws Exception {
        return graphQlGatewayApi.users(bearerToken, request).execute()
                .body();
    }

    public GqlUpdateUser updateUser(String bearerToken, GqlRequest request) throws Exception {
        return graphQlGatewayApi.updateUser(bearerToken, request).execute()
                .body();
    }
}
