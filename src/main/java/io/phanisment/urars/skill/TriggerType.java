package io.phanisment.urars.skill;

public enum TriggerType {
	ON_SPAWN("onSpawn"),
	ON_PRE_SPAWN("onPreSpawn"),
	ON_LOAD("onLoad"),

	ON_USE("onUse"),
	ON_START_USE_ITEM("onStartUseItem"),
	ON_STOP_USING_ITEM("onStopUseItem"),
	ON_SWING("onSwing"),
	ON_SNEAK("onSneak"),
	ON_DEATH("onDeath");
	
	private final String alias;
	TriggerType(String alias) {
		this.alias = alias;
	}
	
	public String alias() {
		return this.alias;
	}
}