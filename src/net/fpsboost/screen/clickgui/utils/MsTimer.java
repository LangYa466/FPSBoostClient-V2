package net.fpsboost.screen.clickgui.utils;

public final class MsTimer {
	private long time;
	private boolean active;

	public MsTimer() {
		time = System.currentTimeMillis();
		active = true;
	}

	public boolean reach(final long time) {
		if (!active)
			return false;
		return time() >= time;
	}

	public void reset() {
		time = System.currentTimeMillis();
	}

	public boolean sleep(final long time) {
		if (!active)
			return false;
		if (time() >= time) {
			reset();
			return true;
		}
		return false;
	}

	public long time() {
		return System.currentTimeMillis() - time;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}