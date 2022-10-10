package org.nge.smartsag.domain;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.nge.smartsag.domain.SAGRequestException.Reason;

import lombok.Data;

@Data
public class Ride implements Serializable {
	
	private Long id;
	
	private String name;
	
	private Organization hostedBy;
	
	private Set<User> hosts;
	
	private Set<User> sagSupporters;
	
	private ZonedDateTime startAt;
	
	private ZonedDateTime endAt;
	
	private Address location;
	
	private Set<SAGRequest> sosRequests;
	
	public ZoneId getEventTimeZone() {
		return startAt.getZone();
	}
	
	public boolean hasActiveSAGRequest() {
		boolean activeRequest = false;
		if (sosRequests != null && !sosRequests.isEmpty()) {
			activeRequest = sosRequests.stream().anyMatch(r -> r.getStatus() == SAGRequestStatus.ACTIVE);
		}
		return activeRequest;
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
	
	public SAGRequest cancelSOS(String referenceId, User cyclist, Optional<User> admin) {
		SAGRequest request = findSOS(referenceId);
		if (!User.isUserIn(hosts.stream(), admin) && !request.getCyclist().getId().equals(cyclist.getId())) {
			throw new SAGRequestException(referenceId, Reason.UNAUTHORIZED);
		}
		request.close(SAGRequestStatus.CANCELED);
		return request;
	}
	
	public SAGRequest completeSOS(String referenceId, User sag, Optional<User> admin) {
		SAGRequest request = findSOS(referenceId);
		if (!User.isUserIn(hosts.stream(), admin) && !User.isUserIn(sagSupporters.stream(), Optional.of(sag))) {
			throw new SAGRequestException(referenceId, Reason.UNAUTHORIZED);
		}
		request.close(SAGRequestStatus.COMPLETE);
		return request;
	}
	
	protected SAGRequest findSOS(String referenceId) {
		return sosRequests.stream()
				.filter(r -> r.getReferenceId().equals(referenceId))
				.findAny()
				.orElseThrow(() -> new SAGRequestException(referenceId, Reason.UNKNOWN_REF_ID));
	}
	
	public static Ride createRide(User admin, String name, ZonedDateTime startAt, ZonedDateTime endAt, Address location) {
		Ride ride = new Ride();
		ride.setName(name);
		ride.setStartAt(startAt);
		ride.setEndAt(endAt);
		ride.setHosts(Collections.singleton(admin));
		ride.setLocation(location);
		return ride;
	}

	private static final long serialVersionUID = 1L;
}