package theblockbox.huntersdream.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
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

	public static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(ITransformationPlayer.class, new TransformationPlayerStorage(),
				() -> new TransformationPlayer());
		CapabilityManager.INSTANCE.register(ITransformationCreature.class, new TransformationCreatureStorage(),
				() -> new TransformationCreature());
		CapabilityManager.INSTANCE.register(IInfectInTicks.class, new InfectInTicksStorage(),
				() -> new InfectInTicks());
		CapabilityManager.INSTANCE.register(IInfectOnNextMoon.class, new InfectOnNextMoonStorage(),
				() -> new InfectOnNextMoon());
		CapabilityManager.INSTANCE.register(IWerewolf.class, new WerewolfStorage(), () -> new Werewolf());
	}
}
