package pl.bookstore.ebook.user.web;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bookstore.ebook.user.application.port.UserRegistrationUseCase;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserRegistrationUseCase registrationUseCase;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        return registrationUseCase
                .register(command.username, command.password)
                .handle(
                        cmd -> ResponseEntity.accepted().build(),
                        failCmd -> ResponseEntity.badRequest().body(failCmd));
    }

    @Data
    static class RegisterCommand {
        @Email String username;

        @Size(min = 3, max = 20)
        String password;
    }
}
