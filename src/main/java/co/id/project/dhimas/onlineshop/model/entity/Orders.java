package co.id.project.dhimas.onlineshop.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private int id;

    @Column(nullable = false, unique = true)
    private String orderNo;

    @Column(nullable = false)
    private int itemId;

    @Column(nullable = false)
    private int qty;

    @Column(nullable = false)
    private int price;
}
