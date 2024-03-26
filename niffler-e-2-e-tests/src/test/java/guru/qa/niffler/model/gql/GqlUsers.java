package guru.qa.niffler.model.gql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GqlUsers extends GqlResponse<GqlUsers>{
    List<GqlUser> users;
}
