package io.phanisment.urars;

import net.fabricmc.api.ModInitializer;

import io.phanisment.urars.pack.PackManager;
import io.phanisment.urars.skill.SkillManager; 

public final class UrArs implements ModInitializer {
	public static String ID = "urars";
	
	@Override
	public void onInitialize() {
		SkillManager.loadClasses();
		
		PackManager.load();
		SkillManager.load();
	}
	
	public static void reload() {
		PackManager.load();
		SkillManager.load();
	}
}