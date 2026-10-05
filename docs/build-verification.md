# Build verification follow-up

## Requirements

- WHEN an early reactor module resolves the author's libraries THEN THE SYSTEM SHALL use the same author repository documented by common, without changing dependency versions.
- WHEN presentation verification runs THEN THE SYSTEM SHALL compile the production presentation helpers and execute both existing proof programs against checksum-pinned companion plugin APIs, failing on compilation, proof or input-integrity errors.
- WHEN a pull request is verified THEN THE SYSTEM SHALL run the complete clean Maven reactor as a separate hosted job, without replacing it with presentation-only checks.
- THE SYSTEM SHALL distinguish presentation verification from a complete multi-version release build and actual Discord/client acceptance.

## Task and evidence

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
