# Hunter's Dream 0.2.1 Changelog

All new features and code changes since the last build (0.2.0)  
To see every little change, please head to our [GitHub](https://github.com/TheGamingLord/HuntersDream) and look at the changelog of all the [commits](https://github.com/TheGamingLord/HuntersDream/commits/master) since the 19th of August.

## New Features

- Added wolfsbane plant, flower and potion
- When drunk wolfsbane potion lets werewolf players not transform on full moon and removes infection from players infected with lycantrophy
- Made iron smelting work
- Switched alex and steve arms
- Werewolves now spawn in forests
- Made that minecraft doesn't crash with Hunter's Dream when another mod with silver is installed
- Added `/hdritual` command
- All oredict silver items are now effective against werewolf and undead
- Fixed visual level bug
- Fixed that the ritual secondwerewolfrite skips a level
- Werewolves now don't attack infected players
- Made that werewolves only go up if they can't suffocate in that block
- Werewolves are now four legged when sneaking
- Made that goblins now have the name "Goblin" instead of their profession
- Werewolves don't have name tags anymore
- Made that you can see if an entity is a werewolf when clicking with silver at them
- Fixed that transformed werewolf players don't drop items in multiplayer
- Made that werewolf villagers and goblins now store their werewolf texture
- Fixed four legged werewolf not having eyes
- Maximum werewolf level is now 8 instead of 7
- Edited effects you have as a werewolf

## Removed Things

- Removed furniture tab because we currently have no furniture

## Code Changes

- Added `EffectivenessHelper` class
- Using functional programming a lot more often
- Using interfaces 
- Renamed and moved some methods
- Deprecated some things
- Added more java doc comments
- Fixed bug with `TranslationHelper#getAsLocalizedList`
- Moved `setTextureIndex` method to `ITransformation` interface
- Improved EffectiveAgainstTransformation system
- Added `TransformationEvent`, `TransformingEvent` and `PlayerTransformingEvent`
- Added more info to `TransformationXPEvent`