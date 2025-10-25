package co.id.project.dhimas.onlineshop.service.function;

import co.id.project.dhimas.onlineshop.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public int remainingStock(int itemId) {
        return inventoryRepository.getStock(itemId);
    }
}
