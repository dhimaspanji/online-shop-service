package co.id.project.dhimas.onlineshop.repository;

import co.id.project.dhimas.onlineshop.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
}
