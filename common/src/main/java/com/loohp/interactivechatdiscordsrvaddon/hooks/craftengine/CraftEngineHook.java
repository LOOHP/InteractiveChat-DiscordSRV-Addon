package com.loohp.interactivechatdiscordsrvaddon.hooks.craftengine;

import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemManager;
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class CraftEngineHook {

    private CraftEngineHook() {
    }

    public static boolean isAvailable() {
        try {
            return Bukkit.getPluginManager().isPluginEnabled("CraftEngine")
                    && BukkitCraftEngine.instance() != null
                    && BukkitItemManager.instance() != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isCustomItem(ItemStack itemStack) {
        if (!isAvailable() || isEmpty(itemStack)) {
            return false;
        }

        try {
            return CraftEngineItems.isCustomItem(itemStack);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static String getCustomItemId(ItemStack itemStack) {
        if (!isAvailable() || isEmpty(itemStack)) {
            return null;
        }

        try {
            net.momirealms.craftengine.core.util.Key key = CraftEngineItems.getCustomItemId(itemStack);
            return key == null ? null : key.toString();
        } catch (Throwable ignored) {
            return null;
        }
    }


    public static ItemStack toClientSideItemStack(ItemStack itemStack, OfflineICPlayer icPlayer) {
        if (!isAvailable() || isEmpty(itemStack)) {
            return itemStack;
        }

        ItemStack clone = itemStack.clone();

        try {
            net.momirealms.craftengine.core.entity.player.Player craftEnginePlayer = adaptPlayer(icPlayer);

            Optional<ItemStack> converted = BukkitItemManager.instance().s2c(clone, craftEnginePlayer);
            return converted.orElse(clone);
        } catch (Throwable ignored) {
            return clone;
        }
    }


    public static File getGeneratedResourcePackFile() {
        if (!isAvailable()) {
            return null;
        }

        try {
            Path path = BukkitCraftEngine.instance().packManager().resourcePackPath();
            if (path != null && Files.exists(path) && Files.isRegularFile(path)) {
                return path.toFile();
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    private static net.momirealms.craftengine.core.entity.player.Player adaptPlayer(OfflineICPlayer icPlayer) {
        if (icPlayer == null || !icPlayer.isOnline() || icPlayer.getPlayer() == null || !icPlayer.getPlayer().isLocal()) {
            return null;
        }

        try {
            Player bukkitPlayer = icPlayer.getPlayer().getLocalPlayer();
            if (bukkitPlayer == null) {
                return null;
            }

            return BukkitCraftEngine.instance().adapt(bukkitPlayer);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().equals(Material.AIR) || itemStack.getAmount() <= 0;
    }
}