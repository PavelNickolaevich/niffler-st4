package guru.qa.niffler.test_hw.grphql_hw;

import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.jupiter.annotations.FriendUser;
import guru.qa.niffler.jupiter.annotations.GqlRequestFileConverted;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.FriendState;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.gql.GqlRequest;
import guru.qa.niffler.model.gql.GqlUpdateUser;
import guru.qa.niffler.model.gql.GqlUser;
import guru.qa.niffler.model.gql.GqlUsers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class GqlUsersTest extends BaseGraphQLTestHw {

    @Test
    @ApiLogin(
            user = @TestUser
    )
    void currentUserShouldBeReturned(@User UserJson testUser,
                                     @Token String bearerToken,
                                     @GqlRequestFile("gql/currentUserQuery.json") GqlRequest request) throws Exception {
        final GqlUser response = gatewayGqlApiClient.currentUser(bearerToken, request);
        Assertions.assertEquals(
                testUser.username(),
                response.getData().getUser().getUsername()
        );
    }

    @Test
    @ApiLogin(
            user = @TestUser(
                    friendUser = {
                            @FriendUser(),
                            @FriendUser(),
                            @FriendUser(friendState = FriendState.INVITE_RECEIVED)
                    }
            )
    )
    void checkStateFriendStatus(@User UserJson testUser,
                                @Token String bearerToken,
                                @GqlRequestFile("gql/getFriendsQuery.json") guru.qa.niffler.model.gql.GqlRequest request) throws Exception {

        final GqlUser response = gatewayGqlApiClient.getFriends(bearerToken, request);
        Assertions.assertAll(
                () -> Assertions.assertEquals(FriendState.FRIEND, response.getData().getUser().getFriends().get(0).getFriendState()),
                () -> Assertions.assertEquals(FriendState.FRIEND, response.getData().getUser().getFriends().get(1).getFriendState()),
                () -> Assertions.assertEquals(FriendState.INVITE_RECEIVED, response.getData().getUser().getInvitations().get(0).getFriendState())
        );
    }


    @Test
    @ApiLogin(
            user = @TestUser(
                    friendUser = {
                            @FriendUser(),
                            @FriendUser(),
                            @FriendUser(friendState = FriendState.INVITE_RECEIVED)
                    }
            )
    )
    void checkStateFriendStatusBD(@User UserJson testUser,
                                  @Token String bearerToken,
                                  @GqlRequestFile("gql/getFriendsQuery.json") guru.qa.niffler.model.gql.GqlRequest request) throws Exception {

        final GqlUser response = gatewayGqlApiClient.getFriends(bearerToken, request);
        Assertions.assertAll(
                () -> Assertions.assertEquals(FriendState.FRIEND, response.getData().getUser().getFriends().get(0).getFriendState()),
                () -> Assertions.assertEquals(FriendState.FRIEND, response.getData().getUser().getFriends().get(1).getFriendState()),
                () -> Assertions.assertEquals(FriendState.INVITE_RECEIVED, response.getData().getUser().getInvitations().get(0).getFriendState()));
    }

    @Test
    @ApiLogin(
            user = @TestUser
    )
    void chekUsers(@User UserJson testUser,
                   @Token String bearerToken,
                   @GqlRequestFile("gql/usersQuery.json") guru.qa.niffler.model.gql.GqlRequest request) throws Exception {

        final GqlUsers response = gatewayGqlApiClient.users(bearerToken, request);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response.getData().getUsers()),
                () -> Assertions.assertFalse(response.getData().getUsers().isEmpty())
        );
    }

    @Test
    @ApiLogin(
            user = @TestUser
    )
    void chekUpdateUser(@User UserJson testUser,
                        @Token String bearerToken,
                        @GqlRequestFile("gql/updateUserMutation.json") guru.qa.niffler.model.gql.GqlRequest request) throws Exception {

        final GqlUpdateUser response = gatewayGqlApiClient.updateUser(bearerToken, request);

        Assertions.assertAll(
                () -> Assertions.assertEquals("Pizzly", response.getData().getUpdateUser().getFirstname()),
                () -> Assertions.assertEquals("Pizzlyvich", response.getData().getUpdateUser().getSurname()),
                () -> Assertions.assertEquals(CurrencyValues.EUR, response.getData().getUpdateUser().getCurrency())
        );
    }


    @ParameterizedTest
    @ApiLogin(user = @TestUser)
    @CsvSource({
            "gql/getFriends2FriedsSubQuery.json, Can`t fetch over 2 friends sub-queries",
            "gql/getFriends2InvitationsSubQuery.json, Can`t fetch over 2 invitations sub-queries"
    })
    void userWithFriendsMustReturnError(@GqlRequestFileConverted GqlRequest request,
                                        String expectedError,
                                        @Token String bearerToken) throws Exception {

        final GqlUser response = gatewayGqlApiClient.getFriends(bearerToken, request);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals(expectedError, response.getErrors().get(0).message());
    }
}
