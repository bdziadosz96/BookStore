package pl.bookstore.ebook.uploads.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.ebook.uploads.domain.Upload;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {}
