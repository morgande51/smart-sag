package org.nge.smartsag.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "sag_request")
@Data
@EqualsAndHashCode(exclude = {"cyclist","ride"})
@ToString(exclude = {"cyclist","ride"})
@NamedQueries(@NamedQuery(name = "SAGRequest.findReqForUser", query = "select req from SAGRequest req join req.cyclist c where c.id = ?1"))
public class SAGRequest implements IdentifiableDomain<Long> {

	@Id
	@SequenceGenerator(name = "sagReqSeq", sequenceName = "sag_req_seq", allocationSize = 1, initialValue = 1000)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sagReqSeq")
	private Long id;
	
	@Column(name = "ref_id", nullable = false)
	private String referenceId;
	
	@Column(name = "requested", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime requestedAt;
	
	@Column(name = "completed", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime completedAt;
	
	@Column(name = "status", nullable = false)
	private SAGRequestStatusType status;
	
	@Column(name = "type", nullable = false)
	private SAGRequestType type;
	
	@Embedded
	private Coordinates lastKnowLocation;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User cyclist;
	
	@ManyToOne
	@JoinColumn(name = "ride_id", nullable = false)
	private Ride ride;
	
	public void close(SAGRequestStatusType sagStatus) {
		this.status = sagStatus;
		completedAt = ZonedDateTime.now(getRide().getStartAt().getZone());
	}
	
	public boolean isActive() {
		return status != SAGRequestStatusType.CANCELED && status != SAGRequestStatusType.COMPLETE;
	}
	
	public static SAGRequest from(
			User user, 
			Ride ride, 
			double lat, 
			double lng, 
			char code) 
	{
		SAGRequestType type = SAGRequestType.from(code);
		Coordinates latLong = Coordinates.from(lat, lng);
		SAGRequest request = new SAGRequest();
		request.setStatus(SAGRequestStatusType.NEW);
		request.setRequestedAt(ZonedDateTime.now(ride.getEventTimeZone()));
		request.setLastKnowLocation(latLong);
		request.setType(type);
		request.setCyclist(user);
		request.setRide(ride);
		request.setReferenceId(UUID.randomUUID().toString());
		return request;
	}
	
	public static final String FIND_REQ_FOR_USER = "#SAGRequest.findReqForUser";
}