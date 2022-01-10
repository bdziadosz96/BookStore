package pl.bookstore.ebook.order.web;

import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.bookstore.ebook.commons.CreatedURI;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderCommand;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.OrderDto;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.security.UserSecurity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.UpdateOrderStatusCommand;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
class OrderController {
    private final QueryOrderUseCase queryOrder;
    private final ManageOrderUseCase manageOrder;
    private final UserSecurity userSecurity;

    @GetMapping
    @ResponseStatus(OK)
    @Secured({"ROLE_ADMIN"})
    public List<OrderDto> getAll() {
        return queryOrder.findAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<?> getById(@PathVariable final Long id, @AuthenticationPrincipal User user) {
        return queryOrder
                .findById(id)
                .map(order -> authorize(user,order))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<OrderDto> authorize(User user, OrderDto orderDto) {
        if (userSecurity.isOwnerOrAdmin(orderDto.getRecipient().getEmail(), user)) {
            return ResponseEntity.ok(orderDto);
        }
        return ResponseEntity.status(FORBIDDEN).build();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<?> createOrder(@RequestBody PlaceOrderCommand command) {
        return manageOrder
                .placeOrder(command)
                .handle(
                        orderId -> ResponseEntity.created(orderUri(orderId)).build(),
                        error -> ResponseEntity.badRequest().body(error));
    }

    @PatchMapping("/{id}/status")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable final Long id, @RequestBody final Map<String, String> body, @AuthenticationPrincipal User user) {
        String status = body.get("status");
        final OrderStatus orderStatus =
                OrderStatus.checkString(status)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                BAD_REQUEST, "Unkown status: " + status));
        UpdateOrderStatusCommand command =
                new UpdateOrderStatusCommand(id, orderStatus, user);
        return manageOrder
                .updateOrderStatus(command)
                .handle(
                        orderId -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.status(error.getStatus()).build());
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable final Long id) {
        manageOrder.deleteOrderById(id);
    }

    private URI orderUri(final Long orderId) {
        return new CreatedURI("/" + orderId).uri();
    }
}
