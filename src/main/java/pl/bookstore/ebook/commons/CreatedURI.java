package pl.bookstore.ebook.commons;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


public record CreatedURI(String path) {
  public URI uri() {
    return ServletUriComponentsBuilder.fromCurrentRequestUri().path(path).build().toUri();
  }
}
