package org.nge.smartsag.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.nge.smartsag.domain.SAGRequestException.Reason;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(exclude = {"marshals","hostedBy","sagSupporters","sagRequests"})
@ToString(exclude = {"marshals","hostedBy","sagSupporters","sagRequests"})
@NamedQueries({
	@NamedQuery(name = "Ride.getWithHostAndSAG", query = "select ride from Ride ride left join fetch ride.marshals left join fetch ride.sagSupporters left join fetch ride.sagRequests where ride.id = :id"),
	@NamedQuery(name = "Ride.getFromRefId", query = "select ride from Ride ride where referenceId = :referenceId"),
	@NamedQuery(name = "Ride.findForSAGSupport", query = "select ride from Ride ride join ride.sagSupporters s where s.id = :userId"),
	@NamedQuery(name = "Ride.findForMarshals", query = "select ride from Ride ride join ride.marshals m where m.id = :userId"),
	@NamedQuery(name = "Ride.findForAdmins", query = "select ride from Ride ride join ride.hostedBy org join org.admins admin where admin.id = :userId")
})
public class Ride implements IdentifiableDomain<Long> , UserVerificationSupport {
	
	@Id
	@SequenceGenerator(name = "rideSeq", sequenceName = "ride_seq", allocationSize = 1, initialValue = 1000)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rideSeq")
	private Long id;
	
	@Column(name = "ref_id", nullable = false, unique = true)
	private String referenceId;
	
	@Column(nullable = false)
	private String name;
	
	@JsonbDateFormat(value = JsonbDateFormat.TIME_IN_MILLIS)
	@Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime startAt;
	
	@JsonbDateFormat(value = JsonbDateFormat.TIME_IN_MILLIS)
	@Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime endAt;
	
	@Embedded
	private Address location;
	
	@ManyToOne
	@JoinColumn(name = "hosting_org", nullable = false)
	private Organization hostedBy;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "ride_marshal",
	           joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
			   inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
	@JsonbTransient
	private Set<User> marshals;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "ride_support",
	           joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
			   inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
	@JsonbTransient
	private Set<User> sagSupporters;
	
	@OneToMany(mappedBy = "ride", orphanRemoval = true, cascade = CascadeType.ALL)
	@JsonbTransient
	private Set<SAGRequest> sagRequests;
	
	public void endNow() {
		if (isActive()) {
			endAt = Instant.now().atZone(getEventTimeZone());
		}
	}
	
	public boolean isActive() {
		Instant now = Instant.now();
		return now.isAfter(startAt.toInstant()) && now.isBefore(endAt.toInstant());
	}
	
	public boolean hasSAGSupporters() {
		return sagSupporters != null && !sagSupporters.isEmpty();
	}
	
	public ZoneId getEventTimeZone() {
		return startAt.getZone();
	}
	
	public boolean hasActiveSAGRequest() {
		boolean activeRequest = false;
		if (sagRequests != null && !sagRequests.isEmpty()) {
			activeRequest = sagRequests.stream().anyMatch(SAGRequest::isActive);
		}
		return activeRequest;
	}
	
	public boolean hasActiveSAGRequest(User user) {
		boolean hasSagRequest = false;
		if (sagRequests != null && !sagRequests.isEmpty()) {
			hasSagRequest = sagRequests.stream()
					.filter(SAGRequest::isActive)
					.map(SAGRequest::getCyclist)
					.anyMatch(c -> c.equals(user));
		}
		return hasSagRequest;
	}
	
	public Optional<SAGRequest> getActiveSAGRequest(User user) {
		SAGRequest request = null;
		if (sagRequests != null && !sagRequests.isEmpty()) {
			request = sagRequests.stream()
					.filter(SAGRequest::isActive)
					.filter(r -> r.getCyclist().equals(user))
					.findAny()
					.orElse(null);
		}
		return Optional.ofNullable(request);
	}
	
	public Set<SAGRequest> getActiveSAGRequest() {
		Set<SAGRequest> activeRequest = Collections.emptySet();
		if (sagRequests != null) {
			activeRequest = sagRequests.stream()
				.filter(SAGRequest::isActive)
				.sorted(Comparator.comparing(SAGRequest::getRequestedAt))
				.collect(Collectors.toSet());
		}
		return activeRequest;
	}
	
	public SAGRequest requestSAG(
			User cyclist, 
			double lat, 
			double lng, 
			char code) 
	{
		if (!isActive()) {
			throw new SAGRequestException(Reason.RIDE_INACTIVE);
		}
		
		if (sagRequests == null) {
			sagRequests = new HashSet<>();
		}
		else if (hasActiveSAGRequest(cyclist)) {
			// TODO real exception
			throw new SAGRequestException(Reason.DUPLICATE_REQ);
		}
		
		SAGRequest req = SAGRequest.from(cyclist, this, lat, lng, code);
		sagRequests.add(req);
		
		return req;	
	}
	
	public void addMarshal(User admin, User user) {
		verifyUserIn(hostedBy.getAdmins(), admin);
		marshals.add(user);
	}
	
	public void removeMarshal(User admin, User user) {
		verifyUserIn(hostedBy.getAdmins(), admin);
		if (marshals.stream().count() == 1) {
			// TODO: must be one host present
			throw new RuntimeException();
		}
		marshals.remove(user);
	}
	
	public void addSAG(User admin, User sag) {
		verifyUserIn(hostedBy.getAdmins(), admin);
		if (sagSupporters == null) {
			sagSupporters = new HashSet<>();
		}
		sagSupporters.add(sag);
	}
	
	public void removeSAG(User admin, User sag) {
		verifyUserIn(hostedBy.getAdmins(), admin);
		sagSupporters.remove(sag);
	}
	
	public void verifyAccess(User user) {
		if (!hasActiveSAGRequest(user) && 
			!isUserIn(marshals, user) && 
			!isUserIn(sagSupporters, user) &&
			!isUserIn(hostedBy.getAdmins(), user)) 
		{
			throw new InvalidAdminException(user);
		}
	}
	
	public void verifyRoleForRide(User user, Role role) {
		switch (role) {
			case HOST:
				verifyUserIn(hostedBy.getAdmins(), user);
				break;
				
			case MARSHAL:
				verifyUserIn(marshals, user);
				break;
				
			case SUPPORT:
				verifyUserIn(sagSupporters, user);
				break;
				
			default:
				throw new InvalidAdminException(user); 
		}
	}
	
	protected SAGRequest findSAG(String referenceId) {
		return sagRequests.stream()
				.filter(r -> r.getReferenceId().equals(referenceId))
				.findAny()
				.orElseThrow(() -> new SAGRequestException(referenceId, Reason.UNKNOWN_REF_ID));
	}
	
	public static Ride createRide(User admin, Organization hostOrg, String name, ZonedDateTime startAt, ZonedDateTime endAt, Address location) {
		Ride ride = new Ride();
		ride.setName(name);
		ride.setStartAt(startAt);
		ride.setEndAt(endAt);
		ride.setLocation(location);
		ride.setMarshals(Collections.singleton(admin));
		ride.setHostedBy(hostOrg);
		ride.setReferenceId(UUID.randomUUID().toString());
		return ride;
	}
	
	public static final String GET_WITH_HOST_AND_SAG = "#Ride.getWithHostAndSAG";
	
	public static final String GET_FROM_REF_ID = "#Ride.getFromRefId";

	public static final String FIND_FOR_SAG_SUPPORT = "#Ride.findForSAGSupport";
	
	public static final String FIND_FOR_MARSHALS = "#Ride.findForMarshals";
	
	public static final String FIND_FOR_ADMINS = "#Ride.findForAdmins";
}