package antifraud.repository;

import antifraud.model.Role;
import antifraud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// https://stackoverflow.com/questions/33438483/spring-data-jpa-query-manytomany

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findFirstByUserRoles(Role userRoles);

    void removeByUsername(String username);

    void deleteByUsername(String username);

    Optional<User> findByUsername(String username);

}
