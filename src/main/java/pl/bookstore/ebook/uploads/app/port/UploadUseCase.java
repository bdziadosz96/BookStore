package pl.bookstore.ebook.uploads.app.port;

import java.util.List;
import java.util.Optional;
import pl.bookstore.ebook.uploads.domain.Upload;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);

    Optional<Upload> getById(Long id);

    List<Upload> getAll();

    void removeById(Long id);

    record SaveUploadCommand(String filename, byte[] file, String contentType) {}
}
