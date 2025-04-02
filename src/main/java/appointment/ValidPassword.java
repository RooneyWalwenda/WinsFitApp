/*package appointment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.twilio.jwt.accesstoken.ChatGrant.Payload;

import jakarta.validation.Constraint;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password too weak: it must be six characters with a number, an uppercase and a special character together with a lower case.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}*/
