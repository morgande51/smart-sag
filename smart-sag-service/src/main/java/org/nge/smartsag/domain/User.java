package org.nge.smartsag.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "sag_user")
@Data
public class User {
	
	@Id
	@SequenceGenerator(name = "userSeq", sequenceName = "sag_user_seq", allocationSize = 1, initialValue = 1000)
	private Long id;
	
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	
	@Column(name = "phone", nullable = false, length = 10)
	private String phone;
	
	@OneToMany(mappedBy = "primaryContact", orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<Organization> organizations;
	
	public Organization createOrg(String name) {
		if (organizations == null) {
			organizations = new HashSet<>();
		}
		Organization org = Organization.createOrg(name, this);
		organizations.add(org);
		return org;
	}
	
	public static boolean isUserIn(Stream<User> users, Optional<User> targetUser) {
		return targetUser.isPresent() && users.anyMatch(u -> u.getId().equals(targetUser.get().getId()));
	}
}