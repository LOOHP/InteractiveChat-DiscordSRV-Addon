package com.loohp.interactivechatdiscordsrvaddon.utils;

public final class DiscordPlainChatProof {
    private static int checks;
    private static void check(String input, String expected) {
        String actual = DiscordPlainChat.render(input);
        if (!expected.equals(actual)) throw new AssertionError(input + " -> " + actual + "; expected " + expected);
        checks++;
    }
    public static void main(String[] args) {
        check("**[Glorious Fellow]** Player » *hello*", "\\[Glorious Fellow\\] Player » hello");
        check("<b><gradient:#FF110A:#C70000>Glorious Fellow</gradient></b> Player » hello", "Glorious Fellow Player » hello");
        check("Player » __underline__ ~~strike~~ ||spoiler||", "Player » underline strike spoiler");
        check("Player » `code`", "Player » code");
        check("# heading", "\\# heading");
        check("-# small text", "-\\# small text");
        check("> quote", "\\> quote");
        check("- list\n1. numbered\n+ list", "\\- list\n1\\. numbered\n\\+ list");
        check("Player_Name » hello", "Player\\_Name » hello");
        check("Player » https://example.org/a-b", "Player » https://example.org/a-b");
        check("[Sword]<ICD=123>", "\\[Sword\\]<ICD=123>");
        check("Player » 5 * 3 = 15", "Player » 5 \\* 3 = 15");
        check("Player » <3", "Player » <3");
        check("§a[Rank] §rPlayer » hello", "\\[Rank\\] Player » hello");
        check("Player » \\*literal\\*", "Player » \\*literal\\*");
        check("Player » <click:run_command:'/op x'>Name</click>", "Player » <click:run\\_command:'/op x'\\>Name</click\\>");
        check("Player » <gradient:not-a-color>hello</gradient>", "Player » <gradient:not-a-color\\>hello</gradient\\>");
        check("Sword<ICD=12><ICD=345>", "Sword<ICD=12><ICD=345>");
        check("", "");
        check("Player » `@everyone` `@here`", "Player » @\u200Beveryone @\u200Bhere");
        check("Player » `<@123456789>` `<@&123456789>`", "Player » <@\u200B123456789\\> <@\u200B&123456789\\>");
        check("Player » @everyone @here", "Player » @everyone @here");
        check("Player » §zhello§", "Player » zhello");
        String fenced = DiscordPlainChat.render("Player » ```code ` internal @everyone <@123456789>``` ");
        if (fenced.contains("@everyone") || fenced.contains("<@123456789")) {
            throw new AssertionError("Fenced code mentions became unshielded: " + fenced);
        }
        checks++;
        System.out.println("Passed " + checks + " plain Discord chat checks");
    }
}
