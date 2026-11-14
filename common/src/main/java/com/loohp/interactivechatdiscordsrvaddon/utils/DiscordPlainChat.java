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

    /** Remove visual styling without turning previously code-shielded mentions into pings. */
    public static String render(String message) {
        if (message == null || message.isEmpty()) return message;
        // Decode existing Markdown from rank composers, retaining its visible text.
        // The serializer renders block quotes with a decorative pipe; preserve the
        // player's original quote character instead of introducing that decoration.
        String input = preserveCodeMentionShielding(message).replaceAll("(?m)^(\\s*)>(?=\\s|$)", "$1\\\\>");
        String plain = github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                .plainText().serialize(MinecraftSerializer.INSTANCE.serialize(input));
        // Interpret styling only, never commands, clicks, selectors or placeholders.
        plain = plain.replaceAll("(?i)§[0-9a-fk-orx]", "").replace("§", "");
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

    /** Code spans are non-notifying in Discord; retain that property when removing their delimiters. */
    private static String preserveCodeMentionShielding(String message) {
        StringBuilder shielded = new StringBuilder(message.length());
        int copied = 0;
        for (int start = 0; start < message.length();) {
            if (message.charAt(start) != '`' || escaped(message, start)) {
                start++;
                continue;
            }
            int content = runEnd(message, start);
            int width = content - start;
            int end = content;
            while (end < message.length()) {
                if (message.charAt(end) != '`') {
                    end++;
                    continue;
                }
                int after = runEnd(message, end);
                if (after - end == width && !escaped(message, end)) {
                    shielded.append(message, copied, content);
                    shielded.append(message.substring(content, end).replace("@", "@\u200B"));
                    shielded.append(message, end, after);
                    copied = after;
                    break;
                }
                end = after;
            }
            start = copied > content ? copied : content;
        }
        shielded.append(message, copied, message.length());
        return shielded.toString();
    }

    private static int runEnd(String message, int start) {
        int end = start;
        while (end < message.length() && message.charAt(end) == '`') end++;
        return end;
    }

    private static boolean escaped(String message, int offset) {
        int slashes = 0;
        while (offset > 0 && message.charAt(--offset) == '\\') slashes++;
        return (slashes & 1) != 0;
    }
}
