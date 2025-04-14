package my.project.codeguard.util.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import my.project.codeguard.util.validation.validator.UniqueUsernameValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
    String message() default "Пользователь с таким именем уже существует";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}