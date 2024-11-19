package gruppe2.backend.service;

import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.domain.command.CreateItemCommand;
import gruppe2.backend.model.Item;
import gruppe2.backend.repository.ItemProjection;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.ProductTypeRepository;
import gruppe2.backend.repository.OrderProductTypeRepository;
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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        if (item.isDeleted()) {
            throw new RuntimeException("Item is deleted. Use another item ID: " + itemId);
        }
        return item;
    }

    public void setItemAsDeleted(Long itemId) {
        try {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
            if (item.isDeleted()) {
                throw new RuntimeException("Item is already deleted: " + itemId);
            }
            item.setDeleted(true);
            itemRepository.save(item);
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not delete item: " + itemId);
        }
    }

    public List<ItemProjection> findAllByOrderByIdAsc() {
        try {
            return itemRepository.findAllByOrderByIdAsc();
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not fetch all items: " + e);
        }
    }
}
