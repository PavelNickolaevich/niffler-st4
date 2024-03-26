package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.model.FriendState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface FriendUser {
    String username() default "";

    String password() default "";

    FriendState friendState() default FriendState.FRIEND;
}
