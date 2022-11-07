package org.nge.smartsag.service;

import java.io.Serializable;

import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.SAGRequest;
import org.nge.smartsag.domain.User;

import lombok.Getter;

@Getter
public class Me implements Serializable {

	private User account;
	
	private Long activeSAGRequestId;
	
	private Long activeSAGRideId;
	
	public Me(User me) {
		this.account = me;
	}
	
	public Me(User me, Ride ride) {
		this(me);
		activeSAGRideId = ride.getId();
	}
	
	public Me(User me, SAGRequest req) {
		this(me);
		activeSAGRequestId = req.getId();
	}
	
	private static final long serialVersionUID = 1L;
}
