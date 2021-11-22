package pl.bookstore.ebook.uploads.app;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.uploads.app.port.UploadUseCase;
import pl.bookstore.ebook.uploads.domain.Upload;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
class UploadService implements UploadUseCase {
  private final Map<String, Upload> storage = new ConcurrentHashMap<>();

  @Override
  public Upload save(SaveUploadCommand command) {
    final String newId = RandomStringUtils.randomAlphanumeric(8);
    Upload upload =
        new Upload(
            newId, command.file(), command.contentType(), command.filename(), LocalDateTime.now());
    storage.put(upload.getId(), upload);
    System.out.println("Upload saved " + upload.getFilename() + " with id: " + newId);
    return upload;
  }
}
