package gruppe2.backend.domain.command;

import gruppe2.backend.dto.ProductTypeDTO;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.ProductTypeRepository;
import gruppe2.backend.repository.StatusDefinitionRepository;
import java.util.List;

public class CreateProductTypeCommand {
    private final ProductTypeDTO productTypeDTO;
    private final ProductTypeRepository productTypeRepository;
    private final StatusDefinitionRepository statusDefinitionRepository;

    public CreateProductTypeCommand(
            ProductTypeDTO productTypeDTO,
            ProductTypeRepository productTypeRepository,
            StatusDefinitionRepository statusDefinitionRepository) {
        this.productTypeDTO = productTypeDTO;
        this.productTypeRepository = productTypeRepository;
        this.statusDefinitionRepository = statusDefinitionRepository;
    }

    public ProductType execute() {
        validateStepsExist(productTypeDTO.differentSteps());

        ProductType productType = new ProductType();
        productType.setName(productTypeDTO.name());
        productType.setDifferentSteps(productTypeDTO.differentSteps());

        return productTypeRepository.save(productType);
    }

    private void validateStepsExist(List<Long> stepIds) {
        if (stepIds == null || stepIds.isEmpty()) {
            throw new IllegalArgumentException("Product type must have at least one step");
        }
        
        for (Long stepId : stepIds) {
            if (stepId == null) {
                throw new IllegalArgumentException("Step ID cannot be null");
            }
            statusDefinitionRepository.findById(stepId)
                    .orElseThrow(() -> new RuntimeException("Status definition not found: " + stepId));
        }
    }
}
