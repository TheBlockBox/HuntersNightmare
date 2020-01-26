# Hunter's Dream 0.2.2 Changelog

All new features and code changes since the last build (0.2.1)  
To see every little change, please head to our [GitHub](https://github.com/TheGamingLord/HuntersDream) and look at the changelog of all the [commits](https://github.com/TheGamingLord/HuntersDream/commits/master) since the 24th of August.

## New Features

- Fixed that only goblins that were werewolves spawned
- Fixed that goblins had the same chance of being a werewolf as villagers
- Lowered spawn chance for werewolves
- Werewolf hands now change colo(u)r when werewolf colo(u)r changes
- You now don't get thousands of exceptions spaming your chat when a mob spawns
- Werewolves now don't go on four legs when walking in non solid blocks
- You now get hunger when level 0 in werewolf form
- Werewolf goblins and villagers now save their texture, profession and career

## Removed Things

- You now don't get jump boost and speed under level 3

## Code Changes

- Moved `ArmorEffectiveAgainstTransformation` and `EffectiveAgainstTransformation` to `util/effectiveagainsttransformation`
- Added Access Transformer to be able to access the fields `EntityVillager#careerId` and `RenderManager#skinMap` and the method `EntityVillager#populateBuyingList`
- Made that `EntityGoblinTD#getName` can be accessed from the server without logging an error
- Added `IUntransformedCreatureExtraData` to save and load extra data for entities that can transform (like villagers and goblins)