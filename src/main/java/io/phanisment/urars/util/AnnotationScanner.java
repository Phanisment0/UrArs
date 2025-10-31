package io.phanisment.urars.util;

import net.fabricmc.loader.api.FabricLoader;

import static io.phanisment.urars.UrArs.LOGGER;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Utility for find annotation class in specific package inside mod jar.
 */
public final class AnnotationScanner {
	private AnnotationScanner() {
	}

	/**
	 * Search all class with annotation in the targeted package.
	 *
	 * @param mod        Id mod that registered in {@link FabricLoader}
	 * @param path       Package target to scan, ex: "io.phanisment.urars.util"
	 * @param annotation Target annotation class
	 * @param <T>        Annotation type
	 * @return           List founded class
	 */
	public static <T extends Annotation> List<Class<?>> find(String mod, String path, Class<T> annotation) {
		List<Class<?>> results = new ArrayList<>();
		try {
			Path jar_path = FabricLoader.getInstance().getModContainer(mod).orElseThrow(() -> new IllegalStateException("Mod not found: " + mod)).getOrigin().getPaths().get(0);
			String decoded_path = URLDecoder.decode(jar_path.toString(), StandardCharsets.UTF_8);
			
			try (InputStream input = Files.newInputStream(Path.of(decoded_path)); JarInputStream jar_stream = new JarInputStream(input)) {
				JarEntry entry;
				while ((entry = jar_stream.getNextJarEntry()) != null) {
					if (isTargetClass(entry, path)) loadIfAnnotated(entry, annotation, results);
				}
			}
		} catch (IOException e) {
			LOGGER.error("[AnnotationScanner] Failed to read the jar", e);
		}
		
		return results;
	}
	
	private static boolean isTargetClass(JarEntry entry, String path) {
		return entry.getName().endsWith(".class") && entry.getName().replace('/', '.').startsWith(path);
	}
	
	private static <T extends Annotation> void loadIfAnnotated(JarEntry entry, Class<T> annotation, List<Class<?>> results) {
		String class_name = entry.getName().replace('/', '.').replace(".class", "");
		try {
			Class<?> clazz = Class.forName(class_name, false, AnnotationScanner.class.getClassLoader());
			if (clazz.isAnnotationPresent(annotation)) {
				results.add(clazz);
			}
		} catch (LinkageError | ClassNotFoundException ignored) {
			// Ignored
		}
	}
}
