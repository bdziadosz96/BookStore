package pl.bookstore.ebook.order.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
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
@Generated
public class Recipient extends BaseEntity {
    @Column(unique = true)
    private String email;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
}
