package co.id.project.dhimas.onlineshop.repository;

import co.id.project.dhimas.onlineshop.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    @Query("""
            SELECT coalesce(sum(case when i.type = 'T' then i.qty else -i.qty end),0)
            FROM Inventory i
            WHERE i.itemId = :itemId
    """)
    Integer getStock(@Param("itemId") int itemId);
}
