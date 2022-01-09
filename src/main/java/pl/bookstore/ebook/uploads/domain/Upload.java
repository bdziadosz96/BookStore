package pl.bookstore.ebook.uploads.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.bookstore.ebook.jpa.BaseEntity;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Upload extends BaseEntity {
    private byte[] file;
    private String contentType;
    private String filename;
    @CreatedDate private LocalDateTime createAt;

    public Upload(final String filename, final String contentType, final byte[] file) {
        this.file = file;
        this.contentType = contentType;
        this.filename = filename;
    }
}
