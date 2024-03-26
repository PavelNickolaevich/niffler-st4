package guru.qa.niffler.model.gql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlUpdateUser extends GqlResponse<GqlUpdateUser> {
    private GqlUser updateUser;
}
