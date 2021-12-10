package pl.bookstore.ebook.commons;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Either<L, R> {
  private final boolean success;
  private final L left;
  private final R right;

  public <T> T handle(final Function<R, T> onSuccess, final Function<L, T> onError) {
    if (success) {
      return onSuccess.apply(right);
    } else {
      return onError.apply(left);
    }
  }
}
