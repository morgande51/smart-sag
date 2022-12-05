package org.nge.smartsag.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class SAGRequestNotePK implements Serializable {

	@Column(name = "user_role", nullable = false)
	private Role role;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "request_id", nullable = false)
	private Long requestId;
	
	public static SAGRequestNotePK create(User user, SAGRequest request, Role role) {
		SAGRequestNotePK key = new SAGRequestNotePK();
		key.setRequestId(request.getId());
		key.setUserId(user.getId());
		key.setRole(role);
		return key;
	}
	
	private static final long serialVersionUID = 1L;
}