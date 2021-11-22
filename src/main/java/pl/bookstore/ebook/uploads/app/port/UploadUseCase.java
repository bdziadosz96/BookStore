package pl.bookstore.ebook.uploads.app.port;

import pl.bookstore.ebook.uploads.domain.Upload;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);

    record SaveUploadCommand(String filename, byte[] file, String contentType) {}
}
