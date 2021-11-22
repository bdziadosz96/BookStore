package pl.bookstore.ebook.uploads.web;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bookstore.ebook.uploads.app.port.UploadUseCase;
import pl.bookstore.ebook.uploads.domain.Upload;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/uploads")
@AllArgsConstructor
class UploadController {
  public final UploadUseCase upload;

  @GetMapping("/{id}")
  public ResponseEntity<UploadResponse> getUpload(@PathVariable String id) {
    return upload
        .getById(id)
        .map(
            uploadFile -> {
              final UploadResponse uploadResponse =
                  new UploadResponse(
                      uploadFile.getId(),
                      uploadFile.getContentType(),
                      uploadFile.getFilename(),
                      uploadFile.getCreateAt());
              return ResponseEntity.ok(uploadResponse);
            })
        .orElse(ResponseEntity.notFound().build());
  }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getUploadFile(@PathVariable String id) {
    return upload
        .getById(id)
        .map(
            file -> {
              String contentDisposition = "attachment; filename=\"" + file.getFilename() + "\"";
              final byte[] bytes = file.getFile();
              Resource resource = new ByteArrayResource(bytes);
              return ResponseEntity.ok()
                  .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                  .contentType(MediaType.parseMediaType(file.getContentType()))
                  .body(resource);
            })
        .orElse(ResponseEntity.notFound().build());
    }

  @GetMapping
  public List<UploadResponse> getAll() {
    return upload.getAll().stream()
            .map(p -> new UploadResponse(
                    p.getId(),
                    p.getContentType(),
                    p.getFilename(),
                    p.getCreateAt()
            )).toList();
  }

  record UploadResponse(String id, String contentType, String fileName, LocalDateTime createdAt) {}
}

//    String id;
//    byte[] file;
//    String contentType;
//    String filename;
//    LocalDateTime createAt;
