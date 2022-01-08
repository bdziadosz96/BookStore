package pl.bookstore.ebook.uploads.app;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.uploads.app.port.UploadUseCase;
import pl.bookstore.ebook.uploads.db.UploadJpaRepository;
import pl.bookstore.ebook.uploads.domain.Upload;

@Service
class UploadService implements UploadUseCase {
    private final UploadJpaRepository repository;

    public UploadService(UploadJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Upload save(SaveUploadCommand command) {
        Upload upload = new Upload(command.filename(), command.contentType(), command.file());
        repository.save(upload);
        System.out.println("Upload saved " + upload.getFilename() + " with id: " + upload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Upload> getAll() {
        return repository.findAll();
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }
}
