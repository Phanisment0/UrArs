package io.phanisment.urars.skill.config;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Optional;

public class ConditionLineConfig {
	private Collection<String> parts = new ArrayList<>();
	private Optional<String> action;
	
	public ConditionLineConfig(String line) {
		line = line.trim();
		
		if (isComposite(line)) {
			int depth = 0;
			int last_bracket = -1;
			int first_bracket = line.indexOf("(");
			
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if (c == '(') depth++;
				else if (c == ')') {
					depth--;
					if (depth == 0) {
						last_bracket = i;
						break;
					}
				}
				
			}
			if (depth != 0) System.out.println("Bracket unbalance!");
			
			if (first_bracket != -1 && last_bracket != -1 && last_bracket > first_bracket) {
				String content = line.substring(first_bracket + 1, last_bracket).trim();
				String after = (last_bracket + 1 < line.length()) ? line.substring(last_bracket + 1).trim() : null;
				this.action = Optional.ofNullable(after);
				this.parts.addAll(split(content));
				return;
			}
		}
		this.parts.add(line.trim());
		this.action = Optional.empty();
	}
	
	private Collection<String> split(String line) {
		Collection<String> result = new ArrayList<>();
		int depth = 0;
		int last_bracket = 0;
		
		for (int i = 0; i < line.length() - 1; i++) {
			char c = line.charAt(i);
			if (c == '(') depth++;
			else if (c == ')') depth--;
			else if (depth == 0) {
				if (line.startsWith("&&", i) || line.startsWith("||", i)) {
					result.add(line.substring(last_bracket, i).trim());
					result.add(line.substring(i, i + 2));
					last_bracket = i + 2;
					i++;
				}
			}
		}
		if (last_bracket < line.length()) result.add(line.substring(last_bracket).trim());
		
		return result;
	}
	
	public Collection<String> parts() {
		return this.parts;
	}
	
	public Optional<String> action() {
		return this.action;
	}
	
	public static boolean isComposite(String line) {
		return line.contains("(") && line.contains(")");
	}
	
	@Override
	public String toString() {
		return parts.toString() + " " + action;
	}
}