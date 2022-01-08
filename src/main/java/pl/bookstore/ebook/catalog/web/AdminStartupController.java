package pl.bookstore.ebook.catalog.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bookstore.ebook.catalog.app.port.CatalogInitializerUseCase;

@Slf4j
@RequestMapping("/admin")
@RestController
@AllArgsConstructor
class AdminStartupController {
    CatalogInitializerUseCase initializer;

    @PostMapping("/initialize")
    public void initialize() {
        initializer.load();
    }
}
