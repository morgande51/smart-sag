package org.nge.smartsag.service;

import org.nge.smartsag.dao.UserDao;
import org.nge.smartsag.domain.User;

public interface ContextedUserSupport {
	
	public UserDao getUserDao();
	
	default User getAuthenticatedUser() {
		return getUserDao().findByEmail(getAuthSubjectEmail());
	}
	
	// TODO: get this from IAM
	default String getAuthSubjectEmail() {
		return "test@test.com";
	}
}