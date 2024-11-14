package gruppe2.backend.domain.command;

/**
 * Base interface for all commands in the application.
 * Follows the Command pattern to encapsulate business operations.
 *
 * @param <T> The type of result this command produces
 */
public interface Command<T> {
    /**
     * Executes the command and returns the result.
     *
     * @return The result of type T
     * @throws RuntimeException if the command execution fails
     */
    T execute();
}
