package org.nge.smartsag.domain;

import java.util.Collection;

public interface UserVerificationSupport {

	default void verifyUserIn(Collection<User> users, User targetUser) {
		if (targetUser == null || users.stream().noneMatch(u -> u.getId().equals(targetUser.getId()))) {
			throw new InvalidAdminException(targetUser);
		}
	}
	
	default boolean isUserIn(Collection<User> users, User targetUser) {
		return targetUser != null && users.stream().anyMatch(u -> u.getId().equals(targetUser.getId()));
	}
}