package io.phanisment.urars.util;

import net.minecraft.client.gui.font.providers.FreeTypeUtil;

public interface ActionUtils {
	static String[] aliases(String... alias) {
		return alias;
	}

	static String[] aliasNull() {
		return new String[]{null};
	}

	static boolean optionalBoolean(Boolean input) {
		return input != null ? input : false;
	}
}
