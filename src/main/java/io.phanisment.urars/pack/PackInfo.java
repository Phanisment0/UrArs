package io.phanisment.urars.pack;

import java.util.List;

public record PackInfo(
	String version,
	String name,
	List<String> description,
	List<String> author
) {
}