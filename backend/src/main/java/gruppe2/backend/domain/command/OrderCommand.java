package gruppe2.backend.domain.command;

import gruppe2.backend.domain.Order;

@FunctionalInterface
public interface OrderCommand {
    /**
     * Executes a command on the given order.
     * @param order The order to execute the command on
     */
    void execute(Order order);
}
