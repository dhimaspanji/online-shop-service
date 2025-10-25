package co.id.project.dhimas.onlineshop.service;

import co.id.project.dhimas.onlineshop.exception.GeneralErrorException;
import co.id.project.dhimas.onlineshop.exception.ResourceNotFoundException;
import co.id.project.dhimas.onlineshop.model.entity.Inventory;
import co.id.project.dhimas.onlineshop.model.request.InventoryRequest;
import co.id.project.dhimas.onlineshop.model.response.InventoryListResponse;
import co.id.project.dhimas.onlineshop.model.response.InventoryResponse;
import co.id.project.dhimas.onlineshop.repository.InventoryRepository;
import co.id.project.dhimas.onlineshop.utils.ErrorType;
import co.id.project.dhimas.onlineshop.utils.InventoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inv1;
    private Inventory inv2;

    @BeforeEach
    void setup() {
        inv1 = Inventory.builder()
                .id(10)
                .itemId(1)
                .qty(5)
                .type(InventoryType.T)
                .build();

        inv2 = Inventory.builder()
                .id(11)
                .itemId(2)
                .qty(7)
                .type(InventoryType.W)
                .build();
    }

    @Test
    @DisplayName("getInventory(page,size) - returns page of inventories")
    void getInventory_list_success() {
        // given
        var pageable = PageRequest.of(0, 2);
        var pageData = new PageImpl<>(
                List.of(inv1, inv2),
                pageable,
                2
        );

        when(inventoryRepository.findAll(pageable)).thenReturn(pageData);

        // when
        InventoryListResponse response = inventoryService.getInventory(0, 2);

        // then
        assertThat(response).isNotNull();
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalItems()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);

        assertThat(response.inventories()).hasSize(2);

        var first = response.inventories().get(0);
        assertThat(first.id()).isEqualTo(10);
        assertThat(first.itemId()).isEqualTo(1);
        assertThat(first.qty()).isEqualTo(5);
        assertThat(first.type()).isEqualTo("T");

        var second = response.inventories().get(1);
        assertThat(second.id()).isEqualTo(11);
        assertThat(second.itemId()).isEqualTo(2);
        assertThat(second.qty()).isEqualTo(7);
        assertThat(second.type()).isEqualTo("W");

        verify(inventoryRepository).findAll(pageable);
        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    @DisplayName("getInventory(id) - success returns inventory response")
    void getInventory_single_success() {
        // given
        when(inventoryRepository.findById(10)).thenReturn(Optional.of(inv1));

        // when
        InventoryResponse response = inventoryService.getInventory(10);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10);
        assertThat(response.itemId()).isEqualTo(1);
        assertThat(response.qty()).isEqualTo(5);
        assertThat(response.type()).isEqualTo("T");

        verify(inventoryRepository).findById(10);
        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    @DisplayName("getInventory(id) - throws ResourceNotFoundException if not found")
    void getInventory_single_notFound() {
        // given
        when(inventoryRepository.findById(999)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> inventoryService.getInventory(999))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(inventoryRepository).findById(999);
        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    @DisplayName("createInventory - saves new inventory and returns response")
    void createInventory_success() {
        // given
        InventoryRequest request = new InventoryRequest(
                7,          // itemId
                20,         // qty
                "T"         // type
        );

        Inventory saved = Inventory.builder()
                .id(123)
                .itemId(7)
                .qty(20)
                .type(InventoryType.T)
                .build();

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        // when
        InventoryResponse response = inventoryService.createInventory(request);

        // then
        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(captor.capture());

        Inventory toSave = captor.getValue();
        assertThat(toSave.getId()).isEqualTo(0); // builder tanpa id -> default 0 (int)
        assertThat(toSave.getItemId()).isEqualTo(7);
        assertThat(toSave.getQty()).isEqualTo(20);
        assertThat(toSave.getType()).isEqualTo(InventoryType.T);

        assertThat(response.id()).isEqualTo(123);
        assertThat(response.itemId()).isEqualTo(7);
        assertThat(response.qty()).isEqualTo(20);
        assertThat(response.type()).isEqualTo("T");

        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    @DisplayName("updateInventory - T (Top Up) just saves new value and returns response")
    void updateInventory_success_topUp() {
        // given
        int id = 50;
        InventoryRequest request = new InventoryRequest(
                99,     // itemId
                10,     // qty
                "T"     // type
        );

        // kalau type T, logic nggak cek stok sama sekali
        // tapi service tetap call getStock(), jadi kita boleh stub seadanya
        when(inventoryRepository.getStock(99)).thenReturn(999);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        InventoryResponse response = inventoryService.updateInventory(id, request);

        // then
        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).getStock(99);
        verify(inventoryRepository).save(captor.capture());

        Inventory savedInv = captor.getValue();
        assertThat(savedInv.getId()).isEqualTo(50);
        assertThat(savedInv.getItemId()).isEqualTo(99);
        assertThat(savedInv.getQty()).isEqualTo(10);
        assertThat(savedInv.getType()).isEqualTo(InventoryType.T);

        assertThat(response.id()).isEqualTo(50);
        assertThat(response.itemId()).isEqualTo(99);
        assertThat(response.qty()).isEqualTo(10);
        assertThat(response.type()).isEqualTo("T");

        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    @DisplayName("updateInventory - W (Withdrawal) with ENOUGH stock should save")
    void updateInventory_success_withdrawal_enoughStock() {
        // given
        int id = 77;
        InventoryRequest request = new InventoryRequest(
                5,      // itemId
                3,      // qty (requested withdraw)
                "w"     // type lowercase, will toUpperCase() in service
        );

        // stok cukup (5)
        when(inventoryRepository.getStock(5)).thenReturn(5);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        InventoryResponse response = inventoryService.updateInventory(id, request);

        // then
        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).getStock(5);
        verify(inventoryRepository).save(captor.capture());

        Inventory savedInv = captor.getValue();
        assertThat(savedInv.getId()).isEqualTo(77);
        assertThat(savedInv.getItemId()).isEqualTo(5);
        assertThat(savedInv.getQty()).isEqualTo(3);
        assertThat(savedInv.getType()).isEqualTo(InventoryType.W);

        assertThat(response.id()).isEqualTo(77);
        assertThat(response.itemId()).isEqualTo(5);
        assertThat(response.qty()).isEqualTo(3);
        assertThat(response.type()).isEqualTo("W");

        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    @DisplayName("updateInventory - W (Withdrawal) with ZERO stock throws GeneralErrorException")
    void updateInventory_withdrawal_zeroStock_throws() {
        // given
        InventoryRequest request = new InventoryRequest(
                9,      // itemId
                2,      // qty
                "W"     // withdraw
        );

        when(inventoryRepository.getStock(9)).thenReturn(0);

        // when / then
        assertThatThrownBy(() -> inventoryService.updateInventory(99, request))
                .isInstanceOf(GeneralErrorException.class)
                .satisfies(ex -> {
                    GeneralErrorException gee = (GeneralErrorException) ex;
                    assertThat(gee.getErrorType()).isEqualTo(ErrorType.STOCK_NOT_ENOUGH);
                });

        verify(inventoryRepository).getStock(9);
        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    @DisplayName("updateInventory - W (Withdrawal) with INSUFFICIENT stock throws GeneralErrorException")
    void updateInventory_withdrawal_notEnoughStock_throws() {
        // given
        InventoryRequest request = new InventoryRequest(
                9,      // itemId
                10,     // qty request
                "W"     // withdraw
        );

        // stok cuma 5 < qty 10
        when(inventoryRepository.getStock(9)).thenReturn(5);

        // when / then
        assertThatThrownBy(() -> inventoryService.updateInventory(999, request))
                .isInstanceOf(GeneralErrorException.class)
                .satisfies(ex -> {
                    GeneralErrorException gee = (GeneralErrorException) ex;
                    assertThat(gee.getErrorType()).isEqualTo(ErrorType.STOCK_NOT_ENOUGH);
                });

        verify(inventoryRepository).getStock(9);
        verifyNoMoreInteractions(inventoryRepository);
    }

    // -----------------------------------------------------------------------
    // deleteInventory
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteInventory - delete when exists")
    void deleteInventory_success() {
        when(inventoryRepository.findById(10)).thenReturn(Optional.of(inv1));

        inventoryService.deleteInventory(10);

        verify(inventoryRepository).findById(10);
        verify(inventoryRepository).deleteById(10);
        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    @DisplayName("deleteInventory - throws ResourceNotFoundException when not exists")
    void deleteInventory_notFound() {
        when(inventoryRepository.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.deleteInventory(404))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(inventoryRepository).findById(404);
        verifyNoMoreInteractions(inventoryRepository);
    }
}
