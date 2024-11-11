package gruppe2.backend.mapper;

import gruppe2.backend.domain.CustomerInfo;
import gruppe2.backend.service.webhook.WebhookPayload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WebhookMapper {
    
    public CustomerInfo toCustomerInfo(WebhookPayload payload) {
        String displayName;
        if (!Objects.equals(payload.getBilling().getCompany(), "")) {
            String companyName = payload.getBilling().getCompany();
            String name = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
            displayName = companyName + " | " + name;
        } else {
            displayName = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
        }

        return new CustomerInfo(
            displayName,
            "", // Notes
            false // Priority
        );
    }
}
