package io.phanisment.urars.config.pack;

import java.io.File;

@FunctionalInterface
public interface PackLoader {
	void load(File file);
}
