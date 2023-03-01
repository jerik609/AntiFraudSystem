package antifraud.resources;

import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.internal.constraintvalidators.hv.LuhnCheckValidator;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.Payload;
import java.lang.annotation.Annotation;

@Configuration
public class CustomValidatorConfiguration {

    private final ConstraintValidatorFactory constraintValidatorFactory = new ConstraintValidatorFactoryImpl();

    @Bean
    public LuhnCheckValidator customLuhnCheckValidator() {

        final var luhnCheckValidator = constraintValidatorFactory.getInstance(LuhnCheckValidator.class);

        LuhnCheck check = new LuhnCheck(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public int startIndex() {
                return 0;
            }

            @Override
            public int endIndex() {
                return Integer.MAX_VALUE;
            }

            @Override
            public int checkDigitIndex() {
                return -1;
            }

            @Override
            public boolean ignoreNonDigitCharacters() {
                return true;
            }
        };

        luhnCheckValidator.initialize(check);

        return luhnCheckValidator;
    }

}
