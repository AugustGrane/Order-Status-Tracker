package gruppe2.backend.service;

import gruppe2.backend.service.webhook.WebhookPayload;
import gruppe2.backend.domain.command.ProcessWebhookCommand;
import gruppe2.backend.mapper.OrderMapper;
import gruppe2.backend.mapper.WebhookMapper;
import gruppe2.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookService {
    private final OrderService orderService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final WebhookMapper webhookMapper;
    private final OrderMapper orderMapper;

    public WebhookService(
            OrderService orderService,
            ItemService itemService,
            ItemRepository itemRepository,
            ProductTypeRepository productTypeRepository,
            OrderProductTypeRepository orderProductTypeRepository,
            WebhookMapper webhookMapper,
            OrderMapper orderMapper) {
        this.orderService = orderService;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
        this.webhookMapper = webhookMapper;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public void createOrderInDatabase(WebhookPayload payload) {
        ProcessWebhookCommand command = new ProcessWebhookCommand(
            payload,
            orderService,
            itemService,
            itemRepository,
            productTypeRepository,
            orderProductTypeRepository,
            webhookMapper,
            orderMapper
        );
        command.execute();
    }
}
