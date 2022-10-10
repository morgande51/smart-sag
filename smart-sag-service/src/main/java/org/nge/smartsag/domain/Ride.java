package org.nge.smartsag.domain;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	public SAGRequest closeSOS(String referenceId, SAGRequestStatus status) {
		SAGRequest sos = sosRequests.stream()
				.filter(r -> r.getReferenceId().equals(referenceId))
				.findAny()
				.orElseThrow(() -> new UnknownSAGRequest(referenceId));
		sos.close(status);
		return sos;
	}
	
	public Set<SAGRequest> getActiveSOS() {
		Set<SAGRequest> activeRequest = Collections.emptySet();
		if (sosRequests != null) {
			activeRequest = sosRequests.stream()
				.filter(r -> r.getStatus() == SAGRequestStatus.ACTIVE)
				.sorted(Comparator.comparing(SAGRequest::getRequestedAt))
				.collect(Collectors.toSet());
		}
		return activeRequest;
	}

	private static final long serialVersionUID = 1L;
}