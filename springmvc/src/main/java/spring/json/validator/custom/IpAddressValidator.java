package spring.json.validator.custom;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpAddressValidator implements ConstraintValidator<IpAddressAnnotation, String> {

    private Pattern pattern;

    @Override
    public void initialize(IpAddressAnnotation constraintAnnotation) {
        //init with config regex value from @IpAddress
        String regex = constraintAnnotation.regex();
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //case null should validate with @NotNull
        if (value == null) {
            return true;
        }


        Matcher matcher = pattern.matcher(value);
        if (!matcher.matches()) {
            return false;
        }
        return validateEachGroup(matcher);
    }

    private boolean validateEachGroup(Matcher matcher) {
        try {
            for (int i = 1; i <= 4; i++) {
                int octet = Integer.parseInt(matcher.group(i));
                if (octet > 255) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}