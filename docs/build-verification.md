# Build verification follow-up

## Combined release review, 2026-10-06

- Spec: deliver implicit-ended bold gradients and authoritative Staff public
  profile/list filtering together, preserving item data, existing settings and
  mention shielding. Require exact-head hosted review and complete builds before
  merge; test-server installation is not real Discord/client acceptance.
- Current upstream master 28806de (2026.1.3.0) was fetched and incorporated into
  an isolated continuation of owner PR #1. Existing independent checkouts remain
  unchanged. Parsing 004f4ba and visibility 358c305 were safely incorporated.
- Behavioral proof exists in their committed executable regression programs;
  this integration does not claim a new historical red run. No EARS/state helper
  is available. Full reactor, pinned runtime companion compatibility, hosted
  exact-head findings and actual hidden-player/image acceptance are distinct gates.
- Upstream's new bump requires unpublished InteractiveChat 2026.1.3.0 and the
  full reactor fails dependency resolution. Revert that version-only bump for
  this supported 2026.1.2.0 candidate; retain the fetched ancestry and use real
  existing companion APIs, not stub artifacts.
- Review regression: fenced code with an internal backtick exposed an
  unshielded mention before the fix. Delimiter-run scanning now preserves
  shielding; 21 item-name and 24 plain-chat checks pass. Authorized unshielded
  mentions remain unchanged. Actual Discord notification behavior is unproven.
- Full combined reactor and hosted review pending. No production changes or
  test restart performed.

## Requirements

- WHEN an early reactor module resolves the author's libraries THEN THE SYSTEM SHALL use the same author repository documented by common, without changing dependency versions.
- WHEN presentation verification runs THEN THE SYSTEM SHALL compile the production presentation helpers and execute both existing proof programs against checksum-pinned companion plugin APIs, failing on compilation, proof or input-integrity errors.
- WHEN a pull request is verified THEN THE SYSTEM SHALL run the complete clean Maven reactor as a separate hosted job, without replacing it with presentation-only checks.
- THE SYSTEM SHALL distinguish presentation verification from a complete multi-version release build and actual Discord/client acceptance.

## Task and evidence

- [x] REVIEW-002 implementation and local verification: scope weapon normalization to its final bracketed occurrence, preserve code-shielded Discord mentions during plain-text normalization, and remove malformed residual section signs. Preserve authorized unshielded mention behavior, item markers, and stored Minecraft components.
  - Red: the new weapon-name regression failed because unrelated matching text was replaced. The separate mention regression demonstrated that code-shielded `@everyone`/`@here` became unshielded text; this is text-level evidence, not a claim of a live notification exploit.
  - Green: 15 item-name and 23 plain-chat assertions pass against checksum-pinned companion APIs. Java 25 `mvn clean verify` passes all 46 modules, including 26.2 and 26.3, and executes both proof programs (2026-10-05).
  - Architecture: normalization remains a Discord-only presentation adapter; stored item components and authorized unshielded mentions are unchanged. Code-shielded mentions retain an invisible separator after `@` when their code delimiters are removed.
  - Pending acceptance: fresh exact-head hosted checks/review, actual bot/webhook notification and rendered-item testing, and the separately requested EnthusiaStaff visibility/staffmode integration. No production upload or activation occurred.

- [x] INFRA-001: repair repository inheritance and make the 31 presentation checks reproducible in hosted CI.
  - Current upstream master: 0c0ab3029c824ecb27bceeac98b72d75acfa2d84; included by this branch. Existing untracked local overlay script is preserved and excluded.
  - Red diagnostic: root Maven verify cannot resolve BlockModelRenderer 1.1.4.0 or InteractiveChat 2026.1.2.0 through JitPack/Central. Their POMs and InteractiveChat JAR are available from the author's HTTPS repository; common already declares it, but abstraction and version modules do not inherit it.
  - Existing proof mains check 12 item-name and 19 plain-relay cases using the actual relocated Adventure and DiscordSRV serializer APIs. No behavioral change or historical TDD red/green is claimed for this infrastructure task.
  - No local SPEAR EARS validator/state helper exists in this repository. This small requirement/task/evidence record tracks spec, diagnostic proof, build infrastructure, unchanged architecture, and verification refinement without claiming absent tooling passed.
  - Reactor diagnostics also showed HTTP 403 responses from common's obsolete Paper URL and slow attempts to resolve Central libraries through third-party repositories. Use Paper's documented endpoint (https://docs.papermc.io/paper/dev/project-setup/) and explicitly prefer Maven Central for its release artifacts, retaining all other repositories and dependency versions.
  - Refinement: at runtime-source commit 239f80489fcd395d35406371cb90ac26d61d66c4, Java 25 Maven clean verify passes all 46 modules, including both 26.2/26.3 adapters and common. Maven executes the 12 item-name and 19 plain-chat proof assertions. The separate checksum-pinned verifier also passes all 31 checks locally and in hosted presentation jobs. Changes are build infrastructure/documentation only; no new gameplay-layer dependency or runtime setting is introduced by this follow-up.
  - Local unmerged full-reactor artifact: common/target/InteractiveChatDiscordSrvAddon-2026.1.2.0.jar; SHA-256 C6CE45EFAF3783806A64FA8C9C47A030CAE58E40C14C7D6C1561A8840C1E3AD6. This supersedes the old overlay as local source-build evidence, but is not a merged release or authorized production artifact. Root build warnings about absent upstream checksums and shading overlap remain visible, not suppressed.

## Release gate

The root reactor also requires CraftBukkit/NMS artifacts for every declared version plus its remaining companion libraries; repository availability is checked by actual compilation, not assumed. If artifacts become unavailable, use the author's supported build prerequisites, not dummy classes or a release overlay. Presentation CI does not build, shade or release the plugin and must not replace the separate full-reactor CI job. Never use the local release-class overlay as merged-source release evidence. Substantive review, complete build and isolated-server image/chat acceptance remain open. No production change is authorized.
