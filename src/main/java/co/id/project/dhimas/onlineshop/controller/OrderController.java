package co.id.project.dhimas.onlineshop.controller;

import co.id.project.dhimas.onlineshop.model.request.OrdersRequest;
import co.id.project.dhimas.onlineshop.model.response.OrdersListResponse;
import co.id.project.dhimas.onlineshop.model.response.OrdersResponse;
import co.id.project.dhimas.onlineshop.service.OrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrdersService ordersService;

    @GetMapping
    public OrdersListResponse getAllOrders(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "2") int size
    ) {
        return ordersService.getOrder(page, size);
    }

    @GetMapping("/{orderNo}")
    public OrdersResponse getOrders(@PathVariable String orderNo) {
        return ordersService.getOrder(orderNo);
    }

    @PostMapping
    public OrdersResponse createOrder(@Valid @RequestBody OrdersRequest ordersRequest) {
        return ordersService.createOrder(ordersRequest);
    }

    @PutMapping("/{orderNo}")
    public OrdersResponse updateOrder(@PathVariable String orderNo, @Valid @RequestBody OrdersRequest ordersRequest) {
        return ordersService.updateOrder(orderNo, ordersRequest);
    }

    @DeleteMapping("/{orderNo}")
    public void deleteOrder(@PathVariable String orderNo) {
        ordersService.deleteOrder(orderNo);
    }
}
