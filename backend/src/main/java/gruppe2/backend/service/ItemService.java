package gruppe2.backend.service;

import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.domain.command.CreateItemCommand;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.ProductTypeRepository;
import gruppe2.backend.repository.OrderProductTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;

    public ItemService(ItemRepository itemRepository, ProductTypeRepository productTypeRepository, OrderProductTypeRepository orderProductTypeRepository) {
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
    }

    public Item createItem(ItemDTO itemDTO) {
        CreateItemCommand command = new CreateItemCommand(itemDTO, itemRepository, productTypeRepository);
        return command.execute();
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
    }

    @Transactional
    public void deleteItem(Long itemId) {
        try {
            List<OrderDetails> orderDetails = orderProductTypeRepository.findByItemId(itemId);
            orderProductTypeRepository.deleteAll(orderDetails);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete item relations with ItemID: " + itemId, e);
        }
        try {
            itemRepository.deleteItemById(itemId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete item with ID: " + itemId, e);
        }
    }
}
