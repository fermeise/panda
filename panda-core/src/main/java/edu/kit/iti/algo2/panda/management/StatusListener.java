package edu.kit.iti.algo2.panda.management;

public interface StatusListener {
	/**
	 * Called when the current status has been updated.
	 * @param status The new status.
	 */
	public void statusUpdate(String status);
}
