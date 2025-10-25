package co.id.project.dhimas.onlineshop.controller;

import co.id.project.dhimas.onlineshop.model.request.ItemRequest;
import co.id.project.dhimas.onlineshop.model.response.ItemListResponse;
import co.id.project.dhimas.onlineshop.model.response.ItemResponse;
import co.id.project.dhimas.onlineshop.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ItemListResponse getAllItems(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "2") int size
    ) {
        return itemService.getItem(page, size);
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable int id) {
        return itemService.getItem(id);
    }

    @PostMapping
    public ItemResponse createItem(@Valid @RequestBody ItemRequest request) {
        return itemService.createItem(request);
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@PathVariable int id, @Valid @RequestBody ItemRequest request) {
        return itemService.updateItem(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable int id) {
        itemService.deleteItem(id);
    }
}
