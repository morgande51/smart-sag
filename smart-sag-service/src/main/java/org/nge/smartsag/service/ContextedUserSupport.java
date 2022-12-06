package org.nge.smartsag.service;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.nge.smartsag.dao.UserDao;
import org.nge.smartsag.domain.User;

import io.quarkus.oidc.UserInfo;
import io.quarkus.security.identity.SecurityIdentity;

public abstract class ContextedUserSupport {
	
	private static final Logger log = Logger.getLogger(ContextedUserSupport.class);
	
	@Inject
	UserDao userDao;
	
	@Inject
	SecurityIdentity securityIdentity;
	
	@Inject
	UserInfo userInfo;
	
	protected User getAuthenticatedUser() {
		return userDao.findByEmail(getAuthSubjectEmail());
	}
	
	protected String getAuthSubjectEmail() {
		log.tracef("The subject: %s", securityIdentity.getPrincipal());
		String username = securityIdentity.getPrincipal().getName();
		String email = userInfo.getString("email");
		log.tracef("Authenticated user[%s] email: %s", username, email);
		return email;
	}
}