package org.nge.smartsag.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class SAGRequest implements Serializable {
	
	private SAGRequestId id;
	
	private String referenceId;
	
	private ZonedDateTime requestedAt;
	
	private ZonedDateTime completedAt;
	
	private SAGRequestStatus status;
	
	private Coordinates lastKnowLocation;
	
	public User getCyclist() {
		return id.getCyclist();
	}
	
	public Ride getRide() {
		return id.getRide();
	}
	
	public void close(SAGRequestStatus sagStatus) {
		this.status = sagStatus;
		completedAt = ZonedDateTime.now(getRide().getStartAt().getZone());
	}
	
	public boolean isActive() {
		return status != SAGRequestStatus.CANCELED && status != SAGRequestStatus.COMPLETE;
	}
	
	public static SAGRequest from(User user, Ride ride, Coordinates latLong) {
		SAGRequestId id = new SAGRequestId();
		id.setCyclist(user);
		id.setRide(ride);
		
		SAGRequest request = new SAGRequest();
		request.setStatus(SAGRequestStatus.NEW);
		request.setId(id);
		request.setRequestedAt(ZonedDateTime.now(ride.getEventTimeZone()));
		request.setLastKnowLocation(latLong);
		request.setReferenceId(UUID.randomUUID().toString());
		return request;
	}
	
	private static final long serialVersionUID = 1L;
}