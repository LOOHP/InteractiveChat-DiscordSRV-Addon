# Discord presentation

Item embed titles, tooltip names and weapon death notices normalize literal
hex-gradient MiniMessage spans for Discord presentation only. Stored ItemStacks,
item components and Minecraft hover rendering are not changed.

## Open item-name styling follow-up (2026-10-05)

- Spec: WHEN an item name contains a literal hex gradient with optional bold
  prefix and implicit end tags THEN Discord embed/death text SHALL show the
  plain name and the rendered tooltip SHALL show its gradient and bold style.
  Stored components, unrelated message text and escaped markup SHALL remain
  unchanged; interactive tags SHALL never become active.
- Current upstream and fork master were fetched and both remain 0c0ab30.
  Isolated work begins at the ongoing presentation head b3b70d1, which includes
  that base, without overwriting the independent Staff visibility work.
- Prove: the screenshot's `<b><gradient:#FF9B00:#FFB172>✧ Founding Supporter`
  lacks explicit closing tags. Existing matching required `</gradient>`.
  New proof failed at `implicit gradient end` before the implementation change.
- Engine/architecture: extend only the item-name span adapter to optional bold
  and implicit closes. Existing embed, image-tooltip and final bracketed death
  weapon call sites share it. Only gradient and decoration resolvers are enabled;
  no item mutation or whole-message parsing is introduced.
- Refine: 21 item-name and 23 plain-chat checks pass against pinned companion
  APIs. Full Maven clean verify also passed all 46 modules, including 26.2 and
  26.3, on Java 25 at 22:09 EDT; see the workspace artifact
  `ic-addon-open-item-styles-build.log`. No project EARS/state helper is present; this is the scoped SPEAR record,
  not a tooling-success claim. Full reactor, hosted review and real Discord
  acceptance remain distinct gates. No server upload/restart is authorized by
  this follow-up alone.

`Settings.PlainTextRelayedChat` (default `true`) strips styling from relayed
Minecraft chat and escapes Discord Markdown. Set it to `false` to retain the
previous behavior. Exact `<ICD=n>` image markers are preserved. Messages typed
directly in Discord and advancement/item embed images are not moderated by this
option.

Two executable regression proof classes in `common/src/test/java` cover 15 item
name and 23 plain-chat cases. Run each `main` with the common module and its
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

Review follow-up adds seven regressions (38 checks total): only the final
bracketed weapon-name occurrence is normalized, code-shielded mentions retain
non-notifying text when Markdown delimiters are removed, and malformed residual
section signs are removed. Authorized unshielded mentions remain unchanged.
The checksum-pinned verifier and all 46 clean reactor modules pass locally;
fresh hosted review and real Discord delivery remain separate gates.
