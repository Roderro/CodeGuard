package my.project.codeguard.util.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import my.project.codeguard.service.user.UserDetailsServiceImpl;
import my.project.codeguard.util.validation.annotation.UniqueUsername;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private UserDetailsServiceImpl userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        try {
            userService.loadUserByUsername(username);
            return false;
        } catch (UsernameNotFoundException e) {
            return true;
        }
    }
}