# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v1.5.4] - 2022-10-22
### :sparkles: New Features
- [`fc5b88d`](https://github.com/WinDanesz/AncientSpellcraft/commit/fc5b88df1f014234e48a6f086c68f6c7f9d83417) - Slightly increased the chance of getting Runic Plates *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`f3ad73f`](https://github.com/WinDanesz/AncientSpellcraft/commit/f3ad73fe31c191c3f3984bcde7c1be93cd163ac4) - Added Chinese translation (by ScoTWT) *(PR [#52](https://github.com/WinDanesz/AncientSpellcraft/pull/52) by [@ScoTWT](https://github.com/ScoTWT))*
- [`b1a9caa`](https://github.com/WinDanesz/AncientSpellcraft/commit/b1a9caa7279213e7799a0dbb0ade05b8a1ff65fe) - Added Guardian Blade charm *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`1ac50a7`](https://github.com/WinDanesz/AncientSpellcraft/commit/1ac50a7fb7e3de986c0e94dfe869352ea2e2293f) - Allow closing a permanent transportation portal with recasting the spell. Fixes [#91](https://github.com/WinDanesz/AncientSpellcraft/pull/91) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`bc36a09`](https://github.com/WinDanesz/AncientSpellcraft/commit/bc36a099463f5493c250d0e373668556c2f538e3) - Added a dynamic spell book recipe crafting function *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`729d92d`](https://github.com/WinDanesz/AncientSpellcraft/commit/729d92d6dab5b3d965b86b74bef9846090790d13) - Spell Book bookshelves now accept Ritual Books. Fixes [#106](https://github.com/WinDanesz/AncientSpellcraft/pull/106) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`e2ffe5c`](https://github.com/WinDanesz/AncientSpellcraft/commit/e2ffe5ceee5289660535814bc60d3c51c2bdef01) - Added some helper tooltip to ritual books *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`07e0dd7`](https://github.com/WinDanesz/AncientSpellcraft/commit/07e0dd78c1727cc7034db271e8d6de6d8c6ae32b) - Spectral army summons 8->5 minions *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`cbb471c`](https://github.com/WinDanesz/AncientSpellcraft/commit/cbb471c9c10065b8e60c6d7dc435178f35396492) - New texture for the Wizard's Tankard *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :bug: Bug Fixes
- [`02e9399`](https://github.com/WinDanesz/AncientSpellcraft/commit/02e9399c896f719ec8c9015ec2d2da1acde32e34) - Fixed crash with player bow. Fixes [#75](https://github.com/WinDanesz/AncientSpellcraft/pull/75) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`dc2984a`](https://github.com/WinDanesz/AncientSpellcraft/commit/dc2984a2089955851e2868611a41ce30a87ec9cc) - Should fix battlemage loot. Fixes [#76](https://github.com/WinDanesz/AncientSpellcraft/pull/76) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`6e7e4e9`](https://github.com/WinDanesz/AncientSpellcraft/commit/6e7e4e945f342d5a3501fd60a1711c4843402372) - **spell**: fixed several issues caused by resetting the renderview entity every tick ([#79](https://github.com/WinDanesz/AncientSpellcraft/pull/79)) (bl4ckscor3) *(commit by [@bl4ckscor3](https://github.com/bl4ckscor3))*
- [`b0791f6`](https://github.com/WinDanesz/AncientSpellcraft/commit/b0791f6ce78407da42d9bdcc80d500f72b9010de) - Fixed Sorcery and Healing sage tome recipe *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`c94dc08`](https://github.com/WinDanesz/AncientSpellcraft/commit/c94dc088322d05e31e9bd4a1219bcbd7a44bac90) - Fixed battlemage camp/chest empty loot table *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`4523b95`](https://github.com/WinDanesz/AncientSpellcraft/commit/4523b95081428de0139afc52e7c2cf814805e026) - Fixed apprentice battlemage sword missing its texture *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`9f656da`](https://github.com/WinDanesz/AncientSpellcraft/commit/9f656da4bd6959674b7076a3a786a0d5d7be28c1) - Fixed Purifying Elixir's artefact recipe *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`fca4ff3`](https://github.com/WinDanesz/AncientSpellcraft/commit/fca4ff3a95ae09f481cacc59b8d1a2b3451b8bf8) - Fixed Lithic Literature advancement. Fixes [#64](https://github.com/WinDanesz/AncientSpellcraft/pull/64) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`fa3ac59`](https://github.com/WinDanesz/AncientSpellcraft/commit/fa3ac59d34c76aeb1d90aae2bedc71034d193d5e) - Fixed occassional crash with Bartering Scroll *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`8484e48`](https://github.com/WinDanesz/AncientSpellcraft/commit/8484e4848c8e923a89a8e76003e9887ea690c947) - Fixed Arcane Anvil breaking instantly when hit *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`56a1b0d`](https://github.com/WinDanesz/AncientSpellcraft/commit/56a1b0deed1fdc4d520cd22466ca13ea27e06a8f) - Fixed bad spell overlay icon in inventory for unknown spells *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`6572904`](https://github.com/WinDanesz/AncientSpellcraft/commit/65729047872c54f355befcb4210aafb91f0b7ea4) - Fixed crash with dispenser spell casts (caused by Orb of Suppression's code) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`e6de558`](https://github.com/WinDanesz/AncientSpellcraft/commit/e6de558abc28e9e9b8dc8dad23fa5f44a06b1391) - Fixed class armour material tooltips *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`e76fe68`](https://github.com/WinDanesz/AncientSpellcraft/commit/e76fe68c7bdbfe2abbe660bc66be2c9650583a17) - Living Comet spell now respects the terrainDamage setting, only optionally breaking blocks *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`efba368`](https://github.com/WinDanesz/AncientSpellcraft/commit/efba368c6ba9ef03a260e343f3db501557c4d565) - Fix crashes related to Ring of the Protector. Fixes [#89](https://github.com/WinDanesz/AncientSpellcraft/pull/89) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`dd37924`](https://github.com/WinDanesz/AncientSpellcraft/commit/dd37924f1bd272ae21aba4253aa948aa5e694c57) - Permashrink and Permagrowth spells no longer can be used on players. Fixes [#86](https://github.com/WinDanesz/AncientSpellcraft/pull/86) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`ae3d7b3`](https://github.com/WinDanesz/AncientSpellcraft/commit/ae3d7b3a853f9436da2e84abb99c21ed82181ae5) - Once again fixed Magelight and Candlelight spells. Fixes [#96](https://github.com/WinDanesz/AncientSpellcraft/pull/96) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`3a70ceb`](https://github.com/WinDanesz/AncientSpellcraft/commit/3a70ceb0ebbf93dcd57ef219fbb0705521089d9b) - Fix Molten Earth blast radius not scaling with Blast upgrades. Fixes [#84](https://github.com/WinDanesz/AncientSpellcraft/pull/84) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`9dcbbc7`](https://github.com/WinDanesz/AncientSpellcraft/commit/9dcbbc7ff9a3ca84cf40a98ccfd62ea4483f6d7c) - Fix crash with Soul Scorch. Fixes [#80](https://github.com/WinDanesz/AncientSpellcraft/pull/80) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`e48da85`](https://github.com/WinDanesz/AncientSpellcraft/commit/e48da857c9df362d68527ca64a5b391eb1e49496) - Added crafting recipe for Scribe and Experiment spells. Fixes [#110](https://github.com/WinDanesz/AncientSpellcraft/pull/110) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`f6e708f`](https://github.com/WinDanesz/AncientSpellcraft/commit/f6e708fa9fa80abb56b720caaccb7831e45873b2) - Changed Experiment spell type attack -> alteration *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`064d696`](https://github.com/WinDanesz/AncientSpellcraft/commit/064d696ca64fc76362ccd857111b436318aae445) - Fix crash with dispenser casts on server with the Force Strike spell. Fixes [#107](https://github.com/WinDanesz/AncientSpellcraft/pull/107) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`e77fc9a`](https://github.com/WinDanesz/AncientSpellcraft/commit/e77fc9a369e40e19c9a259406f54c302ce3cbf52) - Fixed ritual book identification. Fixes [#105](https://github.com/WinDanesz/AncientSpellcraft/pull/105) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`d9c3522`](https://github.com/WinDanesz/AncientSpellcraft/commit/d9c352268a320928ae2a2215a01c0733f9e695c7) - Added a dozen missing artefacts to loot tables. Fixes [#102](https://github.com/WinDanesz/AncientSpellcraft/pull/102) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`7c3ebb6`](https://github.com/WinDanesz/AncientSpellcraft/commit/7c3ebb6b06a82bc7437af36285a37110b10712c2) - Fixed crash with Neat/Xaero minimap. Fixes [#81](https://github.com/WinDanesz/AncientSpellcraft/pull/81) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`9e53e61`](https://github.com/WinDanesz/AncientSpellcraft/commit/9e53e61b1895d640c1111e44c53b7b27de89028b) - Fix crash with /listbiomes command, updated Will o' Wisp spell. Fixes [#31](https://github.com/WinDanesz/AncientSpellcraft/pull/31), Fixes [#3](https://github.com/WinDanesz/AncientSpellcraft/pull/3), Fixes [#47](https://github.com/WinDanesz/AncientSpellcraft/pull/47) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`04f9480`](https://github.com/WinDanesz/AncientSpellcraft/commit/04f948099d9f1df792db84364731039aa38cde02) - Fixed the elder futhark encrypted text overflowing in undiscovered Ritual Books. Fixes [#41](https://github.com/WinDanesz/AncientSpellcraft/pull/41), Fixes [#44](https://github.com/WinDanesz/AncientSpellcraft/pull/44) *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :recycle: Refactors
- [`30f4929`](https://github.com/WinDanesz/AncientSpellcraft/commit/30f4929dcf4efe6d7886ae9f4440186d1df854db) - implement IClassSpell for runewords *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :wrench: Chores
- [`1ef7ed6`](https://github.com/WinDanesz/AncientSpellcraft/commit/1ef7ed62c45a870bfa75ff036f212281338e824e) - 1.4.1 version bump *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`98dd740`](https://github.com/WinDanesz/AncientSpellcraft/commit/98dd7407ddf1ef91e2c5fe0f2ea56c2a8a9ac216) - updated README.md *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`a53ebe5`](https://github.com/WinDanesz/AncientSpellcraft/commit/a53ebe5590ba32cc08dde757c60da88c0c9bd032) - updated README.md *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`ef74ebe`](https://github.com/WinDanesz/AncientSpellcraft/commit/ef74ebe983b7499574f154d854af7acf917de3d0) - updated README.md *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`849a33b`](https://github.com/WinDanesz/AncientSpellcraft/commit/849a33b3bc2c64b331888bd54abfd3e6113a00a5) - version bump to 1.4.2 *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`85649cc`](https://github.com/WinDanesz/AncientSpellcraft/commit/85649cc76c8cf0a8598a7532382e26cb2f754fab) - update no_mana_source lang key *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`55fa8a8`](https://github.com/WinDanesz/AncientSpellcraft/commit/55fa8a84e095e060f71880bf99de6006dd2c4970) - updated charm_omnicron.desc in en_us.lang *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`63dd5e5`](https://github.com/WinDanesz/AncientSpellcraft/commit/63dd5e520b14c3ae3cef187aa05b5220a0fbd3eb) - remove None spell from animated spells *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`37a2ced`](https://github.com/WinDanesz/AncientSpellcraft/commit/37a2ced8b49c8d7c0897cde8ec79d7c0758c7a10) - added warlock spell books textures *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`e7e1a09`](https://github.com/WinDanesz/AncientSpellcraft/commit/e7e1a093d4eff430ed449b660bd3530e5aa253fc) - v1.5 *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`b8214d4`](https://github.com/WinDanesz/AncientSpellcraft/commit/b8214d43f073df6f70ffb20048f3cfbcf477153d) - update EntityArcaneBarrier *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`31fb2ef`](https://github.com/WinDanesz/AncientSpellcraft/commit/31fb2efd859298a2be7412fadcc9bb21c63b4fb2) - Stripped metadata from all images, reducing jar size *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`99af8e1`](https://github.com/WinDanesz/AncientSpellcraft/commit/99af8e1bcea9f2b0f9db1ee790d2a7086e05c7d0) - Removed some unused files *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.5.2] - 2022-09-10
### :sparkles: New Features
- [`b1a9caa`](https://github.com/WinDanesz/AncientSpellcraft/commit/b1a9caa7279213e7799a0dbb0ade05b8a1ff65fe) - Added Guardian Blade charm *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`1ac50a7`](https://github.com/WinDanesz/AncientSpellcraft/commit/1ac50a7fb7e3de986c0e94dfe869352ea2e2293f) - Allow closing a permanent transportation portal with recasting the spell. Fixes [#91](https://github.com/WinDanesz/AncientSpellcraft/pull/91) *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :bug: Bug Fixes
- [`e76fe68`](https://github.com/WinDanesz/AncientSpellcraft/commit/e76fe68c7bdbfe2abbe660bc66be2c9650583a17) - Living Comet spell now respects the terrainDamage setting, only optionally breaking blocks *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`efba368`](https://github.com/WinDanesz/AncientSpellcraft/commit/efba368c6ba9ef03a260e343f3db501557c4d565) - Fix crashes related to Ring of the Protector. Fixes [#89](https://github.com/WinDanesz/AncientSpellcraft/pull/89) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`dd37924`](https://github.com/WinDanesz/AncientSpellcraft/commit/dd37924f1bd272ae21aba4253aa948aa5e694c57) - Permashrink and Permagrowth spells no longer can be used on players. Fixes [#86](https://github.com/WinDanesz/AncientSpellcraft/pull/86) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`ae3d7b3`](https://github.com/WinDanesz/AncientSpellcraft/commit/ae3d7b3a853f9436da2e84abb99c21ed82181ae5) - Once again fixed Magelight and Candlelight spells. Fixes [#96](https://github.com/WinDanesz/AncientSpellcraft/pull/96) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`3a70ceb`](https://github.com/WinDanesz/AncientSpellcraft/commit/3a70ceb0ebbf93dcd57ef219fbb0705521089d9b) - Fix Molten Earth blast radius not scaling with Blast upgrades. Fixes [#84](https://github.com/WinDanesz/AncientSpellcraft/pull/84) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`9dcbbc7`](https://github.com/WinDanesz/AncientSpellcraft/commit/9dcbbc7ff9a3ca84cf40a98ccfd62ea4483f6d7c) - Fix crash with Soul Scorch. Fixes [#80](https://github.com/WinDanesz/AncientSpellcraft/pull/80) *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.5.1] - 2022-08-31
### :bug: Bug Fixes
- [`6572904`](https://github.com/WinDanesz/AncientSpellcraft/commit/65729047872c54f355befcb4210aafb91f0b7ea4) - Fixed crash with dispenser spell casts (caused by Orb of Suppression's code) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`e6de558`](https://github.com/WinDanesz/AncientSpellcraft/commit/e6de558abc28e9e9b8dc8dad23fa5f44a06b1391) - Fixed class armour material tooltips *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :wrench: Chores
- [`e7e1a09`](https://github.com/WinDanesz/AncientSpellcraft/commit/e7e1a093d4eff430ed449b660bd3530e5aa253fc) - v1.5 *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.5.0] - 2022-08-30
### :bug: Bug Fixes
- [`9f656da`](https://github.com/WinDanesz/AncientSpellcraft/commit/9f656da4bd6959674b7076a3a786a0d5d7be28c1) - Fixed Purifying Elixir's artefact recipe *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`fca4ff3`](https://github.com/WinDanesz/AncientSpellcraft/commit/fca4ff3a95ae09f481cacc59b8d1a2b3451b8bf8) - Fixed Lithic Literature advancement. Fixes [#64](https://github.com/WinDanesz/AncientSpellcraft/pull/64) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`fa3ac59`](https://github.com/WinDanesz/AncientSpellcraft/commit/fa3ac59d34c76aeb1d90aae2bedc71034d193d5e) - Fixed occassional crash with Bartering Scroll *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`8484e48`](https://github.com/WinDanesz/AncientSpellcraft/commit/8484e4848c8e923a89a8e76003e9887ea690c947) - Fixed Arcane Anvil breaking instantly when hit *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`56a1b0d`](https://github.com/WinDanesz/AncientSpellcraft/commit/56a1b0deed1fdc4d520cd22466ca13ea27e06a8f) - Fixed bad spell overlay icon in inventory for unknown spells *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :recycle: Refactors
- [`30f4929`](https://github.com/WinDanesz/AncientSpellcraft/commit/30f4929dcf4efe6d7886ae9f4440186d1df854db) - implement IClassSpell for runewords *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :wrench: Chores
- [`55fa8a8`](https://github.com/WinDanesz/AncientSpellcraft/commit/55fa8a84e095e060f71880bf99de6006dd2c4970) - updated charm_omnicron.desc in en_us.lang *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`63dd5e5`](https://github.com/WinDanesz/AncientSpellcraft/commit/63dd5e520b14c3ae3cef187aa05b5220a0fbd3eb) - remove None spell from animated spells *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`37a2ced`](https://github.com/WinDanesz/AncientSpellcraft/commit/37a2ced8b49c8d7c0897cde8ec79d7c0758c7a10) - added warlock spell books textures *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`78656b8`](https://github.com/WinDanesz/AncientSpellcraft/commit/78656b813225da561f7449c192a0546ad9fcc7dc) - v1.5 *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.4.2] - 2022-04-15
### :bug: Bug Fixes
- [`b0791f6`](https://github.com/WinDanesz/AncientSpellcraft/commit/b0791f6ce78407da42d9bdcc80d500f72b9010de) - Fixed Sorcery and Healing sage tome recipe *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`c94dc08`](https://github.com/WinDanesz/AncientSpellcraft/commit/c94dc088322d05e31e9bd4a1219bcbd7a44bac90) - Fixed battlemage camp/chest empty loot table *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`4523b95`](https://github.com/WinDanesz/AncientSpellcraft/commit/4523b95081428de0139afc52e7c2cf814805e026) - Fixed apprentice battlemage sword missing its texture *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :wrench: Chores
- [`98dd740`](https://github.com/WinDanesz/AncientSpellcraft/commit/98dd7407ddf1ef91e2c5fe0f2ea56c2a8a9ac216) - updated README.md *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`a53ebe5`](https://github.com/WinDanesz/AncientSpellcraft/commit/a53ebe5590ba32cc08dde757c60da88c0c9bd032) - updated README.md *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`ef74ebe`](https://github.com/WinDanesz/AncientSpellcraft/commit/ef74ebe983b7499574f154d854af7acf917de3d0) - updated README.md *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`849a33b`](https://github.com/WinDanesz/AncientSpellcraft/commit/849a33b3bc2c64b331888bd54abfd3e6113a00a5) - version bump to 1.4.2 *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`85649cc`](https://github.com/WinDanesz/AncientSpellcraft/commit/85649cc76c8cf0a8598a7532382e26cb2f754fab) - update no_mana_source lang key *(commit by [@WinDanesz](https://github.com/WinDanesz))*


[v1.4.2]: https://github.com/WinDanesz/AncientSpellcraft/compare/alpha-1.12.2-1.2.0.103...v1.4.2
[v1.5.0]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.4.2...v1.5.0
[v1.5.1]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.5.0...v1.5.1
[v1.5.2]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.5.1...v1.5.2
[v1.5.4]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.3.0...v1.5.4