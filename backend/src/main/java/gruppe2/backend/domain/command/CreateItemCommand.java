package gruppe2.backend.domain.command;

import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.model.Item;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.ProductTypeRepository;

public class CreateItemCommand {
    private final ItemDTO itemDTO;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;

    public CreateItemCommand(
            ItemDTO itemDTO,
            ItemRepository itemRepository,
            ProductTypeRepository productTypeRepository) {
        this.itemDTO = itemDTO;
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
    }

    public Item execute() {
        validateProductType(itemDTO.productTypeId());

        Item item = new Item();
        item.setId(itemDTO.id());
        item.setName(itemDTO.name());
        item.setProductTypeId(itemDTO.productTypeId());
        item.setImage(itemDTO.item_image());

        return itemRepository.save(item);
    }

    private void validateProductType(Long productTypeId) {
        productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new RuntimeException("Product type not found"));
    }
}
