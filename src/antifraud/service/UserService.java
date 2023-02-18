package antifraud.service;

import antifraud.model.User;
import antifraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User enterUser(User user) {
        return userRepository.save(user);
    }


}
