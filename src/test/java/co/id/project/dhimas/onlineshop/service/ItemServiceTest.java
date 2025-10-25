package co.id.project.dhimas.onlineshop.service;

import co.id.project.dhimas.onlineshop.exception.ResourceNotFoundException;
import co.id.project.dhimas.onlineshop.model.entity.Item;
import co.id.project.dhimas.onlineshop.model.request.ItemRequest;
import co.id.project.dhimas.onlineshop.model.response.ItemListResponse;
import co.id.project.dhimas.onlineshop.model.response.ItemResponse;
import co.id.project.dhimas.onlineshop.repository.ItemRepository;
import co.id.project.dhimas.onlineshop.service.function.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private ItemService itemService;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setup() {
        item1 = Item.builder()
                .id(1)
                .name("Pen")
                .price(5)
                .build();

        item2 = Item.builder()
                .id(2)
                .name("Book")
                .price(10)
                .build();
    }

    @Test
    @DisplayName("getItem(page,size) - should return paged items with remaining stock")
    void getItem_list_success() {
        // given
        PageRequest pageable = PageRequest.of(0, 2);
        Page<Item> pageData = new PageImpl<>(
                List.of(item1, item2),
                pageable,
                2
        );

        when(itemRepository.findAll(pageable)).thenReturn(pageData);
        when(stockService.remainingStock(1)).thenReturn(100);
        when(stockService.remainingStock(2)).thenReturn(50);

        // when
        ItemListResponse result = itemService.getItem(0, 2);

        // then
        assertThat(result).isNotNull();
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.totalItems()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);

        assertThat(result.items()).hasSize(2);

        var first = result.items().get(0);
        assertThat(first.id()).isEqualTo(1);
        assertThat(first.name()).isEqualTo("Pen");
        assertThat(first.price()).isEqualTo(5);
        assertThat(first.remainingStock()).isEqualTo(100);

        var second = result.items().get(1);
        assertThat(second.id()).isEqualTo(2);
        assertThat(second.name()).isEqualTo("Book");
        assertThat(second.price()).isEqualTo(10);
        assertThat(second.remainingStock()).isEqualTo(50);

        verify(itemRepository).findAll(pageable);
        verify(stockService).remainingStock(1);
        verify(stockService).remainingStock(2);
        verifyNoMoreInteractions(itemRepository, stockService);
    }

    @Test
    @DisplayName("getItem(id) - should return single item response with remaining stock")
    void getItem_single_success() {
        // given
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(stockService.remainingStock(1)).thenReturn(77);

        // when
        ItemResponse result = itemService.getItem(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Pen");
        assertThat(result.price()).isEqualTo(5);
        assertThat(result.remainingStock()).isEqualTo(77);

        verify(itemRepository).findById(1);
        verify(stockService).remainingStock(1);
        verifyNoMoreInteractions(itemRepository, stockService);
    }

    @Test
    @DisplayName("getItem(id) - should throw ResourceNotFoundException when item missing")
    void getItem_single_notFound() {
        // given
        when(itemRepository.findById(999)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> itemService.getItem(999))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(itemRepository).findById(999);
        verifyNoMoreInteractions(itemRepository, stockService);
    }

    @Test
    @DisplayName("createItem - should save and return created item")
    void createItem_success() {
        // given
        ItemRequest request = new ItemRequest("Marker", 15);

        Item saved = Item.builder()
                .id(10)
                .name("Marker")
                .price(15)
                .build();

        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        // when
        ItemResponse response = itemService.createItem(request);

        // then
        // verify save() got the right content
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());
        Item toSave = captor.getValue();

        assertThat(toSave.getId()).isEqualTo(0); // builder() without id -> default int 0
        assertThat(toSave.getName()).isEqualTo("Marker");
        assertThat(toSave.getPrice()).isEqualTo(15);

        // verify response
        assertThat(response.id()).isEqualTo(10);
        assertThat(response.name()).isEqualTo("Marker");
        assertThat(response.price()).isEqualTo(15);

        verifyNoMoreInteractions(itemRepository, stockService);
    }

    @Test
    @DisplayName("updateItem - should update existing item and return new values")
    void updateItem_success() {
        // given
        int id = 1;
        ItemRequest request = new ItemRequest("Updated Pen", 99);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item1));

        // Mockito doesn't care about return of save in service code,
        // but let's still stub to be safe/explicit:
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ItemResponse result = itemService.updateItem(id, request);

        // then
        // verify that it first checks existence
        verify(itemRepository).findById(id);

        // capture what was saved
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());
        Item savedItem = captor.getValue();

        assertThat(savedItem.getId()).isEqualTo(id);
        assertThat(savedItem.getName()).isEqualTo("Updated Pen");
        assertThat(savedItem.getPrice()).isEqualTo(99);

        // verify response matches saved data
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Updated Pen");
        assertThat(result.price()).isEqualTo(99);

        verifyNoMoreInteractions(itemRepository, stockService);
    }

    @Test
    @DisplayName("updateItem - should throw ResourceNotFoundException if item not found")
    void updateItem_notFound() {
        // given
        int id = 404;
        ItemRequest request = new ItemRequest("Ghost", 999);

        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> itemService.updateItem(id, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(itemRepository).findById(id);
        verifyNoMoreInteractions(itemRepository, stockService);
    }

    @Test
    @DisplayName("deleteItem - should delete if item exists")
    void deleteItem_success() {
        // given
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        // when
        itemService.deleteItem(1);

        // then
        verify(itemRepository).findById(1);
        verify(itemRepository).deleteById(1);
        verifyNoMoreInteractions(itemRepository, stockService);
    }

    @Test
    @DisplayName("deleteItem - should throw ResourceNotFoundException if item not exists")
    void deleteItem_notFound() {
        // given
        when(itemRepository.findById(123)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> itemService.deleteItem(123))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(itemRepository).findById(123);
        verifyNoMoreInteractions(itemRepository, stockService);
    }
}
