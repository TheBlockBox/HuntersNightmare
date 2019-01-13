package theblockbox.huntersdream.world.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.IWorldGenerator;
import theblockbox.huntersdream.init.StructureInit;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

import java.util.Random;

public class WorldGenHuntersCamp implements IWorldGenerator {
    public static final int CHANCE = 2;

    // TODO: Make better method for choosing height
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World worldIn, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos((chunkX + 1) * 16, 255, (chunkZ + 1) * 16);
        if (GeneralHelper.isForest(worldIn.getBiome(pos)) && ConfigHandler.server.generateHuntersCamp
                && ChanceHelper.chanceOf(worldIn, WorldGenHuntersCamp.CHANCE)) {
            Template template = worldIn.getSaveHandler().getStructureTemplateManager()
                    .getTemplate(worldIn.getMinecraftServer(), StructureInit.HUNTERS_CAMP);
            int y = GeneralHelper.getStructureGenHeight(pos, worldIn, template.getSize());
            if (y == -1)
                return;
            pos.setY(y);
            template.addBlocksToWorldChunk(worldIn, pos, new PlacementSettings().setRotation(ChanceHelper.randomRotation(random)));
        }
    }
}