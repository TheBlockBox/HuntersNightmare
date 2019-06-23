# Hunter's Dream 1.0.0-beta1 Changelog

All new features and code changes since the last build (0.2.2)  
To see every little change, please head to our [GitHub](https://github.com/TheGamingLord/HuntersDream) and look at the changelog of all the [commits](https://github.com/TheGamingLord/HuntersDream/commits/master) since the 24th of August.

## New features

- Added new structures (hunter's camp, werewolf cabin, villager castle etc.)
- Added base for vampires (although it is definitely going to change soon)
- Added new sounds (vampire drinking blood, werewolf howl etc.)
- Added gui based config
- Added campfires and tents
- Improved animations in general
- New werewolf model
- Added skills (natural armor, speed, bite, unarmed etc.), skill bar and skill tab
- Added way better werewolf animation
- Improved silver armor
- Changed all the textures and added new ones
- Changed and added commands
- Added new translations
- Added aconite and monkshood flowers
- Added wolfsbane related things (potions, garlands, petals, etc.)
- Added flintlock guns (musket, pistol and blunderbuss)
- Added base for hunters
- Added hunter armor that can be dyed and have herbs (currently only aconite/monkshood) applied on them


## Removed things

- Removed support for worlds created in older versions of Hunter's Dream
- Removed transformation xp
- Removed some currently not working transformations

## Bug fixes

- Fixed entities being rendered with lag
- Fixed entities not saving their nbt data
- Made werewolves spawn in smaller groups



## Code changes

- Reorganized packages and added lots of new classes
- Created api package with some java docs
- Added new events
- Made the EffectiveAgainstTransformation system solely use events
- Now using newer gradle, mcp and forge version
- Made Transformation be a class and no interface and changed its registering
- Generally cleaned the code up
- Changed item and block registering (no IHasModel anymore!)
- Now using forge blockstates a lot more
- Drastically changed ITransformation and its child classes
- Added lots of new ore dictionary entries
- Now using TextureAtlasSprites
- Added new utilities to the Helper classes
