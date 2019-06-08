package me.PietElite.basicReports.utils.data;

import java.util.Collection;
import java.util.Collections;

public abstract class BasicReportsDatabaseManager {
	private boolean error;
	private int lastReportId;

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
	protected void updateLastReportId(Collection<Integer> idCollection) {
		if (idCollection.isEmpty()) {
			setLastReportId(0);
		} else {
			setLastReportId(Collections.max(idCollection));
		}
	}

	public int getLastReportId() {
		return lastReportId;
	}

	public void setLastReportId(int lastReportId) {
		this.lastReportId = lastReportId;
	}
}
