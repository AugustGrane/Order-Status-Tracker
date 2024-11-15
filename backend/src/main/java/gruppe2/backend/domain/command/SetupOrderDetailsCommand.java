package gruppe2.backend.domain.command;

import gruppe2.backend.model.Item;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.OrderProductTypeRepository;
import gruppe2.backend.repository.ProductTypeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupOrderDetailsCommand {
    private final Long orderId;
    private final Map<Long, Integer> items;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;

    public SetupOrderDetailsCommand(
            Long orderId,
            Map<Long, Integer> items,
            ItemRepository itemRepository,
            ProductTypeRepository productTypeRepository,
            OrderProductTypeRepository orderProductTypeRepository) {
        this.orderId = orderId;
        this.items = items;
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
    }

    public void execute() {
        items.forEach((itemId, quantity) -> {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

            ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                    .orElseThrow(() -> new RuntimeException("Product type not found: " + item.getProductTypeId()));

            // Create OrderDetails with initial status
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrderId(orderId);
            orderDetails.setItem(item);
            orderDetails.setProduct_type(productType.getName());
            orderDetails.setItemAmount(quantity);

            // Set up steps
            List<Long> steps = productType.getDifferentSteps();
            orderDetails.setDifferentSteps(steps);
            orderDetails.setCurrentStepIndex(0);

            // Initialize status updates
            Map<Long, LocalDateTime> statusUpdates = new HashMap<>();
            statusUpdates.put(steps.get(0), LocalDateTime.now());
            orderDetails.setUpdated(statusUpdates);

            orderProductTypeRepository.save(orderDetails);
        });
    }
}
