package io.phanisment.urars.skill;

public enum TriggerType {
	ON_SPAWN("onSpawn"),
	ON_PRE_SPAWN("onPreSpawn"),
	ON_LOAD("onLoad");
	
	private final String alias;
	TriggerType(String alias) {
		this.alias = alias;
	}
	
	public String alias() {
		return this.alias;
	}
}