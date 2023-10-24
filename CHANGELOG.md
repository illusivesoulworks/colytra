# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
Prior to version 6.0.0, this project used MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [6.0.2+1.19.2] - 2023.10.23
### Changed
- Updated to SpectreLib 0.12.6
- [Fabric] Requires Fabric Loader >=0.14.23

## [6.0.1+1.19.2] - 2023.05.28
### Changed
- Updated to SpectreLib 0.12.4+1.19.2

## [6.0.0+1.19.2] - 2022.08.11
### Changed
- Updated to Minecraft 1.19.2
- [Forge] Updated to Forge 43+
- [Fabric] Updated to Fabric API 0.59.0+

## [6.0.0+1.19.1] - 2022.07.30
### Changed
- Updated to Minecraft 1.19.1
- [Forge] Updated to Forge 42+
- [Forge] Updated to Caelus 1.19.1-3.0.0.4+
- [Fabric] Updated to Fabric API 0.58.5+

## [6.0.0-beta.2+1.19] - 2022.07.17
### Changed
- [Forge] Updated to and requires Forge 41.0.94 or above
- Updated to SpectreLib 0.8.1+1.19
- Configuration file has been relocated to the `config` folder
- Configuration file in the `defaultconfigs` folder will be copied to the `config` folder upon initialization
- Local configuration file has been relocated to the `localconfigs` folder in the game directory

## [6.0.0-beta.1+1.19] - 2022.07.07
### Changed
- Merged Forge and Fabric versions of the project together using the [MultiLoader template](https://github.com/jaredlll08/MultiLoader-Template)
- Configuration system is now provided by SpectreLib
- Configuration file is now located in the root folder's defaultconfigs folder
- Changed to [Semantic Versioning](http://semver.org/spec/v2.0.0.html)
- Updated to Minecraft 1.19
- [Forge] Updated to Forge 41+
- [Fabric] Updated to Fabric API 0.55.2+

## [1.18.1-5.2.0.2] - 2022.03.30
### Fixed
- Fixed compatibility with Cyclic's Multi-Jump enchantment [#55](https://github.com/TheIllusiveC4/Colytra/issues/55)

## [1.18.1-5.2.0.1] - 2022.01.15
### Fixed
- Fixed crash when equipping chestplate with elytra [#54](https://github.com/TheIllusiveC4/Colytra/issues/54)

## [1.18.1-5.2.0.0] - 2022.01.15
### Changed
- Updated to Minecraft 1.18.1
- Updated to Forge 38.0+

## [1.17.1-5.2.0.0] - 2022.01.15
### Changed
- Updated to Minecraft 1.17.1
- Updated to Forge 37.0+

## [1.16.5-5.1.1.2] - 2021.11.09
### Changed
- Improved Mending behavior (thanks YukkuriC!) [#47](https://github.com/TheIllusiveC4/Colytra/pull/47)

## [1.16.5-5.1.1.1] - 2021.05.14
### Added
- Added German localization (thanks muffinbarde!) [#43](https://github.com/TheIllusiveC4/Colytra/pull/43)
### Changed
- Updated Aether integration (thanks bconlon1!) [#45](https://github.com/TheIllusiveC4/Colytra/pull/45)

## [1.16.5-5.1.1.0] - 2021.04.22
### Added
- Added Aether integration

## [1.16.4-5.1.0.0] - 2020.11.16
### Changed
- Updated to Minecraft 1.16.4
- Inner elytra stacks will no longer retain damage/cost data when attaching to chestplates in Unison/Perfect mode [#38](https://github.com/TheIllusiveC4/Colytra/issues/38)

## [1.16.3-5.0.0.1] - 2020.09.27
### Changed
- Updated to Minecraft 1.16.3

## [1.16.2-5.0.0.0] - 2020.08.16
### Changed
- Updated to Minecraft 1.16.2
- Tooltips now display custom names when applicable

## [1.16.1-4.0.0.1] - 2020.07.25
### Fixed
- Fixed unison setting using elytra durability instead of chestplate

## [1.16.1-4.0.0.0] - 2020.07.02
### Changed
- Ported to 1.16.1 Forge

## [1.15.2-3.0.0.0] - 2020.02.11
### Changed
- Ported to 1.15.2 Forge

## [1.14.4-2.1.0.0] - 2019.11.28
**Warning: This update has a small possibility of voiding your existing elytra attachments. As a precaution, it's advised to detach colytras if possible before updating.**
### Fixed
- Fixed desyncing issues on dedicated servers [#31](https://github.com/TheIllusiveC4/Colytra/issues/31)

## [1.14.4-2.0.0.1] - 2019.11.12
### Fixed
- Fixed some irregularities with elytras breaking while attached to chestplates

## [1.14.4-2.0.0.0] - 2019.09.14
### Changed
- Updated to Forge RB 28.1.0
- Updated to Caelus 1.14.4-1.0

## [1.14.4-2.0.0.0-beta5] - 2019.09.09
### Added
- Chinese localization (thank you tian0501011)
### Fixed
- Fixed chestplates being able to fly without elytras when using Unison or Perfect modes

## [1.14.4-2.0.0.0-beta4] - 2019.08.07
### Changed
- Updated to Forge 1.14.4-28.0.45

## [1.14.4-2.0.0.0-beta3] - 2019.08.04
### Changed
- Ported to 1.14.4

## [1.13.2-2.0.0.0-beta2] - 2019.07.09
### Fixed
- Fixed Colytra not working on unbreakable chestplates

## [1.13.2-2.0.0.0-beta1] - 2019.04.01
### Added
- Config option for colytra flight energy usage
### Changed
- Ported to 1.13.2 Forge
- Colytra behavior has been condensed into a single config option "Colytra Mode" which handles durability, merging behavior, etc.
### Removed
- Removed Bauble Elytra - Feature split into [Curious Elytra](https://minecraft.curseforge.com/projects/curious-elytra)
- Removed coremodding and colytra toggle keybinding - Features split into [Caelus API](https://minecraft.curseforge.com/projects/caelus)

## [1.12.2-1.2.0.4] - 2019.11.17
### Changed
- Updated required Forge to 23.5.2779. This has been required since the last update, but now it will explicitly tell you rather than just crash.

## [1.12.2-1.2.0.3] - 2019.08.04
### Fixed
- Fixed crashes related to reflection helpers

## [1.12.2-1.2.0.2] - 2019.07.31
### Fixed
- Fixed elytra bauble not rendering with tags

## [1.12.2-1.2.0.1] - 2019.07.06
### Fixed
- Fixed colytra not working when sharing durability with an unbreakable chestplate

## [1.12.2-1.2.0.0] - 2019.04.26
### Changed
- Restricted scope of colytra capability to players only

## [1.12.2-1.1.1.0] - 2019.03.23
### Added
- Added config option for separating colytra in crafting table [#15](https://github.com/TheIllusiveC4/Colytra/issues/15)
- Added toggle feature for bauble elytras [#17](https://github.com/TheIllusiveC4/Colytra/issues/17)

## [1.12.2-1.1.0.4] - 2019.03.09
### Added
- Added Chinese localization files (thank you tian051011)

## [1.12.2-1.1.0.3] - 2019.01.26
### Added
- Added message over hotbar when toggling colytras on/off [#18](https://github.com/TheIllusiveC4/Colytra/issues/18)
### Fixed
- Fixed NPE crash when trying to attach elytras to chestplates using some modded anvils [#19](https://github.com/TheIllusiveC4/Colytra/issues/19)

## [1.12.2-1.1.0.2] - 2019.01.21
### Fixed
- Fixed duplication bug when equipping bauble elytra through offhand [#16](https://github.com/TheIllusiveC4/Colytra/issues/16)

## [1.12.2-1.1.0.1] - 2018.12.05
### Changed
- Textures now use the updated, more vibrant Quark Dyed Elytra textures when applicable

## [1.12.2-1.1.0.0] - 2018.07.18
**Warning: Delete your config file and regenerate it when updating to this version**
### Changed
- Blacklist and whitelist config options have been collapsed into a single item list
- Updated to Forge 14.23.4.2705
### Fixed
- Astral Sorcery Vicio Mantle not working when loaded with Colytra [#16](https://github.com/TheIllusiveC4/Colytra/issues/10)

## [1.12.2-1.0.4.3] - 2017.12.22
### Added
- Added two new configuration options:
  - Permission Mode: "Blacklist" or "Whitelist" - Sets how to determine which chestplates are eligible for elytra attachments
  - Disable Bauble - If Baubles is installed, lets you disable the elytra bauble
### Changed
- Modified tooltip coding so that "Elytra" on colytras now translates to the client language
- Elytra baubles now auto-equip when you right click them in your hand

## [1.12.2-1.0.4.2] - 2017.11.16
### Fixed
- Fixed Mending enchantment incorrectly calculating elytra durability

## [1.12.2-1.0.4.1] - 2017.10.24
### Fixed
- Fixed cape rendering issues

## [1.12.2-1.0.4.0] - 2017.10.24
### Added
- Added config options for colytra durability usage: Normal, Infinite, Chestplate
### Changed
- Colytras now uses RF/Forge Energy from a chestplate if "Chestplate" is selected (at a rate of 1000 RF/sec)

## [1.12.2-1.0.3.0] - 2017.10.23
### Fixed
- Fixed backwards compatibility with 1.12.1

## [1.12.2-1.0.2.0] - 2017.10.20
### Fixed
- Fixed Mending enchantment not working for elytras in the bauble body slot

## [1.12.2-1.0.1.0] - 2017.10.19
### Changed
- Elytra bauble tooltips now show Quark dye information properly

## [1.12.2-1.0.0.0] - 2017.10.19
- Initial release
