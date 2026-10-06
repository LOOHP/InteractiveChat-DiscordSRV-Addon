package com.loohp.interactivechatdiscordsrvaddon.integration;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.plugin.Plugin;

/** Optional adapter over Staff's public Bukkit service, never its persisted state. */
public final class EnthusiaStaffDiscordHook {
    private static final UUID PUBLIC_AUDIENCE = new UUID(0, 0);
    private static final String SERVICE = "net.enthusia.staff.paper.api.StaffVisibilityService";
    private final Plugin owner;
    private final AtomicBoolean staffSeen = new AtomicBoolean();
    private final StaffPublicVisibility visibility;

    public EnthusiaStaffDiscordHook(Plugin owner) {
        this.owner = owner;
        this.visibility = new StaffPublicVisibility(this::staffRequired, this::query, owner.getLogger()::warning);
    }

    private boolean staffRequired() {
        if (owner.getServer().getPluginManager().getPlugin("EnthusiaStaff") != null) {
            staffSeen.set(true);
        }
        return staffSeen.get(); // Provider unload must not silently expose hidden players.
    }

    private StaffPublicVisibility.Query query() {
        Plugin staff = owner.getServer().getPluginManager().getPlugin("EnthusiaStaff");
        if (staff == null || !staff.isEnabled()) {
            return null;
        }
        try {
            Class<?> api = Class.forName(SERVICE, false, staff.getClass().getClassLoader());
            Object provider = owner.getServer().getServicesManager().load(api);
            if (provider == null) {
                return null;
            }
            Method vanished = api.getMethod("isVanished", UUID.class);
            Method canSee = api.getMethod("canSee", UUID.class, UUID.class);
            return subject -> PUBLIC_AUDIENCE.equals(subject)
                    || (boolean) vanished.invoke(provider, subject)
                    || !(boolean) canSee.invoke(provider, PUBLIC_AUDIENCE, subject);
        } catch (ReflectiveOperationException failure) {
            return null;
        }
    }

    public boolean hidden(UUID subject) {
        return visibility.hidden(subject);
    }

}
