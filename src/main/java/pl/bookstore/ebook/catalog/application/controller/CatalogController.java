package pl.bookstore.ebook.catalog.application.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pl.bookstore.ebook.catalog.domain.model.Book;
import pl.bookstore.ebook.catalog.application.service.CatalogService;

@Controller
@RequiredArgsConstructor
public class CatalogController {
  private final CatalogService service;

  public List<Book> findByTitle(String title) {
    return service.findByTitle(title);
  }
}
