package com.example.store.mapper;

import com.example.store.dto.user.CompanyShortDTO;
import com.example.store.dto.order.*;
import com.example.store.dto.user.*;
import com.example.store.model.*;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.OrderStatusRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.webclient.WebClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.store.dto.order.OrderUserDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderMapper {

    private final WebClientService webClientService;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderStatusRepository orderStatusRepository;

    public OrderUserDTO mapOrderToOrderUserDTO(Order order) {
        LocalDateTime sentDate = null;

        Optional<OrderStatus> second = order.getStatuses()
                .stream()
                .filter(status -> status.getStatus().equals(Status.COMPLETED)).findFirst();

        LocalDateTime completeDate = null;
        if (second.isPresent()) {
            completeDate = second.get().getTime();
        }

        Optional<OrderStatus> first = order.getStatuses()
                .stream()
                .filter(status -> status.getStatus().equals(Status.SENT)).findFirst();

        if (first.isPresent()) {
            sentDate = first.get().getTime();
        }

        return new OrderUserDTO(
                order.getId(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                order.getOrderItem().stream().mapToInt(OrderItem::getQuantity).sum(),
                order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                order.getOrderItem().stream().map(this::mapOrderItemToOrderItemUserDTO).toList(),
                sentDate,
                completeDate,
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                order.getZipcode(),
                order.getPhone()
        );
    }

    public OrderCompanyShortDTO mapOrderToOrderCompanyShortDTO(Order order, Long userId, String authToken) {
        UserDTO user = webClientService.getUser(authToken, order.getUserid());
        Long companyId = webClientService.findCompanyIdByUserId(userId);

        List<OrderItem> items = order.getOrderItem().stream().filter(item -> item.getProduct().getCompanyid() == companyId).toList();

            return new OrderCompanyShortDTO(
                order.getId(),
                items.stream().mapToInt(OrderItem::getQuantity).sum(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                items.stream().map(orderItem -> orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                items.get(0).getStatuses()
                        .stream()
                        .max(Comparator.comparing(OrderStatus::getTime))
                        .orElseThrow(() -> new NoSuchElementException("No status found")).getStatus(),
                user.firstName(),
                user.lastName(),
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                items.stream().mapToInt(OrderItem::getQuantity).sum(),
                order.getUserid()
        );
    }

    public OrderItemDTO mapOrderItemToOrderItemDto(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getSize(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getParameters().stream().map(Parameter::getValue).toList()
        );
    }


    public OrderAdminShortDTO mapOrderToOrderAdminShortDTO(Order order) {
        String userName = webClientService.findUserName(order.getUserid());
        List<String> companies = order.getOrderItem().stream().map(item -> item.getProduct().getCompanyid()).distinct().toList().stream().map(asd -> webClientService.findCompanyName(asd).name()).toList();

        return new OrderAdminShortDTO(
                order.getId(),
                userName,
                order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                order.getStatuses()
                        .stream()
                        .max(Comparator.comparing(OrderStatus::getStatus)).orElseThrow(() -> new NoSuchElementException("No status found")).getStatus(),
                companies,
                order.getCity() + " " + order.getStreet() + " " + order.getHouse_number(),
                order.getOrderItem().stream().mapToInt(OrderItem::getQuantity).sum()
        );
    }

    public OrderStatusDTO mapOrderStatusToOrderStatusDTO(OrderStatus orderStatus) {
        return new OrderStatusDTO(orderStatus.getStatus(), orderStatus.getTime());
    }

    public OrderAdminLongDTO mapOrderToOrderAdminLongDTO(Order order) {
        UserDTO user = webClientService.findUser(Long.valueOf(order.getUserid()));

        return new OrderAdminLongDTO(
                order.getId(),
                order.getStatuses().stream().map(this::mapOrderStatusToOrderStatusDTO).sorted(Comparator.comparing(OrderStatusDTO::time)).toList(),
                order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                order.getUserid(),
                user.firstName(),
                user.lastName(),
                user.username(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                order.getZipcode(),
                order.getPhone(),
                mapOrderItemsToCompanyItemsDTO(order.getOrderItem()),
                order.getOrderItem().stream().mapToInt(OrderItem::getQuantity).sum()
        );
    }

    public List<CompanyItemsDTO> mapOrderItemsToCompanyItemsDTO(List<OrderItem> orderItems) {
        return orderItems.stream()
                .collect(Collectors.groupingBy(orderItem -> webClientService.findCompanyName(orderItem.getProduct().getCompanyid()).name()))
                .entrySet().stream()
                .map(entry -> {
                    String companyName = entry.getKey();
                    List<OrderItem> items = entry.getValue();

                    return new CompanyItemsDTO(
                            items.stream()
                            .flatMap(orderItem -> orderItem.getStatuses().stream())
                            .map(this::mapOrderStatusToOrderStatusDTO)
                            .sorted(Comparator.comparing(OrderStatusDTO::time))
                            .toList(),
                            companyName,
                            items.stream()
                                    .map(this::mapOrderItemToOrderItemAdminDTO2)
                                    .toList());
                })
                .toList();
    }

    public OrderItemAdminDTO2 mapOrderItemToOrderItemAdminDTO2(OrderItem orderItem) {

        return new OrderItemAdminDTO2(
                orderItem.getProduct().getName(),
                orderItem.getSize(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                orderItem.getId(),
                orderItem.getProduct().getParameters().stream().map(Parameter::getValue).toList()
        );

    }

    public OrderItemAdminDTO mapOrderItemToOrderItemAdminDTO(OrderItem orderItem) {
        Order order = orderRepository.findOrderByOrderItemContaining(orderItem);

        return new OrderItemAdminDTO(
                order.getId(),
                orderItem.getSize(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                orderItem.getStatuses()
                        .stream()
                        .max(Comparator.comparing(OrderStatus::getStatus))
                        .orElseThrow(() -> new NoSuchElementException("No status found")).getStatus()
        );
    }

    public OrderCompanyLongDTO mapOrderToOrderCompanyLongDTO(Order order, Long userId) {
        UserDTO user = webClientService.findUser(Long.valueOf(order.getUserid()));

        List<OrderItem> items = order.getOrderItem().stream().filter(item -> webClientService.getUserId(item.getProduct().getCompanyid()) == userId).toList();

        OrderItem orderItem = items.get(0);

        LocalDateTime sentDate = null;

        Optional<OrderStatus> first = orderItem.getStatuses().stream().filter(status -> status.getStatus().equals(Status.SENT)).findFirst();
        if(first.isPresent()){
            sentDate = first.get().getTime();
        }

        return new OrderCompanyLongDTO(
                order.getId(),
                order.getStatuses()
                        .stream()
                        .max(Comparator.comparing(OrderStatus::getTime))
                        .orElseThrow(() -> new NoSuchElementException("No status found")).getStatus(),
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                order.getZipcode(),
                order.getPhone(),
                items.stream()
                        .map(order2 -> order2.getPrice().multiply(new BigDecimal(order2.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                sentDate,
                items.stream().map(this::mapOrderItemToOrderItemDto).toList(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                user.firstName(),
                user.lastName(),
                user.username()
        );
    }

    public CompanyLongDTO mapCompanyIdToCompanyLongDTO(Long companyId) {
        CompanyShortDTO company = webClientService.findCompanyName(companyId);
        Long userId = webClientService.getUserId(companyId);
        UserDTO user = webClientService.findUser(userId);

        List<Product> products = productRepository.findProductsByCompanyid(companyId);

        return new CompanyLongDTO(
                company.id(),
                company.name(),
                user.firstName() + " " + user.lastName(),
                products.stream().mapToInt(Product::getSales).sum(),
                orderRepository.getMoneyTurnoverByCompanyId(companyId),
                products.stream().filter(product -> product.getRetired() == null).toList().size(),
                orderRepository.getOrdersByCompanyIdAndStatus(companyId, "COMPLETED").size(),
                orderRepository.getOrdersByCompanyIdAndStatus(companyId, "IN_PROGRESS").size() - orderRepository.getOrdersByCompanyIdAndStatus(companyId, "COMPLETED").size(),
                orderStatusRepository.gets(company.id()),
                company.created(),
                company.banned()
        );
    }

    public OrderUserAdminDTO mapOrderToOrderUserAdminDTO(Order order){

        return new OrderUserAdminDTO(
                order.getId(),
                order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                order.getStreet(),
                order.getZipcode(),
                order.getHouse_number(),
                order.getCity(),
                order.getStatuses().stream().filter( status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().get().getTime(),
                order.getStatuses()
                        .stream()
                        .max(Comparator.comparing(OrderStatus::getStatus))
                        .orElseThrow(() -> new NoSuchElementException("No status found")).getStatus()
        );
    };

    public UserAdminDTO mapUserDtoToUserAdminDTO(UserDTO user){
        List<Order> orders = orderRepository.findOrdersByUserid(user.id());

        return new UserAdminDTO(
                user.id(),
                user.username(),
                user.firstName(),
                user.lastName(),
                user.created(),
                user.banned(),
                user.companyName(),
                orders.stream()
                        .flatMap(order -> order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                orderRepository.getOrdersByUserid(user.id()).size(),
                orderRepository.getSentOrdersByUserid(user.id()).size(),
                user.balance(),
                user.photo()
        );
    }

    public OrderItemUserDTO mapOrderItemToOrderItemUserDTO(OrderItem orderItem){
        CompanyShortDTO companyName = webClientService.findCompanyName(orderItem.getProduct().getCompanyid());

        return new OrderItemUserDTO(
                orderItem.getId(),
                orderItem.getProduct().getName(),
                orderItem.getSize(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                companyName.name()
        );
    }

}


