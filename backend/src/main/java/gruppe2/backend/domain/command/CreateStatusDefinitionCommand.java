package gruppe2.backend.domain.command;

import gruppe2.backend.dto.StatusDefinitionDTO;
import gruppe2.backend.model.StatusDefinition;
import gruppe2.backend.repository.StatusDefinitionRepository;

public class CreateStatusDefinitionCommand {
    private final StatusDefinitionDTO dto;
    private final StatusDefinitionRepository repository;

    public CreateStatusDefinitionCommand(
            StatusDefinitionDTO dto,
            StatusDefinitionRepository repository) {
        this.dto = dto;
        this.repository = repository;
    }

    public StatusDefinition execute() {
        StatusDefinition statusDefinition = new StatusDefinition();
        statusDefinition.setName(dto.name());
        statusDefinition.setDescription(dto.description());
        statusDefinition.setImage(dto.image());

        return repository.save(statusDefinition);
    }
}
