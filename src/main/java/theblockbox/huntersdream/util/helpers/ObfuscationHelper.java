package theblockbox.huntersdream.util.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ObfuscationHelper {
	public static boolean obfuscated = true;
	public static Method setSizeMethod = null;
	public static final String SET_SIZE_OBFUSCATED = "func_70105_a";
	public static final String SET_SIZE_DEOBFUSCATED = "setSize";

	public static void init() {
		for (Field field : World.class.getFields()) {
			if (field.getName().equals("MAX_ENTITY_RADIUS"))
				obfuscated = false;
		}
	}

	public static void forceSetSize(Entity entity, float width, float height) {
		if (setSizeMethod == null) {
			try {
				Method m = Entity.class
						.getDeclaredMethod(ObfuscationHelper.obfuscated ? ObfuscationHelper.SET_SIZE_OBFUSCATED
								: ObfuscationHelper.SET_SIZE_DEOBFUSCATED, float.class, float.class);
				setSizeMethod = m;
			} catch (NoSuchMethodException e) {
				entity.width = width;
				entity.height = height;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (setSizeMethod != null) {
			try {
				setSizeMethod.setAccessible(true);
				setSizeMethod.invoke(entity, width, height);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
