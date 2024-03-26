package guru.qa.niffler.jupiter.annotations;

import guru.qa.niffler.jupiter.converter.GqlRequestConverter;
import org.junit.jupiter.params.converter.ConvertWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ConvertWith(GqlRequestConverter.class)
public @interface GqlRequestFileConverted {
    String value() default "";
}
