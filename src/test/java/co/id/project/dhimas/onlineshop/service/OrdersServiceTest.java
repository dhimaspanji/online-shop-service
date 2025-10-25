package co.id.project.dhimas.onlineshop.service;

import co.id.project.dhimas.onlineshop.exception.GeneralErrorException;
import co.id.project.dhimas.onlineshop.exception.ResourceNotFoundException;
import co.id.project.dhimas.onlineshop.model.entity.Item;
import co.id.project.dhimas.onlineshop.model.entity.Orders;
import co.id.project.dhimas.onlineshop.model.request.OrdersRequest;
import co.id.project.dhimas.onlineshop.model.response.OrdersListResponse;
import co.id.project.dhimas.onlineshop.model.response.OrdersResponse;
import co.id.project.dhimas.onlineshop.repository.ItemRepository;
import co.id.project.dhimas.onlineshop.repository.OrdersRepository;
import co.id.project.dhimas.onlineshop.service.function.StockService;
import co.id.project.dhimas.onlineshop.utils.ErrorType;
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
class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private OrdersService ordersService;

    private Orders order1;
    private Orders order2;
    private Item item1;

    @BeforeEach
    void setup() {
        order1 = Orders.builder()
                .id(1)
                .orderNo("O10")
                .itemId(5)
                .qty(2)
                .price(50)
                .build();

        order2 = Orders.builder()
                .id(2)
                .orderNo("O11")
                .itemId(7)
                .qty(4)
                .price(120)
                .build();

        item1 = Item.builder()
                .id(5)
                .name("Shoe")
                .price(25) // dipakai untuk price kalkulasi qty * price
                .build();
    }

    @Test
    @DisplayName("getOrder(page,size) - should return paged orders")
    void getOrder_list_success() {
        // given
        var pageable = PageRequest.of(0, 2);

        var pageData = new PageImpl<>(
                List.of(order1, order2),
                pageable,
                2
        );

        when(ordersRepository.findAll(pageable)).thenReturn(pageData);

        // when
        OrdersListResponse response = ordersService.getOrder(0, 2);

        // then
        assertThat(response).isNotNull();
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalItems()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);

        assertThat(response.orders()).hasSize(2);

        var first = response.orders().get(0);
        assertThat(first.orderNo()).isEqualTo("O10");
        assertThat(first.itemId()).isEqualTo(5);
        assertThat(first.qty()).isEqualTo(2);
        assertThat(first.price()).isEqualTo(50);

        var second = response.orders().get(1);
        assertThat(second.orderNo()).isEqualTo("O11");
        assertThat(second.itemId()).isEqualTo(7);
        assertThat(second.qty()).isEqualTo(4);
        assertThat(second.price()).isEqualTo(120);

        verify(ordersRepository).findAll(pageable);
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("getOrder(orderNo) - should find order by orderNo (case-insensitive)")
    void getOrder_single_success() {
        // given
        when(ordersRepository.findByOrderNo("O10"))
                .thenReturn(Optional.of(order1));

        // when
        OrdersResponse response = ordersService.getOrder("o10");

        // then
        assertThat(response).isNotNull();
        assertThat(response.orderNo()).isEqualTo("O10");
        assertThat(response.itemId()).isEqualTo(5);
        assertThat(response.qty()).isEqualTo(2);
        assertThat(response.price()).isEqualTo(50);

        verify(ordersRepository).findByOrderNo("O10");
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("getOrder(orderNo) - should throw ResourceNotFoundException if not found")
    void getOrder_single_notFound() {
        // given
        when(ordersRepository.findByOrderNo("O99"))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> ordersService.getOrder("o99"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ordersRepository).findByOrderNo("O99");
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("createOrder - should create order if stock is enough")
    void createOrder_success() {
        // given
        OrdersRequest request = new OrdersRequest(
                5,  // itemId
                2   // qty
        );

        // stok cukup
        when(stockService.remainingStock(5)).thenReturn(10);

        // item ada
        when(itemRepository.findById(5)).thenReturn(Optional.of(item1));

        // last order no in db = "O10"
        when(ordersRepository.findLastOrderNo()).thenReturn("O10");

        // save() kita mock, ga perlu return value dipakai di service
        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        OrdersResponse response = ordersService.createOrder(request);

        // then
        // capture order yang disave
        ArgumentCaptor<Orders> captor = ArgumentCaptor.forClass(Orders.class);
        verify(ordersRepository).save(captor.capture());

        Orders savedOrder = captor.getValue();

        // createOrderNo() must produce O11 kalau last was O10
        assertThat(savedOrder.getOrderNo()).isEqualTo("O11");
        assertThat(savedOrder.getItemId()).isEqualTo(5);
        assertThat(savedOrder.getQty()).isEqualTo(2);
        // price = qty * item.price = 2 * 25
        assertThat(savedOrder.getPrice()).isEqualTo(50);

        // response harus sama persis
        assertThat(response.orderNo()).isEqualTo("O11");
        assertThat(response.itemId()).isEqualTo(5);
        assertThat(response.qty()).isEqualTo(2);
        assertThat(response.price()).isEqualTo(50);

        verify(stockService).remainingStock(5);
        verify(itemRepository).findById(5);
        verify(ordersRepository).findLastOrderNo();
        verify(ordersRepository).save(any(Orders.class));
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("createOrder - should throw GeneralErrorException if stock not enough")
    void createOrder_notEnoughStock() {
        // given
        OrdersRequest request = new OrdersRequest(
                5,
                10 // minta 10
        );

        // stok cuma 3
        when(stockService.remainingStock(5)).thenReturn(3);

        // when / then
        assertThatThrownBy(() -> ordersService.createOrder(request))
                .isInstanceOf(GeneralErrorException.class)
                .satisfies(ex -> {
                    GeneralErrorException gee = (GeneralErrorException) ex;
                    assertThat(gee.getErrorType()).isEqualTo(ErrorType.STOCK_NOT_ENOUGH);
                });

        verify(stockService).remainingStock(5);
        // createOrder harus fail sebelum nge-hit itemRepository/findLastOrderNo/save
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("updateOrder - should update order if stock enough and both order + item exist")
    void updateOrder_success() {
        // given
        OrdersRequest request = new OrdersRequest(
                5, // itemId
                3  // qty
        );

        // stok cukup
        when(stockService.remainingStock(5)).thenReturn(9);

        // order found
        when(ordersRepository.findByOrderNo("O10"))
                .thenReturn(Optional.of(order1));

        // item found
        when(itemRepository.findById(5))
                .thenReturn(Optional.of(item1)); // price=25

        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        OrdersResponse response = ordersService.updateOrder("o10", request);

        // then
        ArgumentCaptor<Orders> captor = ArgumentCaptor.forClass(Orders.class);
        verify(ordersRepository).save(captor.capture());

        Orders saved = captor.getValue();
        // id harus sama dengan order1.id karena kita set id(orders.getId())
        assertThat(saved.getId()).isEqualTo(1);
        assertThat(saved.getOrderNo()).isEqualTo("o10"); // service pake argumen orderNo langsung di builder
        assertThat(saved.getItemId()).isEqualTo(5);
        assertThat(saved.getQty()).isEqualTo(3);
        assertThat(saved.getPrice()).isEqualTo(3 * 25);

        // response harus sesuai object 'order' yang baru
        assertThat(response.orderNo()).isEqualTo("o10");
        assertThat(response.itemId()).isEqualTo(5);
        assertThat(response.qty()).isEqualTo(3);
        assertThat(response.price()).isEqualTo(75);

        verify(stockService).remainingStock(5);
        verify(ordersRepository).findByOrderNo("O10");
        verify(itemRepository).findById(5);
        verify(ordersRepository).save(any(Orders.class));
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("updateOrder - should throw GeneralErrorException if stock not enough")
    void updateOrder_notEnoughStock() {
        // given
        OrdersRequest request = new OrdersRequest(
                5,
                10 // minta 10
        );

        // stok cuma 4
        when(stockService.remainingStock(5)).thenReturn(4);

        // when / then
        assertThatThrownBy(() -> ordersService.updateOrder("O10", request))
                .isInstanceOf(GeneralErrorException.class)
                .satisfies(ex -> {
                    GeneralErrorException gee = (GeneralErrorException) ex;
                    assertThat(gee.getErrorType()).isEqualTo(ErrorType.STOCK_NOT_ENOUGH);
                });

        verify(stockService).remainingStock(5);
        // harus stop sebelum findByOrderNo atau findById item
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("updateOrder - should throw ResourceNotFoundException if order not found")
    void updateOrder_orderNotFound() {
        // given
        OrdersRequest request = new OrdersRequest(
                5,
                2
        );

        when(stockService.remainingStock(5)).thenReturn(10);

        when(ordersRepository.findByOrderNo("O123"))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> ordersService.updateOrder("o123", request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(stockService).remainingStock(5);
        verify(ordersRepository).findByOrderNo("O123");
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("updateOrder - should throw ResourceNotFoundException if item not found")
    void updateOrder_itemNotFound() {
        // given
        OrdersRequest request = new OrdersRequest(
                99,
                2
        );

        when(stockService.remainingStock(99)).thenReturn(10);

        when(ordersRepository.findByOrderNo("O10"))
                .thenReturn(Optional.of(order1));

        // item not found
        when(itemRepository.findById(99))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> ordersService.updateOrder("O10", request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(stockService).remainingStock(99);
        verify(ordersRepository).findByOrderNo("O10");
        verify(itemRepository).findById(99);
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    // -----------------------------------------------------------------------
    // deleteOrder(orderNo)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteOrder - should delete when order exists")
    void deleteOrder_success() {
        // given
        when(ordersRepository.findByOrderNo("O10"))
                .thenReturn(Optional.of(order1));

        // when
        ordersService.deleteOrder("o10");

        // then
        verify(ordersRepository).findByOrderNo("O10");
        verify(ordersRepository).deleteById(1);
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }

    @Test
    @DisplayName("deleteOrder - should throw ResourceNotFoundException when order not found")
    void deleteOrder_notFound() {
        // given
        when(ordersRepository.findByOrderNo("O10"))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> ordersService.deleteOrder("o10"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ordersRepository).findByOrderNo("O10");
        verifyNoMoreInteractions(ordersRepository, itemRepository, stockService);
    }
}
