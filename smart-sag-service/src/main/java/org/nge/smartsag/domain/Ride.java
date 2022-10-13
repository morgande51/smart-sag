package org.nge.smartsag.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.nge.smartsag.domain.SAGRequestException.Reason;

import lombok.Data;

@Entity
@Data
public class Ride {
	
	@Id
	@SequenceGenerator(name = "rideSeq", sequenceName = "ride_seq", allocationSize = 1, initialValue = 1000)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime startAt;
	
	@Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime endAt;
	
	@Embedded
	private Address location;
	
	@ManyToOne
	@JoinColumn(name = "hosting_org", nullable = false)
	private Organization hostedBy;
	
	@OneToMany
	@JoinTable(name = "ride_host",
	           joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
			   inverseJoinColumns = @JoinColumn(name = "sag_user_id", referencedColumnName = "id"))
	private Set<User> hosts;
	
	@OneToMany
	@JoinTable(name = "ride_sag",
	           joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
			   inverseJoinColumns = @JoinColumn(name = "sag_user_id", referencedColumnName = "id"))
	private Set<User> sagSupporters;
	
	@OneToMany(mappedBy = "ride", orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<SAGRequest> sosRequests;
	
	public void endNow() {
		if (isActive()) {
			endAt = Instant.now().atZone(getEventTimeZone());
		}
	}
	
	public boolean isActive() {
		Instant now = Instant.now();
		return now.isAfter(startAt.toInstant()) && now.isBefore(endAt.toInstant());
	}
	
	public ZoneId getEventTimeZone() {
		return startAt.getZone();
	}
	
	public boolean hasActiveSAGRequest() {
		boolean activeRequest = false;
		if (sosRequests != null && !sosRequests.isEmpty()) {
			activeRequest = sosRequests.stream().anyMatch(SAGRequest::isActive);
		}
		return activeRequest;
	}
	
	public Set<SAGRequest> getActiveSAGRequest() {
		Set<SAGRequest> activeRequest = Collections.emptySet();
		if (sosRequests != null) {
			activeRequest = sosRequests.stream()
				.filter(SAGRequest::isActive)
				.sorted(Comparator.comparing(SAGRequest::getRequestedAt))
				.collect(Collectors.toSet());
		}
		return activeRequest;
	}
	
	public SAGRequest requestSAG(User cyclist, Coordinates latLong) {
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
	
	public SAGRequest cancelSAGRequest(String referenceId, User cyclist, Optional<User> admin) {
		SAGRequest request = findSOS(referenceId);
		if (!User.isUserIn(hosts.stream(), admin) && !request.getCyclist().getId().equals(cyclist.getId())) {
			throw new SAGRequestException(referenceId, Reason.UNAUTHORIZED);
		}
		request.close(SAGRequestStatus.CANCELED);
		return request;
	}
	
	public SAGRequest completeSAGRequest(String referenceId, User sag, Optional<User> admin) {
		SAGRequest request = findSOS(referenceId);
		if (!User.isUserIn(hosts.stream(), admin) && !User.isUserIn(sagSupporters.stream(), Optional.of(sag))) {
			throw new SAGRequestException(referenceId, Reason.UNAUTHORIZED);
		}
		request.close(SAGRequestStatus.COMPLETE);
		return request;
	}
	
	public void addHost(User admin, User user) {
		verifyHost(admin);
		hosts.add(user);
	}
	
	public void removeHost(User admin, User user) {
		verifyHost(admin);
		if (hosts.stream().count() == 1) {
			// TODO: must be one host present
		}
		hosts.remove(user);
	}
	
	public void addSAG(User admin, User sag) {
		verifyHost(admin);
		if (sagSupporters == null) {
			sagSupporters = new HashSet<>();
		}
		sagSupporters.add(sag);
	}
	
	public void removeSAG(User admin, User sag) {
		verifyHost(admin);
		sagSupporters.remove(sag);
	}
	
	protected SAGRequest findSOS(String referenceId) {
		return sosRequests.stream()
				.filter(r -> r.getReferenceId().equals(referenceId))
				.findAny()
				.orElseThrow(() -> new SAGRequestException(referenceId, Reason.UNKNOWN_REF_ID));
	}
	
	protected void verifyHost(User admin) {
		if (!User.isUserIn(hosts.stream(), Optional.of(admin))) {
			throw new InvalidAdminException(admin);
		}
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
}