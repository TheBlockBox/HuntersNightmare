package theblockbox.huntersdream.util.handlers;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import theblockbox.huntersdream.util.Reference;

public class KeyBindingInit {
    public static final KeyBinding ACTIVATE_SKILL_BAR = new KeyBinding(Reference.MODID + ".key.activateSkillBar",
            KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.MODID + ".keyCategory");

    public static void register() {
        ClientRegistry.registerKeyBinding(KeyBindingInit.ACTIVATE_SKILL_BAR);
    }
}
