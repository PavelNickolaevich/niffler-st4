package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import guru.qa.niffler.jupiter.annotations.User;

public record TestData(
    @JsonIgnore String password,
    @JsonIgnore User.UserType userType
) {
}