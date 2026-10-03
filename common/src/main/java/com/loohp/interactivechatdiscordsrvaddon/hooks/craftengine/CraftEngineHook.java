package com.loohp.interactivechatdiscordsrvaddon.hooks.craftengine;

import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.momirealms.craftengine.bukkit.item.BukkitItemManager;
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine;
import net.momirealms.craftengine.core.pack.Pack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class CraftEngineHook {

    public static boolean isEngineAvailable() {
        return BukkitCraftEngine.instance() != null && BukkitItemManager.instance() != null;
    }

    public static ItemStack toClientSideItemStack(ItemStack itemStack, OfflineICPlayer icPlayer) {
        if (!isEngineAvailable() || isEmpty(itemStack)) {
            return itemStack;
        }
        ItemStack clone = itemStack.clone();
        net.momirealms.craftengine.core.entity.player.Player craftEnginePlayer = adaptPlayer(icPlayer);
        return BukkitItemManager.instance().s2c(clone, craftEnginePlayer).orElse(clone);
    }

    public static List<ValuePairs<String, File>> getGeneratedResourcePackFile(Set<String> craftEngineResourcePacks) {
        if (!isEngineAvailable()) {
            return Collections.emptyList();
        }
        List<ValuePairs<String, File>> packs = new ArrayList<>();
        for (Pack pack : BukkitCraftEngine.instance().packManager().loadedPacks()) {
            String namespacedKey = pack.namespace() + ":" + pack.name();
            if (craftEngineResourcePacks.contains(namespacedKey)) {
                Path path = pack.resourcePackFolder();
                if (path != null && Files.exists(path) && Files.isRegularFile(path)) {
                    packs.add(new ValuePairs<>(namespacedKey, path.toFile()));
                }
            }
        }
        return packs;
    }

    private static net.momirealms.craftengine.core.entity.player.Player adaptPlayer(OfflineICPlayer icPlayer) {
        if (icPlayer == null || !icPlayer.isOnline() || !icPlayer.getPlayer().isLocal()) {
            return null;
        }
        return BukkitCraftEngine.instance().platform().getPlayer(icPlayer.getUniqueId());
    }

    private static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().equals(Material.AIR) || itemStack.getAmount() <= 0;
    }
}