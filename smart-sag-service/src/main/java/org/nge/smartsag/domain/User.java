package org.nge.smartsag.domain;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import lombok.Data;

@Data
public class User implements Serializable {

	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String phone;
	
	private Set<Organization> organizations;
	
	public static boolean isUserIn(Stream<User> users, Optional<User> targetUser) {
		return targetUser.isPresent() && users.anyMatch(u -> u.getId().equals(targetUser.get().getId()));
	}
	
	private static final long serialVersionUID = 1L;
}