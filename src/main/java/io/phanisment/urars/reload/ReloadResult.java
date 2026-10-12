package io.phanisment.urars.reload;

public record ReloadResult(boolean result, String message) {
	public static ReloadResult success() {
		return new ReloadResult(true, null);
	}

	public static ReloadResult success(String message) {
		return new ReloadResult(true, message);
	}

	public static ReloadResult failed() {
		return new ReloadResult(false, null);
	}

	public static ReloadResult failed(String message) {
		return new ReloadResult(false, message);
	}
}
