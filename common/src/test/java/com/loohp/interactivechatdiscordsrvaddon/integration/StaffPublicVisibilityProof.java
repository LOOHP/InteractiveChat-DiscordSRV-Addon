package com.loohp.interactivechatdiscordsrvaddon.integration;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class StaffPublicVisibilityProof {
    private static int checks;

    public static void main(String[] args) {
        UUID subject = UUID.randomUUID();
        AtomicBoolean installed = new AtomicBoolean(false);
        AtomicReference<StaffPublicVisibility.Query> provider = new AtomicReference<>();
        AtomicInteger warnings = new AtomicInteger();
        StaffPublicVisibility visibility = new StaffPublicVisibility(installed::get, provider::get, message -> warnings.incrementAndGet());
        check(!visibility.hidden(subject), "Absent optional Staff preserves legacy behavior");
        installed.set(true);
        check(visibility.hidden(subject), "Missing required service hides subject");
        check(visibility.hidden(subject) && warnings.get() == 1, "Repeated failure does not flood logs");
        provider.set(id -> false);
        check(!visibility.hidden(subject), "Visible subject and recovery");
        provider.set(id -> true);
        check(visibility.hidden(subject), "Vanished or duty-hidden subject");
        provider.set(id -> { throw new IllegalStateException("provider failed"); });
        check(visibility.hidden(subject) && warnings.get() == 2, "Provider runtime failure is closed");
        provider.set(id -> { throw new NoClassDefFoundError("missing optional API"); });
        check(visibility.hidden(subject), "Linkage failure does not kill updater");
        provider.set(id -> { throw new ReflectiveOperationException("service invocation failed"); });
        check(visibility.hidden(subject), "Reflective failure is closed");
        provider.set(id -> false);
        check(!visibility.hidden(subject), "Provider replacement recovers without stale cache");
        provider.set(null);
        check(visibility.hidden(subject) && warnings.get() == 3, "Provider unload is closed");
        System.out.println("Staff visibility proof: " + checks + " checks passed");
    }

    private static void check(boolean passed, String message) {
        checks++;
        if (!passed) throw new AssertionError(message);
    }
}
