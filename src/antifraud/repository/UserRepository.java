package antifraud.repository;

import antifraud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    void removeByUsername(String username);

    void deleteByUsername(String username);

    Optional<User> findByUsername(String username);

}
