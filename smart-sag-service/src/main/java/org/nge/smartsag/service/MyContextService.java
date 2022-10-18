package org.nge.smartsag.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.nge.smartsag.dao.OrganizationDao;
import org.nge.smartsag.dao.UserDao;
import org.nge.smartsag.domain.Organization;
import org.nge.smartsag.domain.User;

import lombok.Getter;

@ApplicationScoped
@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
public class MyContextService implements ContextedUserSupport {
	
	private static final Logger log = Logger.getLogger(MyContextService.class);
	
	@Inject
	@Getter
	UserDao userDao;
	
	@Inject
	OrganizationDao orgDao;
	
	@GET
	public User whoAmiI() {
		return getAuthenticatedUser();
	}
	
	@Path("/orgs")
	@GET
	public List<Organization> getMyOrgs() {
		log.info("testing logging!");
		return orgDao.findOrgsByAdminEmail(getAuthSubjectEmail());
	}
}