package org.nge.smartsag.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "sag_request")
@Data
public class SAGRequest {
	
	@Id
	@SequenceGenerator(name = "sagReqSeq", sequenceName = "sag_req_seq", allocationSize = 1, initialValue = 1000)
	private Long id;
	
	@Column(name = "ref_id", nullable = false)
	private String referenceId;
	
	@Column(name = "requested", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime requestedAt;
	
	@Column(name = "completed", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime completedAt;
	
	private SAGRequestStatus status;
	
	@Embedded
	private Coordinates lastKnowLocation;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User cyclist;
	
	@ManyToOne
	@JoinColumn(name = "ride_id", nullable = false)
	private Ride ride;
	
	public void close(SAGRequestStatus sagStatus) {
		this.status = sagStatus;
		completedAt = ZonedDateTime.now(getRide().getStartAt().getZone());
	}
	
	public boolean isActive() {
		return status != SAGRequestStatus.CANCELED && status != SAGRequestStatus.COMPLETE;
	}
	
	public static SAGRequest from(User user, Ride ride, Coordinates latLong) {		
		SAGRequest request = new SAGRequest();
		request.setStatus(SAGRequestStatus.NEW);
		request.setRequestedAt(ZonedDateTime.now(ride.getEventTimeZone()));
		request.setLastKnowLocation(latLong);
		request.setReferenceId(UUID.randomUUID().toString());
		return request;
	}
}