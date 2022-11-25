package org.nge.smartsag.service;

import java.util.List;

import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.User;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Me {
	
	@Setter
	private User account;
	
	@Setter
	private ActiveSAGRequest activeSAGRequest;
	
	private Long[] activeSupportRideIds = new Long[]{};
	
	private Long[] activeMarshalRideIds = new Long[]{};
	
	private Long[] activeAdminRideIds = new Long[]{};
	
	public Me(User account) {
		this.account = account;
	}

	public void setSupportRides(List<Ride> rides) {
		activeSupportRideIds = rides.stream().map(Ride::getId).toArray(i -> new Long[i]);
	}
	
	public void setMarshalRides(List<Ride> rides) {
		activeMarshalRideIds = rides.stream().map(Ride::getId).toArray(i -> new Long[i]);
	}
	
	public void setAdminRides(List<Ride> rides) {
		activeAdminRideIds = rides.stream().map(Ride::getId).toArray(i -> new Long[i]);
	}
}