package theblockbox.huntersdream.api.init;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.entity.EntityBullet;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.entity.EntityHunter;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.entity.renderer.RenderBullet;
import theblockbox.huntersdream.entity.renderer.RenderGoblinTD;
import theblockbox.huntersdream.entity.renderer.RenderHunter;
import theblockbox.huntersdream.entity.renderer.RenderWerewolf;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EntityInit {
    // using the same values used in
    // net.minecraft.entity.EntityTracker#track(Entity)
    public static final boolean VEL_UPDATES = true;
    public static final int TRACKING_RANGE = 80;
    public static final int UPDATE_FREQ = 3;
    private static int networkID = 0;

    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().registerAll(
                EntityInit.getEntityEntryBuilder("goblintd", EntityGoblinTD.class).egg(29696, 255)
                        .tracker(EntityInit.TRACKING_RANGE, EntityInit.UPDATE_FREQ, EntityInit.VEL_UPDATES).build(),
                EntityInit.getEntityEntryBuilder("werewolf", EntityWerewolf.class)
                        .tracker(EntityInit.TRACKING_RANGE, EntityInit.UPDATE_FREQ, EntityInit.VEL_UPDATES)
                        .spawn(EnumCreatureType.CREATURE, 2, 5, 5,
                                StreamSupport.stream(Biome.REGISTRY.spliterator(), false)
                                        .filter(b -> BiomeDictionary.hasType(b, BiomeDictionary.Type.FOREST))
                                        .collect(Collectors.toSet())).build(),
                EntityInit.getEntityEntryBuilder("hunter", EntityHunter.class).egg(12820338, 4532224)
                        .tracker(EntityInit.TRACKING_RANGE, EntityInit.UPDATE_FREQ, EntityInit.VEL_UPDATES).build(),
                EntityInit.getEntityEntryBuilder("bullet", EntityBullet.class).tracker(64, 20,
                        false).build());
    }

    public static void registerEntityRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGoblinTD.class, new IRenderFactory<EntityGoblinTD>() {
            @Override
            public Render<? super EntityGoblinTD> createRenderFor(RenderManager manager) {
                return new RenderGoblinTD(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityWerewolf.class, new IRenderFactory<EntityWerewolf>() {
            @Override
            public Render<? super EntityWerewolf> createRenderFor(RenderManager manager) {
                return new RenderWerewolf(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityHunter.class, new IRenderFactory<EntityHunter>() {
            @Override
            public Render<? super EntityHunter> createRenderFor(RenderManager manager) {
                return new RenderHunter(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, new IRenderFactory<EntityBullet>() {
            @Override
            public Render<? super EntityBullet> createRenderFor(RenderManager manager) {
                return new RenderBullet(manager);
            }
        });
    }

    private static EntityEntryBuilder<Entity> getEntityEntryBuilder(String name, Class<? extends Entity> clazz) {
        return EntityEntryBuilder.create().entity(clazz).id(GeneralHelper.newResLoc(name), EntityInit.networkID++).name(name);
    }
}
