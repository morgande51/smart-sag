package org.nge.smartsag.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sag_request_note")
@Data
@EqualsAndHashCode(exclude = {"role", "request", "user"})
@ToString(exclude = {"role", "request", "user"})
public class SAGRequestNote implements IdentifiableDomain<SAGRequestNotePK> {

	@EmbeddedId
	private SAGRequestNotePK id;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String note;
	
	@Setter(value = AccessLevel.NONE)
	@Column(name = "user_role", insertable = false, updatable = false)
	private Role role;
	
	@Setter(value = AccessLevel.NONE)
	@ManyToOne
	@JoinColumn(name = "request_id", insertable = false, updatable = false)
	private SAGRequest request;
	
	@Setter(value = AccessLevel.NONE)
	@ManyToOne
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;
	
	public static SAGRequestNote create(String noteStr, User user, SAGRequest request, Role role) {
		SAGRequestNotePK key = SAGRequestNotePK.create(user, request, role);
		SAGRequestNote note = new SAGRequestNote();
		note.setId(key);
		note.setNote(noteStr);
		return note;
	}
}