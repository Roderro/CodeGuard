package my.project.codeguard.service.user;


import my.project.codeguard.entity.User;
import my.project.codeguard.exception.EntityNotFoundException;
import my.project.codeguard.repository.UserRepository;
import my.project.codeguard.util.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setMessageTemplate("Code - {code}");
        userRepository.save(user);
    }


    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("user", String.valueOf(id)));
    }

    @Transactional
    public void setMessageTemplate(Long userId, String template) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user", String.valueOf(userId)));
        user.setMessageTemplate(template);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findByRoleIsNot(Role.ROLE_ADMIN);
    }

    @Transactional
    public void deleteUser(String username) {
        User delUser = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("user", username));
        userRepository.delete(delUser);
    }
}
