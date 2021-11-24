package pl.bookstore.ebook.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderCommand;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderResponse;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.Order;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.Recipient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

import static pl.bookstore.ebook.order.app.port.ManageOrderUseCase.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
class OrderController {
  private final QueryOrderUseCase queryOrder;
  private final ManageOrderUseCase manageOrder;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Order> getAll() {
    return queryOrder.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    return queryOrder
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> addOrder(@RequestBody PlaceOrderCommand command) {
    final PlaceOrderResponse response = manageOrder.placeOrder(command);
    return ResponseEntity.created(URI.create(response.toString())).build();
  }

  @PutMapping("/{id}/status")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void updateOrderStatus(
      @PathVariable Long id, @RequestBody RestOrderCommand command) {
    final UpdateOrderStatusResponse response = manageOrder.updateOrderStatus(command.toUpdateStatus(id));
    if (response.isSuccess()) {
      String message = String.join(",", response.getErrors());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
  }

  @Data
  private static class RestOrderCommand {
    @NotNull private Long id;
    private List<OrderItem> items;
    private Recipient recipient;
    @NotBlank private String status;

    UpdateOrderStatusCommand toUpdateStatus(Long id) {
      return new UpdateOrderStatusCommand(id, status);
    }
  }
}
