package gruppe2.backend.dto;

import java.util.List;
import java.util.ArrayList;

public record ProductTypeDTO(
        String name,
        List<Long> differentSteps
) {
    public ProductTypeDTO {
        // Ensure differentSteps is never null
        differentSteps = differentSteps != null ? new ArrayList<>(differentSteps) : new ArrayList<>();
    }

    // Factory method for backward compatibility
    public static ProductTypeDTO fromArray(String name, Long[] differentSteps) {
        List<Long> stepsList = new ArrayList<>();
        if (differentSteps != null) {
            for (Long step : differentSteps) {
                stepsList.add(step);
            }
        }
        return new ProductTypeDTO(name, stepsList);
    }
}
