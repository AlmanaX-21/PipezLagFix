# PipezLagFix Migration to NeoForge 26.1

## Build System

- [x] `gradle.properties` — `minecraft_version` → `26.1`
- [x] `gradle.properties` — `minecraft_version_range` → `[26.1,26.2)`
- [x] `gradle.properties` — `neo_version` → `26.1.0.1-beta`
- [x] `gradle.properties` — `neo_version_range` → `[26.1.0.1-beta,)`
- [x] `gradle.properties` — `loader_version_range` → `[5,)`
- [x] `gradle.properties` — `org.gradle.java.home` → Java 25 path
- [x] `gradle.properties` — Remove `parchment_minecraft_version` and `parchment_mappings_version`
- [x] `gradle.properties` — Bump `mod_version` to `1.1.0`
- [x] `build.gradle` — ModDevGradle plugin `2.0.140` → `2.0.141`
- [x] `build.gradle` — Java toolchain `21` → `25`
- [x] `build.gradle` — Remove `parchment {}` block
- [x] `build.gradle` — Update pipez dependency file ID to `7806241` for Pipez `1.2.27+26.1`
- [x] `gradle-wrapper.properties` — Gradle `8.14` → `9.1`
- [x] `settings.gradle` — Update `foojay-resolver-convention` `0.8.0` → `1.0.0`

## Core API Migration (IItemHandler → ResourceHandler)

- [x] Researched `ResourceHandler<T>` interface methods from NeoForge 26.1 source
- [x] Rewrite `TrackingItemHandler.java` to wrap `ResourceHandler<ItemResource>` instead of `IItemHandler`
  - Delegates all `ResourceHandler` methods
  - Intercepts both `extract` overloads and only triggers success on committed transactions
- [x] Update `MixinItemPipeType.java` — change parameter types from `IItemHandler` to `ResourceHandler<ItemResource>`
  - [x] `@Inject` on `insertEqually` HEAD — updated parameter list
  - [x] `@Inject` on `insertOrdered` HEAD — updated parameter list
  - [x] `@ModifyVariable` on `insertEqually` — wraps `ResourceHandler` instead of `IItemHandler`
  - [x] `@ModifyVariable` on `insertOrdered` — wraps `ResourceHandler` instead of `IItemHandler`
  - [x] `@Inject` on `insertEqually` RETURN — updated parameter list
  - [x] `@Inject` on `insertOrdered` RETURN — updated parameter list
- [x] Verified `ItemPipeType.insertEqually` and `ItemPipeType.insertOrdered` signatures against upstream Pipez 26.1 source

## Mixin Config

- [x] `pipezlagfix.mixins.json` — `compatibilityLevel` `JAVA_21` → `JAVA_25`

## Verify (likely no changes)

- [x] `MixinPipeLogicTileEntity.java` — confirmed `PipeLogicTileEntity` still at same package (no changes needed)
- [x] `Pipezlagfix.java` — `@Mod`, `IEventBus`, `ModContainer` pattern unchanged (no changes needed)
- [x] `Config.java` — `ModConfigSpec`, `EventBusSubscriber` unchanged (no changes needed)
- [x] `Commands.java` — Brigadier API stable (no changes needed)
- [x] `IItemPipeBackoff.java` — pure custom interface (no changes needed)
- [x] `MixinPlugin.java` — no NeoForge APIs (no changes needed)
- [x] `neoforge.mods.toml` — format unchanged, version ranges auto-updated via template (no changes needed)

## Post-Migration

- [x] Find correct Pipez 26.1 CurseForge file ID
- [x] Verify mixin targets against upstream Pipez 26.1 source
- [ ] Test that backoff logic still fires correctly with new transfer API
