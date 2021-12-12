package pl.bookstore.ebook.commons;

import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@AllArgsConstructor
public class CreatedURI {
  private final String path;

  public URI uri() {
    return ServletUriComponentsBuilder.fromCurrentRequestUri().path(path).build().toUri();
  }
}
