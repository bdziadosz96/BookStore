package pl.bookstore.ebook.catalog.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.ebook.order.domain.Recipient;

public interface RecipientJpaRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findRecipientByEmailIgnoreCase(String email);
}
