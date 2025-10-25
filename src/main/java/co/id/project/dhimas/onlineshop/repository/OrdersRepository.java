package co.id.project.dhimas.onlineshop.repository;

import co.id.project.dhimas.onlineshop.model.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {

    Optional<Orders> findByOrderNo(String orderNo);

    @Query("SELECT o.orderNo FROM Orders o ORDER BY o.id DESC LIMIT 1")
    String findLastOrderNo();
}
