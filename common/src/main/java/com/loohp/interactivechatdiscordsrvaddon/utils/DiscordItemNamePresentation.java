package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.minimessage.MiniMessage;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import java.util.regex.Pattern;

/** Discord-only presentation; never writes item metadata or parses arbitrary message tags. */
public final class DiscordItemNamePresentation {
    private static final Pattern GRADIENT = Pattern.compile(
            "(?<!\\\\)(?<!<b>)(?<!<bold>)(?:<b>|<bold>)?<gradient:#[0-9a-fA-F]{6}(?::#[0-9a-fA-F]{6})+(?::[-+]?(?:[01](?:\\.\\d+)?|\\.\\d+))?>[^<>]*(?:</gradient>)?(?:</b>|</bold>)?");
    // Item-name components provide the boundary for MiniMessage's implicit closes.
    // Do not enable arbitrary tags (clicks, hover, selectors, placeholders, etc.).
    private static final MiniMessage FORMAT = MiniMessage.builder().tags(
            TagResolver.resolver(StandardTags.gradient(), StandardTags.decorations())).build();

    private DiscordItemNamePresentation() { }

    public static Component format(Component name) {
        if (name == null) return null;
        return name.replaceText(builder -> builder.match(GRADIENT).replacement((match, ignored) ->
                match.group().length() > 4096 ? Component.text(match.group()) : FORMAT.deserialize(match.group())));
    }

    /** Discord embed text cannot display per-character RGB colors. */
    public static String plain(Component name) {
        return PlainTextComponentSerializer.plainText().serialize(format(name));
    }

    /** Normalize only the final bracketed weapon occurrence, leaving unrelated text intact. */
    public static String replaceWeaponName(String message, Component weaponName) {
        if (message == null || weaponName == null) return message;
        String raw = PlainTextComponentSerializer.plainText().serialize(weaponName);
        String clean = plain(weaponName);
        if (raw.isEmpty() || raw.equals(clean)) return message;
        String bracketed = "[" + raw + "]";
        int weaponIndex = message.lastIndexOf(bracketed);
        return weaponIndex < 0 ? message : message.substring(0, weaponIndex)
                + "[" + clean + "]" + message.substring(weaponIndex + bracketed.length());
    }
}
