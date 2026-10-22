package io.phanisment.urars.registry;

import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.util.AnnotationScanner;
import io.phanisment.urars.util.UrArsDebugger;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static io.phanisment.urars.UrArs.MOD_ID;

public final class RegistryScanner {
	private RegistryScanner() {
	}

	public static <T, F, R extends AnnotatedRegistry<T, F>> R scan(R registry, String path, Class<T> base_class, Class<?> factory_type) {
		Method sam = findFactoryMethod(factory_type);
		MethodHandles.Lookup lookup = MethodHandles.lookup();

		long start = System.nanoTime();
		int failed = 0;
		UrArsDebugger.debug("Scanning package '{}' for {}", path, base_class.getSimpleName());

		for (Class<?> clazz : AnnotationScanner.find(MOD_ID, path, RegistryInfo.class)) {
			if (!base_class.isAssignableFrom(clazz)) {
				UrArsDebugger.trace("Skipped '{}': not a {}", clazz.getName(), base_class.getSimpleName());
				continue;
			}

			try {
				F factory = createFactory(lookup, clazz, factory_type, sam);
				if (registry.register(clazz.asSubclass(base_class), factory)) UrArsDebugger.debug("Registered class '{}'", clazz.getSimpleName());
				else failed++;
			} catch (Throwable e) {
				failed++;
				UrArsDebugger.error("Failed to register '" + clazz.getName() + "'", e);
			}
		}

		long ms = (System.nanoTime() - start) / 1_000_000;
		UrArsDebugger.info("Scanned '{}': {} registered, {} failed, {} aliases ({} ms)", path, registry.size(), failed, registry.aliasSize(), ms);
		return registry;
	}

	@SuppressWarnings("unchecked")
	private static <F> F createFactory(MethodHandles.Lookup lookup, Class<?> clazz, Class<?> factory_type, Method sam) throws Throwable {
		Class<?>[] params = sam.getParameterTypes();

		MethodHandle constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class, params));
		CallSite site = LambdaMetafactory.metafactory(
				lookup,
				sam.getName(),                                      // "create"
				MethodType.methodType(factory_type),                // factory interface produced
				MethodType.methodType(sam.getReturnType(), params), // erased interface signature
				constructor,
				MethodType.methodType(clazz, params)                // real signature
		);
		return (F) site.getTarget().invoke();
	}

	private static Method findFactoryMethod(Class<?> factory_type) {
		return Arrays.stream(factory_type.getMethods()).filter(m -> Modifier.isAbstract(m.getModifiers())).findFirst().orElseThrow(() -> new IllegalArgumentException(factory_type.getName() + " has no abstract method"));
	}
}