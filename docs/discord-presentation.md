# Discord presentation

Item embed titles, tooltip names and weapon death notices normalize literal
hex-gradient MiniMessage spans for Discord presentation only. Stored ItemStacks,
item components and Minecraft hover rendering are not changed.

`Settings.PlainTextRelayedChat` (default `true`) strips styling from relayed
Minecraft chat and escapes Discord Markdown. Set it to `false` to retain the
previous behavior. Exact `<ICD=n>` image markers are preserved. Messages typed
directly in Discord and advancement/item embed images are not moderated by this
option.

Two executable regression proof classes in `common/src/test/java` cover 12 item
name and 19 plain-chat cases. Run each `main` with the common module and its
InteractiveChat/DiscordSRV dependencies on the classpath. These are explicit
proof programs, not automatically discovered JUnit tests.

Local validation compiled the changed classes against release plugin APIs and
ran all 31 checks. A full multi-module Maven build still requires the historical
NMS/renderer dependencies. Hosted checks and actual Discord delivery on a test
server are separate acceptance gates; a release-class overlay is not proof of a
complete current-upstream release build.
