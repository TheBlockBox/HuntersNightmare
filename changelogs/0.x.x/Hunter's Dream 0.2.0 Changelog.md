# Hunter's Dream 0.2.0 Changelog

All new features and code changes since the last build (0.1.0)
To see every little change, please head to our [GitHub](https://github.com/TheGamingLord/HuntersDream) and look at the changelog of all the [commits](https://github.com/TheGamingLord/HuntersDream/commits/master) since the 13th of July.

## New Features

- The 0.2.0 now supports worlds made in version 0.1.0, so worlds from the 0.1.0 build **should** load without any exceptions or errors, **though this may not always be the case**. You should at least save your world before updating or create a new one if possible
- We now have a correct working levelling system (for most of the levels you need to get transformation xp, but for some you have to perform certain rituals). When you level up, you often get new buffs and advantages.
- Silver is now more effective against werewolves (= deals more damage) 
- Silver armour now deals thorns when damaged by a werewolf
- Goblins and Villagers now have a small chance of being a werewolf
- Goblin and Villager werewolves now transform to a werewolf at full moon
- Renamed and improved commands (for more info look at our [Wiki](https://github.com/TheGamingLord/HuntersDream/wiki))
- Fixed creative tabs not working
- Made new werewolf models and improved animation
- Added four legged werewolf animation for player when sprinting
- Added transformation xp bar (shows your current transformation xp)
- We now use GUI configs!
- Werewolves now get 5 transformation xp for staying one minute under the full moon
- Werewolves now get 10 transformation xp when they kill a mob
- Renamed Pure Silver to just Silver
- Silver ore is back now!
- Fixed goblin texture bug
- Silver Axe is now a real axe and not an item
- Added Update Checker (the forge one, not that annoying thing that spams you with messages when you have an outdated version)
- You now retain your transformation stats when you die
- Added infection
- Werewolves get damage when holding silver items
- When you give a player silver, you get a chat message telling you if the player was a werewolf
- Added Werewolf Enchanting Block. When you click it and are a werewolf, you "perform a ritual" and then get level 1 (this is the only way to get level 1)
- Added rituals and saving of rituals
- You now slowly transform into a werewolf (you get potion effects, your hear a heartbeat sound and you get a chat message informing you about your current situation)

## Removed Things

- Removed Lupum Vocant effect
- Removed werewolf villager

## Code Changes

- Added additional files to our GitHub
- Created interfaces `IEffectiveAgainstTransformation` and `IArmorEffectiveAgainstTransformation`
- Added `WerewolfEventHandler` and `ChanceHelper`
- Optimised lots of code 
- Added base for no control
- Every chat message is now translatable
- Added `TransformationHelper#setXP` method to set xp, send packet, inform about levelup and more
- Changed group name to `theblockbox.huntersdream` (before: `huntersdream`)
- Renamed `HuntersDreamPacketHandler` to `PacketHandler`
- Removed the `IMetaName` interface
- The `Transformations` enum now uses `ResourceLocations` instead of integers as identifiers
- `CommonProxy` became `ServerProxy` and the two proxies now implement the new interface `IProxy`
- Removed all `@SideOnly` annotations
- Added some new Exceptions
- Using more RegistryEvents now
- Added some better AI to entities
- Added `ExecutionPath` to trace back issues easier
- Added some new capabilities and interfaces
- Added `TransformationXPEvent` (called when transformation xp is set with TransformationHelper#setXP)
- Removed some old models
- Now using a logger instead of `System.out.println`
- Added `Rituals` enum
- Added `TransformationEntry` class for all mods that want to add their own transformation

## Other Changes

- Changed Hunter's Dream logo
- We are now called "TheBlockBox" instead of "PixelEyeStudios" to avoid copyright issues
