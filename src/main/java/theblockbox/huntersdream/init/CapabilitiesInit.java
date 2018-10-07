package theblockbox.huntersdream.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandler;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks.InfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks.InfectInTicksStorage;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectOnNextMoonStorage;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature.TransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature.TransformationCreatureStorage;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer.TransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer.TransformationPlayerStorage;
import theblockbox.huntersdream.util.interfaces.transformation.IVampire;
import theblockbox.huntersdream.util.interfaces.transformation.IVampire.Vampire;
import theblockbox.huntersdream.util.interfaces.transformation.IVampire.VampireStorage;
import theblockbox.huntersdream.util.interfaces.transformation.IWerewolf;
import theblockbox.huntersdream.util.interfaces.transformation.IWerewolf.Werewolf;
import theblockbox.huntersdream.util.interfaces.transformation.IWerewolf.WerewolfStorage;

public class CapabilitiesInit {
	@CapabilityInject(ITransformationPlayer.class)
	public static final Capability<ITransformationPlayer> CAPABILITY_TRANSFORMATION_PLAYER = null;
	@CapabilityInject(ITransformationCreature.class)
	public static final Capability<ITransformationCreature> CAPABILITY_TRANSFORMATION_CREATURE = null;
	@CapabilityInject(IInfectInTicks.class)
	public static final Capability<IInfectInTicks> CAPABILITY_INFECT_IN_TICKS = null;
	@CapabilityInject(IInfectOnNextMoon.class)
	public static final Capability<IInfectOnNextMoon> CAPABILITY_INFECT_ON_NEXT_MOON = null;
	@CapabilityInject(IWerewolf.class)
	public static final Capability<IWerewolf> CAPABILITY_WEREWOLF = null;
	@CapabilityInject(IVampire.class)
	public static final Capability<IVampire> CAPABILITY_VAMPIRE = null;
	@CapabilityInject(IItemHandler.class)
	public static final Capability<IItemHandler> CAPABILITY_ITEM_HANDLER = null;

	public static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(ITransformationPlayer.class, new TransformationPlayerStorage(),
				TransformationPlayer::new);
		CapabilityManager.INSTANCE.register(ITransformationCreature.class, new TransformationCreatureStorage(),
				TransformationCreature::new);
		CapabilityManager.INSTANCE.register(IInfectInTicks.class, new InfectInTicksStorage(), InfectInTicks::new);
		CapabilityManager.INSTANCE.register(IInfectOnNextMoon.class, new InfectOnNextMoonStorage(),
				InfectOnNextMoon::new);
		CapabilityManager.INSTANCE.register(IWerewolf.class, new WerewolfStorage(), Werewolf::new);
		CapabilityManager.INSTANCE.register(IVampire.class, new VampireStorage(), Vampire::new);
	}
}
