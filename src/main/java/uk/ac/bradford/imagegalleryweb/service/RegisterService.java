package uk.ac.bradford.imagegalleryweb.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.ac.bradford.imagegalleryweb.entity.User;
import uk.ac.bradford.imagegalleryweb.repository.UserRepository;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Injects the repository and password encoder.
    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String password) {

        // Creates a new user object.
        User user = new User();

        // Sets the username.
        user.setUsername(username);

        // Encodes the password before saving.
        user.setPassword(passwordEncoder.encode(password));

        // Enables the user account.
        user.setEnabled(true);

        // Saves the user in the database.
        userRepository.save(user);
    }
}