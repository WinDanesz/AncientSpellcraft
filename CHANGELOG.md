# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v1.5.10] - 2023-04-22
### :sparkles: New Features
- [`db58f07`](https://github.com/WinDanesz/AncientSpellcraft/commit/db58f07fbd5b906e01ab88000d38c06fdd1bfb7e) - Words of Unbinding supports sage tomes and battlemage swords
- [`6176797`](https://github.com/WinDanesz/AncientSpellcraft/commit/6176797afd7ffa73668074fbc72862380dd847ed) - Bookshelf support for the rare scrolls
- [`c971e18`](https://github.com/WinDanesz/AncientSpellcraft/commit/c971e180eacd1a7dce5d69827207121c52bc9f14) - Mana flask now works with sage tomes *(commit by [@notveryAI](https://github.com/notveryAI))*

### :bug: Bug Fixes
- [`0d29f59`](https://github.com/WinDanesz/AncientSpellcraft/commit/0d29f5970ba3d5bcf27f9f6863ab89656eaf48e8) - Condensing Updates now recharge Sage Tomes in the inventory as well. Fixes[#148](https://github.com/WinDanesz/AncientSpellcraft/pull/148) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`ef37a2a`](https://github.com/WinDanesz/AncientSpellcraft/commit/ef37a2a248a9588eec7562643e991d288b39b7a2) - Golden Scroll Holder no longer voids the current scroll when trying to swap scrolls. Fixes [#146](https://github.com/WinDanesz/AncientSpellcraft/pull/146) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`896b8a2`](https://github.com/WinDanesz/AncientSpellcraft/commit/896b8a2b55bcf64ef6eea7c49ecd6188cf88bcf4) - Fixed Omnicron applying wrong mana from shards/grand crystals. Fixes [#136](https://github.com/WinDanesz/AncientSpellcraft/pull/136) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`f9be5fc`](https://github.com/WinDanesz/AncientSpellcraft/commit/f9be5fce056616762c1e07de0f65bffc6f2f0e92) - Unknown crafted ancient books become discovered  upon crafting at the Scribing Desk. Fixes [#134](https://github.com/WinDanesz/AncientSpellcraft/pull/134) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`83ef62b`](https://github.com/WinDanesz/AncientSpellcraft/commit/83ef62b19dc5b37ad2c60094a3dca44c222e168b) - Fixed Glyph of Illumination not providing light *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :wrench: Chores
- [`4727772`](https://github.com/WinDanesz/AncientSpellcraft/commit/4727772fea97cb6413a61bc59758eb864880216b) - added missing import *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`82b1d5c`](https://github.com/WinDanesz/AncientSpellcraft/commit/82b1d5c85f0a73d4a0635dde3f8f473275ebdcf9) - fix some compile errors *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`a17c629`](https://github.com/WinDanesz/AncientSpellcraft/commit/a17c629332e6b1f4166d7011476e93101191efcb) - scroll color updates *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.5.9] - 2023-02-02
### :bug: Bug Fixes
- [`be8eccb`](https://github.com/WinDanesz/AncientSpellcraft/commit/be8eccbc8895a6433d0bfc8e10eb62d11ed159f8) - Fixed server side particle crash with Turn Undead spell. Fixes [#128](https://github.com/WinDanesz/AncientSpellcraft/pull/128) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`d762436`](https://github.com/WinDanesz/AncientSpellcraft/commit/d762436fa066ccbf7d0fffa9b2169f940915c897) - Added  Runeword - Fury missing a spell icon. Fixes [#126](https://github.com/WinDanesz/AncientSpellcraft/pull/126) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`dd8a49d`](https://github.com/WinDanesz/AncientSpellcraft/commit/dd8a49ddbd2c103aac7b3ad095702d49b2e2ddc4) - Fix server side crash with Starve spell. Fixes [#130](https://github.com/WinDanesz/AncientSpellcraft/pull/130) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`f61c66f`](https://github.com/WinDanesz/AncientSpellcraft/commit/f61c66f38af37e77c4fc9b69ca617f13350854b8) - Fixed the bug with Runic Plates appearing as [Empty], also fixes existing empty plates if you right click them. *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`9c34d30`](https://github.com/WinDanesz/AncientSpellcraft/commit/9c34d306c8e0475ea2f9940b7d524d32ce5f1b19) - Fixed bug with sages not being able to pass through their own Arcane Wall blocks. Fixes [#129](https://github.com/WinDanesz/AncientSpellcraft/pull/129) *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.5.8] - 2023-01-01
### :bug: Bug Fixes
- [`203cdf7`](https://github.com/WinDanesz/AncientSpellcraft/commit/203cdf7ceed053abca4827a1fe61285188986454) - Fixed occasional crash with the Experiment spell on dedicated servers. Fixes [#125](https://github.com/WinDanesz/AncientSpellcraft/pull/125) *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.5.7] - 2022-12-30
### :sparkles: New Features
- [`1c17f53`](https://github.com/WinDanesz/AncientSpellcraft/commit/1c17f537e814da3c219e2a5de2dd1dc982aafb2e) - Added the Scroll of Imbuement, a new rare scroll type *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`90de1bb`](https://github.com/WinDanesz/AncientSpellcraft/commit/90de1bb256157ed1239652680ea16dc242c4f857) - Added the Seal of the Scrollmaster *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :bug: Bug Fixes
- [`521c461`](https://github.com/WinDanesz/AncientSpellcraft/commit/521c4618b28463e0c352c65fa8574c8bd7e82d02) - Fixed book description of the Experiment spell *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`178d7d6`](https://github.com/WinDanesz/AncientSpellcraft/commit/178d7d6a61a61f6418ad2d877a87a4146306d770) - Fixed the epic_artefacts loottable *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`cbf98b4`](https://github.com/WinDanesz/AncientSpellcraft/commit/cbf98b453c86ebb1985d6e322c7adcac9755d4d7) - Fixed Scroll of Amnesia not being consumed on use *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`16f48a7`](https://github.com/WinDanesz/AncientSpellcraft/commit/16f48a7023a5ea45b6c52f728cea49d3cc9af433) - Fixed Scrolls of Duplication instantly using up/activating the duplicated items when conditions are met *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :wrench: Chores
- [`be2d1b3`](https://github.com/WinDanesz/AncientSpellcraft/commit/be2d1b3299c003c77a27fb80488b2594f156bd61) - Updated remnant spawn position *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.5.6] - 2022-12-28
### :sparkles: New Features
- [`6438ebc`](https://github.com/WinDanesz/AncientSpellcraft/commit/6438ebc0b5240fa20669399ab8a6c2f83c15d9a1) - Update duplication scroll description *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`4ba196a`](https://github.com/WinDanesz/AncientSpellcraft/commit/4ba196a77f0db30824e24f212270f3ed8c3cb4dc) - Changed Skull Sentinel spell element: ancient->necromancy *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`4e21c4b`](https://github.com/WinDanesz/AncientSpellcraft/commit/4e21c4b228dda3fa7d70d525021d08bdf8d31da8) - More tooltips for the Runic Plates *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`4f45939`](https://github.com/WinDanesz/AncientSpellcraft/commit/4f459396521d612eb04efc808f2d0f66710bf07f) - Skull Sentinel emits a redstone signal when it detects a hostile creature *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`4f40fe4`](https://github.com/WinDanesz/AncientSpellcraft/commit/4f40fe43beb5f879dfd06ba136e6426f23a3dee0) - Added some new artefacts and runewords *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`c85a041`](https://github.com/WinDanesz/AncientSpellcraft/commit/c85a0418486f4592db50ab2f353db23219d1b4fc) - Added a config property to the ritual json files to allow disabling individual rituals. Fixes [#123](https://github.com/WinDanesz/AncientSpellcraft/pull/123) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`2df82dc`](https://github.com/WinDanesz/AncientSpellcraft/commit/2df82dc57dc2887d96bb0f2698d412bda3223bd6) - More runewords and artefacts *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`919ad3b`](https://github.com/WinDanesz/AncientSpellcraft/commit/919ad3b93a39544291fbb51969749064fedbd1e7) - Skeleton Mages are now spawning naturally, also added a rare, more powerful variant *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :bug: Bug Fixes
- [`307dd82`](https://github.com/WinDanesz/AncientSpellcraft/commit/307dd82e6d2900858c4fd0ac5685c637d88b4a85) - Death Mark spell is no longer continuous. Fixes [#114](https://github.com/WinDanesz/AncientSpellcraft/pull/114) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`6d40bfc`](https://github.com/WinDanesz/AncientSpellcraft/commit/6d40bfc72efefc74cd3694ea243133cfdbe3e755) - Added missing icon for Death Mark spell. Death Mark now also prints the death coordinates of the marked entity. Fixes [#112](https://github.com/WinDanesz/AncientSpellcraft/pull/112) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`1102c86`](https://github.com/WinDanesz/AncientSpellcraft/commit/1102c86d6a81d3b111d8752d64e542d0611323da) - Fixed Skull Sentinel's book item, fixing its assignment to Necromancy. *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`1508459`](https://github.com/WinDanesz/AncientSpellcraft/commit/1508459b85841243f3d412a64de21c9edcea8ad3) - Sage tomes are no longer enchantable and won't have the enchantment glint after every 4th cast. Fixes [#111](https://github.com/WinDanesz/AncientSpellcraft/pull/111) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`1e05cd2`](https://github.com/WinDanesz/AncientSpellcraft/commit/1e05cd2d341e89a20ee70c1e8d88cc30711b0c74) - Fixed the 'Extension' sage spell not going on cooldown after a successful cast. Fixes [#117](https://github.com/WinDanesz/AncientSpellcraft/pull/117) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`1b948fa`](https://github.com/WinDanesz/AncientSpellcraft/commit/1b948fa3b54298f153d9a500efc957af017bee8f) - Fixed the sage tome upgrading with the mystic lectern *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`5baf638`](https://github.com/WinDanesz/AncientSpellcraft/commit/5baf638f00a01286509fd49bc233399e876d20dd) - Fixed biome tag's recipe *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :recycle: Refactors
- [`d425f9e`](https://github.com/WinDanesz/AncientSpellcraft/commit/d425f9e498349096021604b32ddcd6505e29be13) - Optimized Dimension Anchor's code (thanks for the tip, 19) *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :wrench: Chores
- [`1872292`](https://github.com/WinDanesz/AncientSpellcraft/commit/1872292add42c6d0d63b6f007d141bc87df460f4) - some small bugfixes *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`82f4640`](https://github.com/WinDanesz/AncientSpellcraft/commit/82f46403142dc2c796f15a194470c08cf7e59cbe) - fix typo *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`0ab37a5`](https://github.com/WinDanesz/AncientSpellcraft/commit/0ab37a5b7b961ffde6901b4bbe75bfd3c4599a7d) - added the Scorching Belt *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`748a398`](https://github.com/WinDanesz/AncientSpellcraft/commit/748a398795f6549e7566d219d732eda80d68ea4e) - Reordered creative tab items *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.5.5] - 2022-10-31
### :bug: Bug Fixes
- [`7ab1e3c`](https://github.com/WinDanesz/AncientSpellcraft/commit/7ab1e3cf4a3287f89251f22e9f0527c0152546a6) - Fix crash with Death Mark spell *(commit by [@WinDanesz](https://github.com/WinDanesz))*


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
[v1.5.5]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.5.4...v1.5.5
[v1.5.6]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.5.5...v1.5.6
[v1.5.7]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.5.6...v1.5.7
[v1.5.8]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.5.7...v1.5.8
[v1.5.9]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.5.8...v1.5.9
[v1.5.10]: https://github.com/WinDanesz/AncientSpellcraft/compare/v1.5.9...v1.5.10