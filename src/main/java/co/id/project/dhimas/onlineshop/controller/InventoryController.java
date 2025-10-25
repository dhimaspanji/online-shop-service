package co.id.project.dhimas.onlineshop.controller;

import co.id.project.dhimas.onlineshop.model.request.InventoryRequest;
import co.id.project.dhimas.onlineshop.model.response.InventoryListResponse;
import co.id.project.dhimas.onlineshop.model.response.InventoryResponse;
import co.id.project.dhimas.onlineshop.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public InventoryListResponse getAllInventories(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "2") int size
    ) {
        return inventoryService.getInventory(page, size);
    }

    @GetMapping("/{id}")
    public InventoryResponse getInventory(@PathVariable int id) {
        return inventoryService.getInventory(id);
    }

    @PostMapping
    public InventoryResponse createInventory(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.createInventory(request);
    }

    @PutMapping("/{id}")
    public InventoryResponse updateInventory(@PathVariable int id, @Valid @RequestBody InventoryRequest request) {
        return inventoryService.updateInventory(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable int id) {
        inventoryService.deleteInventory(id);
    }
}
