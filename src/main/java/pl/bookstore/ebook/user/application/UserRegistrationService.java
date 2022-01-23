package pl.bookstore.ebook.user.application;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.user.application.port.UserRegistrationUseCase;
import pl.bookstore.ebook.user.db.UserEntityRepository;
import pl.bookstore.ebook.user.domain.UserEntity;

@AllArgsConstructor
@Service
class UserRegistrationService implements UserRegistrationUseCase {
    private final UserEntityRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponse register(String username, String password) {
        if (repository.findByUsernameIgnoreCase(username).isPresent()) {
            return RegisterResponse.failure(String.format("%s already exist!", username));
        }
        UserEntity entity = new UserEntity(username, passwordEncoder.encode(password));
        repository.save(entity);
        return RegisterResponse.success(entity);
    }
}
