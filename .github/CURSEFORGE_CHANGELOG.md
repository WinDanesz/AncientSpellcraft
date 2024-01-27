<h2>Version v1.6.0 - 1/27/2024</h2>
<p>&nbsp;</p>
<h3>What's Changed</h3>
<p><span style="font-size: 1.2rem;">&nbsp;</span></p>
<h4><strong>Features</strong></h4>
<p>- Small buff to Candlelight's light level</p>
<p>- Increased Runeword: Imbue charge count 6->10 ; increased Glyph of Alchemy bonus charge modifier 4x->6x</p>
<p>- Beanstalk can now grow vines downwards when sneak-cast.</p>
<p>- Intensifying Focus no longer reduces spell potency and blast. Fixes #180</p>
<p>- Added the Mystic Scroll</p>
<p>- Added many new runewords and charm artefacts</p>
<p>- Added the Runic Shield</p>
<p>- Added the Runic Hammer</p>
<p>- Contingency - Immobilization now help against spiderweb or quicksand. Fixes #200</p>
<p>- Increased Ghost Skeleton Mage spawn chance 15%->30%. Fixes #194</p>
<p>- Hostile common Healing element Skeleton Mages now use Magic Missile instead of Arcane Jammer. (Or Radiant Spark, if Rise of the Animagus is installed)</p>
<p>- Lowered Burrow's tier advanced->apprentice. Fixes #210</p>
<p>- Lowered the blocking position of the Runic Shield to make aiming easier. Fixes #222</p>
<p>- Increased Battlemage sword spell slot count 3->5.</p>
<p>- Increased Battlemage swords' spell potency from 1/3 to 1/2 of the potency bonus of wands (7.5,15,22.5,30)</p>
<p>- Buffed Runeword - Restoration</p>
<p>- Documentation in the Wizard's Handbook</p>
<p>- All Elemental Crystal Ore worldgen parameters are now exposed to config</p>
<p>- Exposed all ore worldgen parameters to configs</p>
<p>- Added Crystal Silver (Nugget) Ore and Astral Diamond (Shard) Ore</p>
<p>- Crafting recipe for 9x Magic Crystal Shards -> 1x Magic Crystal</p>
<p>- Added missing sounds for a bunch of spells</p>
<p>- Added Runeword - Sealbreaker</p>
<p>- Ritual runes can be placed in all directions</p>
<p>- Renamed the old Runic Hammer artefact to Jeweller's Hammer</p>
<p>- Config list option to set what items the Crown of the Merchant King artefact adds to wizard trades (scrolls by default)</p>
<p>- Some more Runesmithing spells</p>
<p>- Spell Blade base mana cost for a basic hit is set to 0 (can be changed in config). Relates to #228</p>
<p>- Dispel Item Curse spell works on modded curses (enchantments) as well, if added to the config. Relates to #227</p>
<p>- Improved the visuals of Anti-Magic Field spell</p>
<p>- Contingency - Immobilization should work against BoP quicksand. Fixes #200</p>
<p>- Silencing Sigil can be dispelled by a second cast. Fixes #176</p>
<p>- Added Runesmithing: Fortify</p>

<h4><strong>Bug Fixes</strong></h4>
<p>- Fixed Phial of Eternal light not giving any light. Fixes #149</p>
<p>- Fixed the Oaken Talisman not giving regeneration effect</p>
<p>- Fixed the Scroll of Imbuement. Fixes #142</p>
<p>- Fixed crash with Summon Quicksand scroll and dispensers</p>
<p>- Fixed Arcane Barrier wobbling (#151) (by 19-creator)</p>
<p>- Sage spell books (Mystic Spell Book) now has its own GUI texture</p>
<p>- Conjure Cake is now a sage spell, added missing spell icon. Fixes #160</p>
<p>- Sage spells can be bound to scrolls. Fixes #167</p>
<p>- Fixed crash with Pocket Library. Fixes #179, fixes #141</p>
<p>- Sage spells can be bound to scrolls</p>
<p>- Golden Scroll holder now only condenses mana if it actually has a Condensing scroll. Fixes #170</p>
<p>- Fixed Skull Sentinel and Spectral Army spell properties (looting,trading, treasure). Fixes #166</p>
<p>- Fix case</p>
<p>- Unbreaking/Mending can no longer be applied to Spell Blades. Fixes #172</p>
<p>- Fixed Condensing Update in Golden Scroll Holder. Fixes #223</p>
<p>- Fixed Curse of Death not killing the player. Fixes #220</p>
<p>- NPCs can cast Pyrokinesis and Mass Pyrokinesis. Fixes #211</p>
<p>- Fixed Animated Wand from Animate Weapon does not benefit from Siphon wand upgrade. This generally fixes any NPCs using a spellcasting item with Siphoning. Fixes #219</p>
<p>- Fixed Conjure Creeper, Nether Guard, Summon Zombie Pigmen spell type (Utility->Minion). Fixes #217</p>
<p>- Pocket Library no longer destroys blocks and has space checks. Fixes #216</p>
<p>- Fixed metamagic effects not being used up by one spellcast</p>
<p>- Fixed Arcane Augmentation not stacking with wand blast and range. Fixes #203</p>
<p>- Added Metamagic - Projectile incompatible spells config list, made Conflagration castable with metamagic (and NPCs). Fixes #201</p>
<p>- Tome Warp reduces the Sage Tome's mana. Fixes #186</p>
<p>- Words of Unbinding can be cast via scroll</p>
<p>- Mushroom strength now scales with spell potency for the Fairy Ring, Sporeling's Aid and Wild Sporeling spells</p>
<p>- Healing Mushrooms now damage undeads</p>
<p>- Fixed Sage Tome Sentience Upgrade tooltip</p>
<p>- Fixed Spell having full damage and spellcasting without battlemage armor. Fixes #239</p>
<p>- Fix json</p>
<p>- Storage Upgrades on spell blades won't cause basic attacks to consume mana when a Runic Shield is held</p>
<p>- Arcane Anvil cannot receive Crystal Silver Ingots to turn into Crystal Silver Plating. Fixes #237</p>
<p>- Fixed Armageddon spell not consuming mana and not summoning meteors in the right direction. Armageddon spell no longer spawns projectiles close to the caster.</p>
<p>- Fix: Fixed Fimbulwinter spell not consuming mana, improved visuals. Fimbulwinter spell no longer spawns projectiles close to the caster.</p>
<p>- Updated many spell properties regarding NPC or dispenser casting. Fixes #236</p>
<p>- Fixed Runeword: Briar. Fixes #232</p>
<p>- Updated Runeword descriptions. Fixes #231</p>
<p>- Fixed wands keeping assigned spells, even after removing spell slots gained from Attunement upgrade scrolls (using Word of Unbinding). Fixes #229</p>
<p>- Fixed wands keeping assigned spells, even after removing spell slots gained from Attunement upgrade scrolls (using Word of Unbinding). Fixes #229</p>
<p>- Crystal Mine and Might & Magic are Ancient Spells set to being castable by NPC Wizards. Fixes #226</p>
<p>- Fixed some incorrect Spellbook descriptions. Fixes #224</p>

<h4><strong>Refactoring</strong></h4>
<p>- Refactor ItemAmnesiaScroll</p>
<p>&nbsp;</p>
<p>As always, the easiest way to get help or feedback is through the Discord server! Find the link on the CurseForge page.</p>
<p>&nbsp;</p>
<p>&nbsp;Need a server? With BisectHosting's&nbsp;automated modpack install, setting up a server can be done in just a few clicks:</p>
<p><span style="font-size: 24px;"><a href="https://www.curseforge.com/linkout?remoteUrl=https%253a%252f%252fbisecthosting.com%252fWinDanesz"><img src="https://www.bisecthosting.com/partners/custom-banners/a2f8bf1e-2d39-48c4-a80d-02ef73cdd36c.png" width="900" height="150" /></a></span></p>