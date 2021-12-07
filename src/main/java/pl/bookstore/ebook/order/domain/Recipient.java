package pl.bookstore.ebook.order.domain;

import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.bookstore.ebook.jpa.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Recipient extends BaseEntity {
  private String name;
  private String phone;
  private String street;
  private String city;
  private String zipCode;
  private String email;
}
