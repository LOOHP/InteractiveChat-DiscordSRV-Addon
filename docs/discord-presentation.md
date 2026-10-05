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
proof programs, not automatically discovered JUnit tests. The common Maven test
phase now executes both mains and fails on their assertions. The standalone
`scripts/verify-discord-presentation.ps1` also compiles both production helpers
and runs the proofs against SHA-256-pinned companion APIs; GitHub's presentation
workflow runs this bounded check without claiming a complete plugin build.

Local validation compiled the changed classes against release plugin APIs and
ran all 31 checks. After repository-resolution repairs, the full clean Maven
reactor passes all 46 modules at runtime-source commit 239f804; Maven now executes
both proof programs too. Hosted full-reactor checks, substantive review and actual
Discord delivery on a test server remain separate acceptance gates. See
`docs/build-verification.md` for source/artifact evidence; the older release-class
overlay is not used as complete source-build evidence.
