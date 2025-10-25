package co.id.project.dhimas.onlineshop.service;

import co.id.project.dhimas.onlineshop.exception.GeneralErrorException;
import co.id.project.dhimas.onlineshop.exception.ResourceNotFoundException;
import co.id.project.dhimas.onlineshop.model.entity.Inventory;
import co.id.project.dhimas.onlineshop.model.request.InventoryRequest;
import co.id.project.dhimas.onlineshop.model.response.*;
import co.id.project.dhimas.onlineshop.repository.InventoryRepository;
import co.id.project.dhimas.onlineshop.utils.ErrorType;
import co.id.project.dhimas.onlineshop.utils.InventoryType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryListResponse getInventory(int page, int size) {
        // get all inventories
        var inventories = inventoryRepository.findAll(PageRequest.of(page, size));

        var listInventories = inventories.getContent().stream()
                .map(this::mapListInventories)
                .toList();

        return InventoryListResponseBuilder.builder()
                .inventories(listInventories)
                .page(page)
                .size(size)
                .totalItems(inventories.getTotalElements())
                .totalPages(inventories.getTotalPages())
                .build();
    }

    public InventoryResponse getInventory(int id){
        // get inventory
        var inventory = inventoryRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return InventoryResponseBuilder.builder()
                .id(inventory.getId())
                .itemId(inventory.getItemId())
                .qty(inventory.getQty())
                .type(inventory.getType().name())
                .build();
    }

    public InventoryResponse createInventory(InventoryRequest request) {
        var inventory = Inventory.builder()
                .itemId(request.itemId())
                .qty(request.qty())
                .type(InventoryType.valueOf(request.type()))
                .build();

        var saveInventory = inventoryRepository.save(inventory);

        return InventoryResponseBuilder.builder()
                .id(saveInventory.getId())
                .itemId(saveInventory.getItemId())
                .qty(saveInventory.getQty())
                .type(saveInventory.getType().name())
                .build();
    }

    public InventoryResponse updateInventory(int id, InventoryRequest request) {
        var stock = inventoryRepository.getStock(request.itemId());

        var type = InventoryType.valueOf(request.type().toUpperCase());

        if (StringUtils.equals(InventoryType.W.name(), type.name()) &&
                (stock == 0 || stock < request.qty())) {
            throw new GeneralErrorException(ErrorType.STOCK_NOT_ENOUGH);
        }

        var newValueInventory = Inventory.builder()
                .id(id)
                .itemId(request.itemId())
                .qty(request.qty())
                .type(type)
                .build();

        inventoryRepository.save(newValueInventory);

        return InventoryResponseBuilder.builder()
                .id(newValueInventory.getId())
                .itemId(newValueInventory.getItemId())
                .qty(newValueInventory.getQty())
                .type(newValueInventory.getType().name())
                .build();
    }

    public void deleteInventory(int id) {
        inventoryRepository.findById(id)
                .ifPresentOrElse(
                        data -> inventoryRepository.deleteById(data.getId()),
                        () -> {
                            throw new ResourceNotFoundException();
                        }
                );
    }

    private InventoryListResponse.Inventory mapListInventories(Inventory i) {
        return InventoryListResponseInventoryBuilder.builder()
                .id(i.getId())
                .itemId(i.getItemId())
                .qty(i.getQty())
                .type(i.getType().name())
                .build();
    }
}
