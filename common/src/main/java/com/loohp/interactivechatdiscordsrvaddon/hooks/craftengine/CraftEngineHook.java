package com.loohp.interactivechatdiscordsrvaddon.hooks.craftengine;

import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import net.momirealms.craftengine.bukkit.item.BukkitItemManager;
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine;
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

    public static File getGeneratedResourcePackFile() {
        if (!isEngineAvailable()) {
            return null;
        }
        Path path = BukkitCraftEngine.instance().packManager().resourcePackPath();
        if (path != null && Files.exists(path) && Files.isRegularFile(path)) {
            return path.toFile();
        }
        return null;
    }

    private static net.momirealms.craftengine.core.entity.player.Player adaptPlayer(OfflineICPlayer icPlayer) {
        if (icPlayer == null || !icPlayer.isOnline() || !icPlayer.getPlayer().isLocal()) {
            return null;
        }
        return BukkitCraftEngine.instance().adapt(icPlayer.getPlayer().getLocalPlayer());
    }

    private static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().equals(Material.AIR) || itemStack.getAmount() <= 0;
    }
}