package gruppe2.backend.domain.command;

import gruppe2.backend.dto.ProductTypeDTO;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.ProductTypeRepository;
import gruppe2.backend.repository.StatusDefinitionRepository;

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

    private void validateStepsExist(Long[] stepIds) {
        for (Long stepId : stepIds) {
            statusDefinitionRepository.findById(stepId)
                    .orElseThrow(() -> new RuntimeException("Status definition not found: " + stepId));
        }
    }
}
