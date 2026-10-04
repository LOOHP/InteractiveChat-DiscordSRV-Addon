package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.minimessage.MiniMessage;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import github.scarsz.discordsrv.dependencies.mcdiscordreserializer.minecraft.MinecraftSerializer;

/** Plain presentation for relayed chat only; does not affect embeds or Minecraft chat. */
public final class DiscordPlainChat {
    private static final MiniMessage STYLES = MiniMessage.builder().tags(TagResolver.resolver(
            StandardTags.color(), StandardTags.decorations(), StandardTags.gradient(),
            StandardTags.rainbow(), StandardTags.reset(), StandardTags.font())).build();

    private DiscordPlainChat() { }

    public static String render(String message) {
        if (message == null || message.isEmpty()) return message;
        // Decode existing Markdown from rank composers, retaining its visible text.
        // The serializer renders block quotes with a decorative pipe; preserve the
        // player's original quote character instead of introducing that decoration.
        String input = message.replaceAll("(?m)^(\\s*)>(?=\\s|$)", "$1\\\\>");
        String plain = github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                .plainText().serialize(MinecraftSerializer.INSTANCE.serialize(input));
        // Interpret styling only, never commands, clicks, selectors or placeholders.
        plain = plain.replaceAll("(?i)§[0-9a-fk-orx]", "");
        try {
            plain = PlainTextComponentSerializer.plainText().serialize(STYLES.deserialize(plain));
        } catch (RuntimeException invalidStyling) {
            // Invalid player-supplied formatting must not prevent message delivery.
        }
        StringBuilder escaped = new StringBuilder(plain.length());
        for (int i = 0; i < plain.length(); i++) {
            char c = plain.charAt(i);
            if ("\\*_~|`>#[]".indexOf(c) >= 0) escaped.append('\\');
            escaped.append(c);
        }
        // Disable list syntax without changing ordinary hyphens, URLs or decimals.
        return escaped.toString().replaceAll("(?m)^(\\s*)([-+])(?=\\s)", "$1\\\\$2")
                .replaceAll("(?m)^(\\s*\\d+)\\.(?=\\s)", "$1\\\\.")
                .replaceAll("<ICD=(\\d+)\\\\>", "<ICD=$1>");
    }
}
