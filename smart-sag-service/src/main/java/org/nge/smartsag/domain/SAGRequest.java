package org.nge.smartsag.domain;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.nge.smartsag.domain.SAGRequestException.Reason;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "sag_request")
@Data
@EqualsAndHashCode(exclude = {"cyclist","ride"})
@ToString(exclude = {"cyclist","ride"})
@NamedQueries({
	@NamedQuery(name = "SAGRequest.findReqForUser", query = "select req from SAGRequest req left join req.cyclist c left join req.acknowledgedBy s where c.id = :userId or s.id = :userId"),
	@NamedQuery(name = "SAGRequest.getFromRefId", query="select req from SAGRequest req where referenceId = :refId")
})
public class SAGRequest implements IdentifiableDomain<Long> {

	@Id
	@SequenceGenerator(name = "sagReqSeq", sequenceName = "sag_req_seq", allocationSize = 1, initialValue = 1000)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sagReqSeq")
	private Long id;
	
	@Column(name = "ref_id", nullable = false)
	private String referenceId;
	
	@JsonbDateFormat(value = JsonbDateFormat.TIME_IN_MILLIS)
	@Column(name = "requested", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime requestedAt;
	
	@JsonbDateFormat(value = JsonbDateFormat.TIME_IN_MILLIS)
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
	@JoinColumn(name = "support_id")
	private User acknowledgedBy;
	
	@ManyToOne
	@JoinColumn(name = "ride_id", nullable = false)
	private Ride ride;
	
	@OneToMany(mappedBy = "request", orphanRemoval = true, cascade = CascadeType.ALL)
	@JsonbTransient
	private Set<SAGRequestNote> notes;
	
	public boolean isActive() {
		return (status == SAGRequestStatusType.NEW) || (status == SAGRequestStatusType.ACKNOWLEDGED);
	}
	
	public void updateStatus(User user, SAGRequestStatusType status) {
		if (!isActive()) {
			throw new SAGRequestException(Reason.NOT_ACTIVE);
		}
		
		switch (status) {
			case ACKNOWLEDGED:
				// only the SAG Support or Org Admins can acknowledge a request
				if (!ride.isUserIn(ride.getSagSupporters(), user) &&
					!ride.isUserIn(ride.getHostedBy().getAdmins(), user))
				{
					throw new SAGRequestException(Reason.UNAUTHORIZED);
				}
				setAcknowledgedBy(user);
				break;
				
			case COMPLETE:
				// The SAG Support, Marshals, Org Admins or User can complete a request
				if (!cyclist.equals(user) &&
					!ride.isUserIn(ride.getSagSupporters(), user) &&
					!ride.isUserIn(ride.getMarshals(), user) &&
					!ride.isUserIn(ride.getHostedBy().getAdmins(), user)) 
				{
					throw new SAGRequestException(Reason.UNAUTHORIZED);
				}
				setCompletedAt(ZonedDateTime.now(ride.getEventTimeZone()));
				break;
				
			case CANCELED:
				// only the user can cancel a request
				if (!cyclist.equals(user)) {
					throw new SAGRequestException(Reason.UNAUTHORIZED);
				}
				setCompletedAt(ZonedDateTime.now(ride.getEventTimeZone()));
				break;
				
			case ABORTED:
				// only the ride marshals can abort a request
				if (!ride.isUserIn(ride.getMarshals(), user)) {
					throw new SAGRequestException(Reason.UNAUTHORIZED);
				}
				setCompletedAt(ZonedDateTime.now(ride.getEventTimeZone()));
				break;
				
			case DELETED:
				// only the ride hosts can abort a request
				if (!ride.isUserIn(ride.getHostedBy().getAdmins(), user)) {
					throw new SAGRequestException(Reason.UNAUTHORIZED);
				}
				setCompletedAt(ZonedDateTime.now(ride.getEventTimeZone()));
				break;
				
			default:
				// the NEW status not allowed to be set for an update
				throw new SAGRequestException(Reason.UNAUTHORIZED);		
		}
		
		setStatus(status);
	}
	
	public Set<SAGRequestNote> getNotes(User user) {
		ride.verifyUserIn(ride.getHostedBy().getAdmins(), user);
		return notes;
	}
	
	public SAGRequestNote addNote(User user, Role role, String noteStr) {
		ride.verifyRoleForRide(user, role);
		SAGRequestNote note;
		if (notes == null) {
			notes = new HashSet<>();
			note = SAGRequestNote.create(noteStr, user, this, role);
			notes.add(note);
		}
		else {
			Optional<SAGRequestNote> targetNote = notes.stream()
					.filter(n -> n.getId().equals(SAGRequestNotePK.create(user, this, role)))
					.findAny();
			
			if (targetNote.isPresent()) {
				note = targetNote.get();
				note.setNote(noteStr);
			}
			else {
				note = SAGRequestNote.create(noteStr, user, this, role);
				notes.add(note);
			}
		}
		return note;
	}
	
	public SAGRequestNote getNoteForUser(User user, Role role) {
		ride.verifyRoleForRide(user, role);
		
		SAGRequestNote note = null;
		if (notes != null) {
			note = notes.stream()
					.filter(n -> n.getId().equals(SAGRequestNotePK.create(user, this, role)))
					.findAny()
					.orElse(null);
		}
		return note;
	}
	
	public void removeNoteForUser(User user, Role role, Optional<User> forUser) {
		SAGRequestNotePK key;
		
		if (ride.isUserIn(ride.getHostedBy().getAdmins(), user)) {
			// user can delete any note
			User targetUser = forUser.orElse(user);
			key = SAGRequestNotePK.create(targetUser, this, role);
		}
		else {
			ride.verifyRoleForRide(user, role);
			key = SAGRequestNotePK.create(user, this, role);
		}
		
		if (!notes.removeIf(note -> note.getId().equals(key))) {
			throw new UnknownDomainException(SAGRequestNote.class);
		}
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

	public static final String GET_FROM_REF_ID = "#SAGRequest.getFromRefId";
}