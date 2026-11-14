# EnthusiaStaff Discord visibility

## Spec and acceptance

- WHEN EnthusiaStaff is installed THEN THE SYSTEM SHALL use its registered
  StaffVisibilityService for public Discord visibility, including staff-duty
  restrictions independently of the vanish toggle.
- WHEN the required provider is disabled, missing or fails THEN THE SYSTEM SHALL
  hide the subject and report degraded visibility without killing Discord workers.
- WHEN image add-on player lists and profiles test visibility THEN THE SYSTEM
  SHALL additionally apply Staff's public visibility policy.
- THE SYSTEM SHALL preserve other plugin hooks, permissions, stored state and
  the existing behavior when EnthusiaStaff has never been installed.

## Prove / engine / architecture / refine

Starting base: fetched fork master 0c0ab30 and safely incorporated the existing
presentation branch b3b70d1; upstream master is included. No prior checkout edits
were overwritten. No project EARS validator/state helper exists; this record is
the small requirement/task/evidence substitute, not a claim of tooling success.

Runtime failure evidence: DiscordSRV 1.30.5 workers throw NoClassDefFoundError
for de.myzelyam.api.vanish.VanishAPI. Actual JAR bytecode shows PlayerUtil iterates
VanishHook without handling this linkage failure. EnthusiaStaff exposes a
Bukkit StaffVisibilityService with isVanished(UUID) and canSee(UUID, UUID).
Public visibility must use both: canSee includes duty visibility which is not
equivalent to the separately persisted vanish toggle.

- [x] Implement optional infrastructure adapter without shading Staff API classes.
- [x] Execute focused proof and clean full reactor: all 46 modules pass, with
  10 Staff visibility assertions plus the existing 38 presentation assertions.
  Actual downloaded EnthusiaStaff-Paper.jar confirms both service descriptors;
  the adapter does not bundle or redefine the Staff interface.
- [ ] Review exact source head through a PR before release.
- [ ] SMP Test: disable DiscordSRV's built-in SuperVanish hook before startup,
  activate the reviewed build, then verify worker survival and hidden/visible
  Staff sessions with an ordinary viewer. Do not hot-reload plugins or assume
  already terminated updater threads are revived by texture/config reload.

Architecture refinement: the native updater guard belongs in DiscordSRV's own
startup hook loop (separate `discordsrv-staff-vanish` checkout), not mutation of
its live HashSet from this add-on. This add-on adapter handles its independent
InteractiveChat-based profile/list filters only. Both builds require acceptance.
No production deployment or restart is authorized. Local proof is not real
Discord/player acceptance. This adapter does not implement SuperVanish support.
