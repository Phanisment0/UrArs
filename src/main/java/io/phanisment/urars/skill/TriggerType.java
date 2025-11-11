package io.phanisment.urars.skill;

public enum TriggerType {
	ON_SPAWN("onSpawn"),
	ON_PRE_SPAWN("onPreSpawn");
	
	private final String alias;
	
	private TriggerType(String alias) {
		this.alias = alias;
	}
	
	public String alias() {
		return this.alias;
	}
}