package uk.ac.bradford.imagegalleryweb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.bradford.imagegalleryweb.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}