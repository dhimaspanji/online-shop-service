package co.id.project.dhimas.onlineshop.service;

import co.id.project.dhimas.onlineshop.exception.GeneralErrorException;
import co.id.project.dhimas.onlineshop.exception.ResourceNotFoundException;
import co.id.project.dhimas.onlineshop.model.entity.Orders;
import co.id.project.dhimas.onlineshop.model.request.OrdersRequest;
import co.id.project.dhimas.onlineshop.model.response.*;
import co.id.project.dhimas.onlineshop.repository.ItemRepository;
import co.id.project.dhimas.onlineshop.repository.OrdersRepository;
import co.id.project.dhimas.onlineshop.service.function.StockService;
import co.id.project.dhimas.onlineshop.utils.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final ItemRepository itemRepository;
    private final StockService stockService;

    public OrdersListResponse getOrder(int page, int size){
        // get all orders
        var orders = ordersRepository.findAll(PageRequest.of(page, size));

        var listOrder = orders.getContent().stream()
                .map(this::mapListOrder)
                .toList();

        return OrdersListResponseBuilder.builder()
                .orders(listOrder)
                .page(page)
                .size(size)
                .totalItems(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .build();
    }

    public OrdersResponse getOrder(String orderNo){
        // get order
        var orders = ordersRepository.findByOrderNo(orderNo.toUpperCase())
                .orElseThrow(ResourceNotFoundException::new);

        return OrdersResponseBuilder.builder()
                .orderNo(orders.getOrderNo())
                .itemId(orders.getItemId())
                .qty(orders.getQty())
                .price(orders.getPrice())
                .build();
    }

    public OrdersResponse createOrder(OrdersRequest request) {
        var stock = stockService.remainingStock(request.itemId());

        if (stock < request.qty()) {
            throw new GeneralErrorException(ErrorType.STOCK_NOT_ENOUGH);
        }

        var item = itemRepository.findById(request.itemId())
                .orElseThrow(ResourceNotFoundException::new);

        var order = Orders.builder()
                .orderNo(createOrderNo())
                .itemId(request.itemId())
                .qty(request.qty())
                .price(request.qty() * item.getPrice())
                .build();

        ordersRepository.save(order);

        return OrdersResponseBuilder.builder()
                .orderNo(order.getOrderNo())
                .itemId(order.getItemId())
                .qty(order.getQty())
                .price(order.getPrice())
                .build();
    }

    public OrdersResponse updateOrder(String orderNo, OrdersRequest request) {
        var stock = stockService.remainingStock(request.itemId());

        if (stock < request.qty()) {
            throw new GeneralErrorException(ErrorType.STOCK_NOT_ENOUGH);
        }

        var orders = ordersRepository.findByOrderNo(orderNo.toUpperCase())
                .orElseThrow(ResourceNotFoundException::new);

        var item = itemRepository.findById(request.itemId())
                .orElseThrow(ResourceNotFoundException::new);

        var order = Orders.builder()
                .id(orders.getId())
                .orderNo(orderNo)
                .itemId(request.itemId())
                .qty(request.qty())
                .price(request.qty() * item.getPrice())
                .build();

        ordersRepository.save(order);

        return OrdersResponseBuilder.builder()
                .orderNo(order.getOrderNo())
                .itemId(order.getItemId())
                .qty(order.getQty())
                .price(order.getPrice())
                .build();
    }

    public void deleteOrder(String orderNo) {
        ordersRepository.findByOrderNo(orderNo.toUpperCase())
                .ifPresentOrElse(
                        data -> ordersRepository.deleteById(data.getId()),
                        () -> {
                            throw new ResourceNotFoundException();
                        }
                );
    }

    private OrdersListResponse.Orders mapListOrder(Orders o) {
        return OrdersListResponseOrdersBuilder.builder()
                .orderNo(o.getOrderNo())
                .itemId(o.getItemId())
                .qty(o.getQty())
                .price(o.getPrice())
                .build();
    }

    private String createOrderNo() {
        String lastOrderNo = ordersRepository.findLastOrderNo();
        int nextNumber = Integer.parseInt(lastOrderNo.substring(1)) + 1;

        return "O" + nextNumber;
    }
}
