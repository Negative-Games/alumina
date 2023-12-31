package games.negative.alumina.model.future;

import games.negative.alumina.util.Tasks;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A task to complete in the future with a result.
 * @param <T> The type of the result.
 * @deprecated Use {@link games.negative.alumina.future.BukkitCompletableFuture} instead.
 */
@Deprecated
public class BukkitCompletableFuture<T> implements BukkitFuture<T> {

    private static final Duration LIFETIME = Duration.ofMinutes(30);

    private AtomicReference<T> reference = null;
    private Consumer<T> task;
    private boolean async = false;
    private final BukkitTask bukkitTask;
    private final Instant initialization;

    public BukkitCompletableFuture() {
        this.initialization = Instant.now();

        this.bukkitTask = Tasks.async(() -> {
            // Cancel the task if it has been running for more than 30 minutes.
            if (Duration.between(initialization, Instant.now()).compareTo(LIFETIME) > 0) {
                this.cancel();
                return;
            }

            if (reference == null || task == null) return;

            T value = reference.get();
            if (async) {
                task.accept(value);
                this.cancel();
            } else {
                Tasks.run(() -> task.accept(value));
                this.cancel();
            }
        }, 0, 1);
    }

    @Override
    public BukkitFuture<T> supply(@NotNull Supplier<T> supplier) {
        if (this.reference == null) this.reference = new AtomicReference<>(null);
        Tasks.run(() -> this.reference.set(supplier.get()));
        return this;
    }

    @Override
    public BukkitFuture<T> supplyAsync(@NotNull Supplier<T> supplier) {
        if (this.reference == null) this.reference = new AtomicReference<>(null);
        Tasks.async(() -> this.reference.set(supplier.get()));
        return this;
    }

    @Override
    public void whenComplete(@NotNull Consumer<T> task) {
        this.task = task;
        this.async = false;
    }

    @Override
    public void whenCompleteAsync(@NotNull Consumer<T> task) {
        this.task = task;
        async = true;
    }

    @Override
    public void cancel() {
        this.reference = null;
        this.bukkitTask.cancel();
    }
}
