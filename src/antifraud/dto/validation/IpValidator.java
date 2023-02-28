package antifraud.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IpValidator implements ConstraintValidator<IpConstraint, String> {

    private static final String ipRegex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";

    private static final Pattern pattern = Pattern.compile(ipRegex);

    @Override
    public void initialize(IpConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String ip, ConstraintValidatorContext context) {
        final var matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}
