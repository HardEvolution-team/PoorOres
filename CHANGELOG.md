# Changelog

## 1.0.4
### Fixed
- Redstone nugget had a missing (purple/black) texture: its colour was read from the 1.7 texture name redstone.png, which is redstone_dust.png in 1.12.

## 1.0.3
### Performance
- Ore veins are written straight into chunk sections instead of going through the world for every block. Profiled on a large modpack, poor-ore generation was about a third of server time while flying into new terrain; it is now a small fraction of that.

## 1.0.2
### Changed
- The original mod file is now downloaded automatically on first start (needs ArchaicLib 1.42.0+): sources and mirrors are listed in `config/archaiclib/originals/` and can be edited there. If the download fails, a banner on the main menu links to the manual download.

## 1.0.1
- First release of the 1.12.2 (Cleanroom) port of PoorOres by Xexanos. Requires ArchaicLib 1.35.0+.
