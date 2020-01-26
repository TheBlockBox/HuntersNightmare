# Hunter's Dream 1.1.0 Changelog

All new features and code changes since the last build (1.0.0)  
To see every little change, please head to our [GitHub](https://github.com/TheGamingLord/HuntersDream) and look at the changelog of all the [commits](https://github.com/TheGamingLord/HuntersDream/commits/master) since the 14th of July 2019.

## New features

- More translations (British English (VampireRedEye), Brazilian Portugese (Nick Irregular), Mexican Spanish (DragonDKaz), French (MatLeJPP), Russian (Naoki) and Standard German (RedstoneTim))
- Added revolvers, hunting rifles and pump shotguns
- Added new ammunition with a normal and silver variant
- Changed gun stats and added reticles and rifle scopes
- New textures and sound effects
- Tent rewrite
- Added healing herbs, glow fern, magma flowers, poison ivies and wither moss (can all be combined with Hunter Armor) with their own world generation
- Added a progress bar and a new recipe for the spinning wheel
- Added projectile resistance for werewolves
- Added work in progress transformations
- Implemented hunters and changed their model slightly

## Removed things

- Removed work in progress transformations from bestiary
- Removed goblin werewolves

## Bug fixes

- Made werewolves not despawn
- Limited werewolf spawns to a maximum of one per chunk
- Fixed some registry problems
- Lowered skill costs
- Fixed villagers loosing their transformation on rare occurences
- Rendered werewolves unable to ride horses

## Code changes

- Added more gun classes and theblockbox.huntersdream.items.gun package
- Made changes to IAmmunition, IGun and ItemGun
- Added BlockFlowerBase, BlockEffectVine and 
- Added more ore dictionary names
- Added WerewolfTransformingEvent.WerewolfTransformingReason.WOLFSBANE
- Reorganized a lot of classes