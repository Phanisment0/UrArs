package io.phanisment.urars.pack;

import java.io.File;

public class Pack {
	private final String name;
	private final File file;
	private PackInfo info;
	
	public Pack(File file) {
		this.name = file.getName();
		this.file = file;
	}
	
	public String name() {
		return this.name;
	}
	
	public File file() {
		return this.file;
	}
	
	public PackInfo info() {
		return this.info;
	}
	
	public void info(PackInfo info) {
		this.info = info;
	}
}