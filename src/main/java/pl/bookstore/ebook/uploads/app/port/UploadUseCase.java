package pl.bookstore.ebook.uploads.app.port;

import pl.bookstore.ebook.uploads.domain.Upload;

import java.util.List;
import java.util.Optional;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);

    Optional<Upload> getById(String id);

    List<Upload> getAll();

    record SaveUploadCommand(String filename, byte[] file, String contentType) {}
}
