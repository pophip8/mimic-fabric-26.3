# Mimic — Minecraft 26.3 / Fabric

[English](README.md) · [Validation](docs/VALIDATION.md) · [Licence MIT](LICENSE)

**Mimic 1.0**, portage communautaire vers Minecraft Java **26.3** avec Fabric, en français et en anglais. Les Mimics se déguisent en coffres, stockent des objets, attaquent les joueurs curieux et peuvent être verrouillés ou apprivoisés.

Le mod et le code d'origine sont de **Luke Graham Landry**. Les modèles, textures et animations d'origine sont crédités à **Redrix** et **EpicNecromancer1**. Ce dépôt maintient un portage communautaire sans revendiquer la création du travail original ni une approbation de ses auteurs.

## État

Version **1.0**. La compilation, sept GameTests serveur Mimic plus le test témoin Minecraft, ainsi qu'un parcours client automatisé bilingue ont réussi dans l'environnement de développement. Le JAR construit a également passé les tests serveur avec le lanceur Fabric indépendant. Voir [les validations et limites](docs/VALIDATION.md).

Cette première publication contient les sources, sans release binaire. La compatibilité avec les modpacks tiers et la migration automatique des inventaires Mimic de Minecraft 1.19.1 ne sont pas validées. Aucun convertisseur de cet ancien format n'est fourni.

## Prérequis et installation

| Composant | Version |
| --- | --- |
| Minecraft Java | 26.3 |
| JDK / Java | 25 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.164.0+26.3 |
| GeckoLib | **5.5.7 pour Fabric 26.3** |

Compiler le mod, puis copier `mimic-fabric-26.3-1.0.jar` de `build/libs/` dans le dossier `mods/` de l'instance avec Fabric API et [GeckoLib 5.5.7 Fabric 26.3 exact](https://modrinth.com/mod/geckolib/version/kSxHvs99). En multijoueur, installer ces mods sur le serveur et les clients. Les fichiers `-sources.jar` et `-testmod.jar` ne sont pas le mod jouable. Choisir Français (France) ou English (US) dans la langue de Minecraft.

Utiliser une instance de test séparée et sauvegarder les mondes avant de tester le portage avec des sauvegardes existantes.

## Compilation et tests

Installer un JDK 25 et définir `JAVA_HOME`. Sous Windows :

```powershell
.\Construire.ps1
# Avec les GameTests serveur et client :
.\Construire.ps1 -Tests
```

Sous Linux/macOS :

```sh
sh gradlew build -x runGameTest -x runClientGameTest
sh gradlew runGameTest
sh gradlew runClientGameTest
```

La première compilation télécharge Gradle, Minecraft et les dépendances. Le test client nécessite un environnement graphique et ouvre une fenêtre Minecraft. Respecter les conditions d'acceptation de l'EULA Minecraft demandées par les outils de test. Les JAR sont générés dans `build/libs/`.

GeckoLib est épinglé à la publication Maven Modrinth officielle `maven.modrinth:geckolib:kSxHvs99`. Son SHA-256 est `0e397991dae6e1aee9a342f80b740fe4fef6d161dc8abe694c6a555317163f82`, identique au JAR 5.5.7 fourni initialement. Un JAR local facultatif `libs/geckolib-fabric-26.3-5.5.7.jar` est prioritaire ; `-PusePublishedGeckoLib` force Maven. Aucun JAR de dépendance n'est inclus dans ce dépôt.

`src/main` contient la logique commune/serveur et les ressources ; `src/client`, le rendu et les écrans ; `src/gametest`, les tests automatisés isolés. Les mondes, launchers locaux, comptes, caches, journaux et fichiers d'exécution du jeu sont exclus du dépôt.

## Jouer

- Les Mimics mangent les coffres et conservent leur contenu dans 27 cases. Ils se camouflent et attaquent lorsqu'on les ouvre.
- Le cadenas neutralise l'attaque ; retirer un objet peut faire fuir le Mimic verrouillé.
- La clé apprivoise un Mimic non verrouillé, qui suit son propriétaire. Le clic droit accroupi permet de l'asseoir ou de le relever ; son inventaire s'ouvre lorsqu'il est assis.
- Utiliser un cœur de Mimic sur un coffre en étant accroupi le transforme après un court délai.
- Les haches infligent le double de dégâts. Les objets stockés sont lâchés à la mort.
- Les points d'apparition dans les structures, les trois recettes et l'identifiant de butin `mimic:default_mimic_loot` sont conservés. Les datapacks modernes utilisent `data/mimic/loot_table/default_mimic_loot.json`.

Commandes avec les permissions nécessaires :

```mcfunction
/summon mimic:mimic ~ ~ ~
/give @s mimic:mimic_spawn_egg
/give @s mimic:mimic_key
/give @s mimic:mimic_lock
/give @s mimic:fake_chest
```

## Origine et licence

Sources : [LukeGrahamLandry/mimic-mod](https://github.com/LukeGrahamLandry/mimic-mod), branche `1.19`, commit `e5a7443bba2c97e39f3698d9d6940d35aa12a4d6`, version 1.2.38. La licence MIT amont et **Copyright 2021 Luke Graham Landry** sont conservés dans [LICENSE](LICENSE), également inclus dans le JAR construit.

Crédits : Luke Graham Landry (code original), Redrix et EpicNecromancer1 (ressources originales), contributeurs GeckoLib (animations) et Fabric (loader, API, outils). Les dépendances conservent leurs propres licences. Minecraft appartient à Mojang/Microsoft et n'est pas redistribué ici.
