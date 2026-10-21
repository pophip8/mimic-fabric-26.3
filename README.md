# Mimic — Minecraft 26.3 / Fabric

[Français](README_FR.md) · [Validation](docs/VALIDATION.md) · [MIT license](LICENSE)

A community port of **Mimic 1.2.38** to Minecraft Java **26.3** with Fabric and French/English translations. Mimics disguise themselves as chests, store stolen items, attack curious players, and can be locked or tamed.

The original mod and code are by **Luke Graham Landry**. Original models, textures and animations are credited to **Redrix** and **EpicNecromancer1**. This repository maintains a community port; it does not claim authorship of the original work or endorsement by its creators.

## Status

Version **1.2.38-port.1**. Compilation, seven Mimic server GameTests plus the Minecraft control test, and an automated bilingual client scenario passed in the development environment. The built JAR also passed server tests with the standalone Fabric launcher. See [validation and limitations](docs/VALIDATION.md).

This initial publication contains sources. No binary release is published with it. Compatibility with third-party modpacks and automatic migration of Mimic inventories from Minecraft 1.19.1 remain unverified; no legacy inventory converter is provided.

## Requirements and installation

| Component | Version |
| --- | --- |
| Minecraft Java | 26.3 |
| JDK / Java | 25 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.164.0+26.3 |
| GeckoLib | **5.5.7 for Fabric 26.3** |

Build the mod as described below, then copy the main JAR from `build/libs/` to your instance's `mods/` folder together with Fabric API and the exact [GeckoLib 5.5.7 Fabric 26.3 build](https://modrinth.com/mod/geckolib/version/kSxHvs99). Install these mods on both the server and clients for multiplayer. Do not install the `-sources.jar` or `-testmod.jar` as the playable mod. Select English (US) or Français (France) in Minecraft's language settings.

Use a separate test instance and back up worlds before testing this port with existing saves.

## Build and test

Install JDK 25 and set `JAVA_HOME`. On Windows:

```powershell
.\Construire.ps1
# Includes server and client GameTests:
.\Construire.ps1 -Tests
```

On Linux/macOS:

```sh
sh gradlew build -x runGameTest -x runClientGameTest
sh gradlew runGameTest
sh gradlew runClientGameTest
```

The first build downloads Gradle, Minecraft and dependencies. Client GameTests need a graphical environment and launch a Minecraft window. Follow the Minecraft EULA requirements of the test tooling. Build output is in `build/libs/`.

GeckoLib is pinned to the official Modrinth Maven release `maven.modrinth:geckolib:kSxHvs99`. Its SHA-256 is `0e397991dae6e1aee9a342f80b740fe4fef6d161dc8abe694c6a555317163f82`, matching the original supplied 5.5.7 JAR. An optional local `libs/geckolib-fabric-26.3-5.5.7.jar` takes precedence; `-PusePublishedGeckoLib` forces the Maven dependency. No dependency JAR is bundled in this repository.

`src/main` contains shared/server logic and resources, `src/client` rendering and screens, and `src/gametest` isolated automated tests. Test worlds, local launchers, account information, caches, logs and game runtime files are excluded from this repository.

## Gameplay

- Mimics eat chests and keep their contents in a 27-slot inventory. They disguise themselves and attack when opened.
- A Mimic Lock prevents attacks; removing items may make a locked Mimic flee.
- A Mimic Key tames an unlocked Mimic so it follows its owner. Sneak-right-click toggles sitting; a sitting tame Mimic can be opened.
- Sneak-use a Mimic Heart on a chest to transform it after a short delay.
- Axes deal double damage. Stored items drop on death.
- Original structure spawn hooks, three recipes and the `mimic:default_mimic_loot` loot-table identifier are retained. Modern datapacks use `data/mimic/loot_table/default_mimic_loot.json`.

With command permissions:

```mcfunction
/summon mimic:mimic ~ ~ ~
/give @s mimic:mimic_spawn_egg
/give @s mimic:mimic_key
/give @s mimic:mimic_lock
/give @s mimic:fake_chest
```

## Origin and license

Derived from [LukeGrahamLandry/mimic-mod](https://github.com/LukeGrahamLandry/mimic-mod), branch `1.19`, commit `e5a7443bba2c97e39f3698d9d6940d35aa12a4d6`, version 1.2.38. The upstream MIT license and **Copyright 2021 Luke Graham Landry** are preserved in [LICENSE](LICENSE), which is also included in the built JAR.

Credits: Luke Graham Landry (original code), Redrix and EpicNecromancer1 (original assets), GeckoLib contributors (animation library), and Fabric contributors (loader, API and build tools). Dependencies retain their own licenses. Minecraft belongs to Mojang/Microsoft and is not redistributed here.
