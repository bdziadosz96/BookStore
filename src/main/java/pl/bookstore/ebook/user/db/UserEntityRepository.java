package pl.bookstore.ebook.user.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.ebook.user.domain.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
