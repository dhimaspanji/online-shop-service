package co.id.project.dhimas.onlineshop.service;

import co.id.project.dhimas.onlineshop.exception.ResourceNotFoundException;
import co.id.project.dhimas.onlineshop.model.entity.Item;
import co.id.project.dhimas.onlineshop.model.request.ItemRequest;
import co.id.project.dhimas.onlineshop.model.response.*;
import co.id.project.dhimas.onlineshop.repository.ItemRepository;
import co.id.project.dhimas.onlineshop.service.function.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final StockService stockService;

    public ItemListResponse getItem(int page, int size) {
        // get all items
        var items = itemRepository.findAll(PageRequest.of(page, size));

        var listItem = items.getContent().stream()
                .map(this::getRemainingStock)
                .toList();

        return ItemListResponseBuilder.builder()
                .items(listItem)
                .page(page)
                .size(size)
                .totalItems(items.getTotalElements())
                .totalPages(items.getTotalPages())
                .build();
    }

    public ItemResponse getItem(int id){
        // get item
        var item = itemRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        var stock = stockService.remainingStock(id);

        return ItemResponseBuilder.builder()
                .id(id)
                .name(item.getName())
                .price(item.getPrice())
                .remainingStock(stock)
                .build();
    }

    public ItemResponse createItem(ItemRequest request) {
        var item = Item.builder()
                .name(request.name())
                .price(request.price())
                .build();

        var saveItem = itemRepository.save(item);

        return ItemResponseBuilder.builder()
                .id(saveItem.getId())
                .name(saveItem.getName())
                .price(saveItem.getPrice())
                .build();
    }

    public ItemResponse updateItem(int id, ItemRequest request) {
        itemRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        var newValueItem = Item.builder()
                .id(id)
                .name(request.name())
                .price(request.price())
                .build();

        itemRepository.save(newValueItem);

        return ItemResponseBuilder.builder()
                .id(newValueItem.getId())
                .name(newValueItem.getName())
                .price(newValueItem.getPrice())
                .build();
    }

    public void deleteItem(int id) {
        itemRepository.findById(id)
                .ifPresentOrElse(
                        data -> itemRepository.deleteById(data.getId()),
                        () -> {
                            throw new ResourceNotFoundException();
                        }
                );
    }

    private ItemListResponse.Item getRemainingStock(Item i) {
        int stock = stockService.remainingStock(i.getId());

        return ItemListResponseItemBuilder.builder()
                .id(i.getId())
                .name(i.getName())
                .price(i.getPrice())
                .remainingStock(stock)
                .build();
    }
}
