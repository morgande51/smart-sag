package org.nge.smartsag.service;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class CreateRideRequest implements Serializable {

	private String name;
	
	private ZonedDateTime startAt;
	
	private ZonedDateTime endAt;
	
	private Long hostOrg;
	
	private String rideLocation;

	public boolean isPopUpRide() {
		return hostOrg == null;
	}
	
	private static final long serialVersionUID = 1L;
}