# Validation / Validation — 2026-10-21

## English

Development validation reported for version `1.0` with Minecraft 26.3, Java 25, Fabric Loader 0.19.5, Fabric API 0.164.0+26.3 and GeckoLib 5.5.7:

The 1.0 rebuild passed all eight server tests, the bilingual client scenario with JEI installed, and the standalone Fabric server test using the rebuilt JAR. The previous build and 1.0 JAR differ only in the embedded `fabric.mod.json` version metadata; gameplay code and assets are unchanged. The distributed file is `mimic-fabric-26.3-1.0.jar` (73,756 bytes), SHA-256 `d44a8ccb97bfb4eeffb940170e2238740a6b65c0ff72c42b359e464234a8bb20`.

- Build and translation verification passed: the same seven translation keys are covered in English and French.
- Seven Mimic server GameTests and the Minecraft control test passed.
- An automated scenario passed in a real graphical Minecraft client, including English/French resource reload, rendering, inventory screens, ambush, taming, sitting, animation state and following the owner.
- The delivered mod JAR also passed the eight server tests using the standalone Fabric launcher outside Gradle.

Server tests cover chest item transfer and disguise, inventory/lock persistence, delayed chest conversion, recipes and loot-table loading, taming and owner persistence, locking, double axe damage, spawn mixin targets and a deterministic spawn marker, combat and inventory drops on death. The combat test explicitly ticks the attack goal to isolate its timing from other randomly scheduled goals.

These checks do not establish natural spawn rates across every generated structure, compatibility with third-party modpacks or health-bar mods, long multiplayer sessions, or migration of old 1.19.1 worlds. The old custom `mimichelditems` inventory format has no automatic converter. Forge-specific ToroHealth support is not included, consistent with the original supplied Fabric mod.

Original textures are preserved; the model and animation JSON retain the original content. The spawn egg uses Minecraft's built-in zombie egg sprite reference because the old generic egg model was removed. Runtime Minecraft assets and dependencies are not included in this repository.

Local raw logs, paths, screenshots and test worlds are deliberately excluded. This document summarizes the developer's recorded checks and is not a claim of a GitHub CI run. See the README for reproduction commands.

The publication staging copy was also compiled successfully with `build compileGametestJava -PusePublishedGeckoLib -x runGameTest -x runClientGameTest`, without a local GeckoLib JAR. Translation verification passed. The game scenarios were not rerun in that staging copy.

## Français

Validation de développement rapportée pour `1.0` avec Minecraft 26.3, Java 25, Fabric Loader 0.19.5, Fabric API 0.164.0+26.3 et GeckoLib 5.5.7 :

La reconstruction 1.0 a passé les huit tests serveur, le parcours client bilingue avec JEI installé et les tests du serveur Fabric indépendant utilisant le nouveau JAR. Seule la version des métadonnées embarquées dans `fabric.mod.json` diffère du JAR précédent ; le gameplay et les ressources sont inchangés. Le fichier livré est `mimic-fabric-26.3-1.0.jar` (73 756 octets), SHA-256 `d44a8ccb97bfb4eeffb940170e2238740a6b65c0ff72c42b359e464234a8bb20`.

- Compilation et vérification des sept clés de traduction réussies, avec la même couverture française et anglaise.
- Sept GameTests serveur Mimic et le test témoin Minecraft réussis.
- Parcours automatisé réussi dans un vrai client graphique : rechargement anglais/français, rendu, inventaires, embuscade, apprivoisement, position assise, état d'animation et suivi du propriétaire.
- Huit tests serveur également réussis avec le JAR livré via le lanceur Fabric indépendant de Gradle.

Les tests serveur couvrent le transfert du contenu des coffres, le camouflage, la persistance de l'inventaire et du verrouillage, la conversion différée, les recettes et le butin, l'apprivoisement et le propriétaire, les dégâts de hache, les cibles des mixins, un marqueur d'apparition déterministe, le combat et les objets lâchés à la mort. Le test de combat pilote explicitement l'objectif d'attaque pour isoler sa mesure des autres objectifs aléatoires.

Les taux d'apparition dans chaque structure, les modpacks ou interfaces de santé tiers, les longues sessions multijoueurs et la migration des mondes 1.19.1 ne sont pas validés. Aucun convertisseur automatique de l'ancien inventaire `mimichelditems` n'est fourni. Le support ToroHealth spécifique à Forge est absent, comme dans le mod Fabric d'origine fourni.

Les textures d'origine sont conservées, ainsi que le contenu des JSON de modèle et d'animation. L'œuf utilise une référence au sprite vanilla de l'œuf de zombie, l'ancien modèle générique ayant été supprimé. Les fichiers d'exécution Minecraft et les dépendances ne sont pas inclus dans ce dépôt.

Les journaux bruts locaux, chemins, captures et mondes de test sont exclus. Ce document résume les vérifications enregistrées par le développeur et ne revendique pas une exécution CI sur GitHub. Les commandes de reproduction figurent dans le README.

La copie préparée pour publication a aussi été compilée avec succès avec `build compileGametestJava -PusePublishedGeckoLib -x runGameTest -x runClientGameTest`, sans JAR GeckoLib local. La vérification des traductions réussit. Les parcours en jeu n'ont pas été réexécutés dans cette copie.
