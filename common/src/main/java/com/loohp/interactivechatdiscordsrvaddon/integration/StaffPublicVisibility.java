package com.loohp.interactivechatdiscordsrvaddon.integration;

import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.concurrent.atomic.AtomicBoolean;

/** Public Discord audiences have no Minecraft staff identity or rank. */
public final class StaffPublicVisibility {
    public interface Query {
        boolean hidden(UUID subject) throws ReflectiveOperationException;
    }

    private final BooleanSupplier required;
    private final Supplier<Query> query;
    private final Consumer<String> warning;
    private final AtomicBoolean degraded = new AtomicBoolean();

    public StaffPublicVisibility(BooleanSupplier required, Supplier<Query> query, Consumer<String> warning) {
        this.required = required;
        this.query = query;
        this.warning = warning;
    }

    public boolean hidden(UUID subject) {
        try {
            if (!required.getAsBoolean()) {
                return false;
            }
            Query provider = query.get();
            if (provider == null) {
                return failClosed();
            }
            boolean hidden = provider.hidden(subject);
            degraded.set(false);
            return hidden;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            return failClosed();
        }
    }

    private boolean failClosed() {
        if (degraded.compareAndSet(false, true)) {
            warning.accept("EnthusiaStaff public visibility unavailable; hiding subjects from Discord until it recovers.");
        }
        return true;
    }
}
