package org.nge.smartsag.domain;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class Ride implements Serializable {
	
	private Long id;
	
	private Organization hostedBy;
	
	private Set<User> host;
	
	private Set<User> sagSupport;
	
	private ZonedDateTime startAt;
	
	private ZonedDateTime endAt;
	
	private Address location;
	
	private Set<SAGRequest> sosRequests;
	
	public ZoneId getEventTimeZone() {
		return startAt.getZone();
	}
	
	public SAGRequest requestSOS(User cyclist, Coordinates latLong) {
		if (sosRequests == null) {
			sosRequests = new HashSet<>();
		}
		SAGRequest sos = sosRequests.stream()
				.filter(r -> r.getCyclist().getId().equals(cyclist.getId())).findAny()
				.orElseGet(() -> {
					SAGRequest req = SAGRequest.from(cyclist, this, latLong);
					sosRequests.add(req);
					return req;
				});
		
		return sos;	
	}
	
	public SAGRequest closeSAGRequest(Long sagRequestId, SAGRequestStatus status) {
		SAGRequest sos = sosRequests.stream()
				.filter(r -> r.getId().equals(sagRequestId)).findAny().orElseThrow(() -> new UnknownSAGRequest(sagRequestId));
		sos.close(status);
		return sos;
	}

	private static final long serialVersionUID = 1L;
}