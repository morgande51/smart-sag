package org.nge.smartsag.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class SAGRequest implements Serializable {
	
	private Long id;
	
	private User cyclist;
	
	private Ride ride;
	
	private ZonedDateTime requestedAt;
	
	private ZonedDateTime completedAt;
	
	private SAGRequestStatus status;
	
	private Coordinates lastKnowLocation;
	
	public void close(SAGRequestStatus sagStatus) {
		this.status = sagStatus;
		completedAt = ZonedDateTime.now(ride.getStartAt().getZone());
	}
	
	public static SAGRequest from(User user, Ride ride, Coordinates latLong) {
		SAGRequest request = new SAGRequest();
		request.setStatus(SAGRequestStatus.ACTIVE);
		request.setCyclist(user);
		request.setRide(ride);
		request.setRequestedAt(ZonedDateTime.now(ride.getEventTimeZone()));
		request.setLastKnowLocation(latLong);
		return request;
	}
	
	private static final long serialVersionUID = 1L;
}