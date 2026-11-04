package io.phanisment.urars.registry;

/**
 * Implement on a mechanic, targeter, etc. to decide whether it should be registered.
 * <p>
 * Called once during scanning, on an instance created WITHOUT running the constructor,
 * so never read instance fields here. Only use static/global state (config, loaded mods).
 */
public interface IRegistryCondition {
	boolean validate();
}
