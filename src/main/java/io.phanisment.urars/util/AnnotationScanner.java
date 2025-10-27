package io.phanisment.urars.util;

import net.fabricmc.loader.api.FabricLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public final class AnnotationScanner {
	public static <T extends Annotation> List<Class<?>> find(String mod_id, String package_path, Class<T> annotation_class) {
		List<Class<?>> result = new ArrayList<>();
		
		try {
			String jar_path = FabricLoader.getInstance().getModContainer(mod_id).orElseThrow(() -> new IllegalStateException("Mod not found: " + mod_id)).getOrigin().getPaths().get(0).toString();
			jar_path = URLDecoder.decode(jar_path, StandardCharsets.UTF_8);
			try (InputStream fis = new FileInputStream(jar_path); JarInputStream jar_stream = new JarInputStream(fis)) {
				JarEntry entry;
				while ((entry = jar_stream.getNextJarEntry()) != null) {
					if (!entry.getName().endsWith(".class")) continue;
					
					String class_name = entry.getName().replace("/", ".").replace(".class", "");
					if (!class_name.startsWith(package_path)) continue;
					
					try {
						Class<?> clazz = Class.forName(class_name, false, AnnotationScanner.class.getClassLoader());
						if (clazz.isAnnotationPresent(annotation_class)) {
							result.add(clazz);
						}
					} catch (Throwable ignored) {
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}