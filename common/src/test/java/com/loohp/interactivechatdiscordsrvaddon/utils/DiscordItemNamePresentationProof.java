package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/** Standalone regression proof using the same relocated Adventure classes as production. */
public final class DiscordItemNamePresentationProof {
    private static int checks;
    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        checks++;
    }
    private static boolean hasColor(Component component) {
        return component.color() != null || component.children().stream().anyMatch(DiscordItemNamePresentationProof::hasColor);
    }
    public static void main(String[] args) {
        String literal = "<gradient:#00FF1C:#00EAFF>HiWarden</gradient>";
        Component original = Component.text(literal).decorate(TextDecoration.ITALIC);
        Component formatted = DiscordItemNamePresentation.format(original);
        check("HiWarden".equals(DiscordItemNamePresentation.plain(original)), "embed name");
        check(hasColor(formatted), "tooltip gradient colors");
        check(formatted.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE, "existing italics");
        check(literal.equals(PlainTextComponentSerializer.plainText().serialize(original)), "input unchanged");
        String death = "appointive was speared by CheddarManPro using [" + literal + "]";
        check("appointive was speared by CheddarManPro using [HiWarden]".equals(
                DiscordItemNamePresentation.replaceWeaponName(death, original)), "death weapon name");
        check((literal + " was speared using [HiWarden]").equals(
                DiscordItemNamePresentation.replaceWeaponName(literal + " was speared using [" + literal + "]", original)),
                "unrelated matching name preserved");
        check(("[" + literal + "] using [HiWarden]").equals(
                DiscordItemNamePresentation.replaceWeaponName("[" + literal + "] using [" + literal + "]", original)),
                "only final weapon occurrence replaced");
        check(literal.equals(DiscordItemNamePresentation.replaceWeaponName(literal, original)),
                "unbracketed text preserved");
        Component vanilla = Component.translatable("item.minecraft.diamond_sword");
        check(vanilla.equals(DiscordItemNamePresentation.format(vanilla)), "vanilla translations unchanged");
        Component colored = Component.text("Existing", NamedTextColor.GOLD);
        check(colored.equals(DiscordItemNamePresentation.format(colored)), "existing component unchanged");
        String unclosed = "<gradient:#00FF1C:#00EAFF>HiWarden";
        check("HiWarden".equals(DiscordItemNamePresentation.plain(Component.text(unclosed))), "implicit gradient end");
        String supporter = "<b><gradient:#FF9B00:#FFB172>✧ Founding Supporter";
        Component supporterName = Component.text(supporter);
        Component supporterFormatted = DiscordItemNamePresentation.format(supporterName);
        check("✧ Founding Supporter".equals(DiscordItemNamePresentation.plain(supporterName)), "open bold gradient embed");
        check(hasColor(supporterFormatted), "open bold gradient tooltip colors");
        check(hasBold(supporterFormatted), "open bold gradient tooltip bold");
        check("FainNoir was slain by FainNeito using [✧ Founding Supporter]".equals(
                DiscordItemNamePresentation.replaceWeaponName("FainNoir was slain by FainNeito using [" + supporter + "]", supporterName)),
                "open bold gradient death weapon");
        check(supporter.equals(PlainTextComponentSerializer.plainText().serialize(supporterName)), "supporter input unchanged");
        check(("\\" + supporter).equals(DiscordItemNamePresentation.plain(Component.text("\\" + supporter))), "escaped bold gradient stays literal");
        String escaped = "\\" + literal;
        check(escaped.equals(DiscordItemNamePresentation.plain(Component.text(escaped))), "escaped stays literal");
        String unsafe = "<click:run_command:'/op x'>Name</click>";
        check(unsafe.equals(DiscordItemNamePresentation.plain(Component.text(unsafe))), "no event tag parsing");
        Component siblings = Component.text("Before ").append(original).append(Component.text(" After"));
        check("Before HiWarden After".equals(DiscordItemNamePresentation.plain(siblings)), "siblings retained");
        check("unrelated <gradient:#00FF1C:#00EAFF>Name</gradient>".equals(
                DiscordItemNamePresentation.replaceWeaponName("unrelated <gradient:#00FF1C:#00EAFF>Name</gradient>", original)),
                "unrelated message markup unchanged");
        System.out.println("Passed " + checks + " Discord item-name checks");
    }
    private static boolean hasBold(Component component) {
        return component.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE
                || component.children().stream().anyMatch(DiscordItemNamePresentationProof::hasBold);
    }
}
