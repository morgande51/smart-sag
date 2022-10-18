package org.nge.smartsag.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.logging.Logger;
import org.nge.smartsag.dao.OrganizationDao;
import org.nge.smartsag.dao.UserDao;
import org.nge.smartsag.domain.Organization;
import org.nge.smartsag.domain.User;

import lombok.Getter;

@Path("/orgs")
@ApplicationScoped
public class OrganizationService implements ContextedUserSupport {
	
	private static final Logger log = Logger.getLogger(OrganizationService.class);
	
	@Inject
	OrganizationDao orgDao;
	
	@Inject
	@Getter
	UserDao userDao;
	
	@Path("/{id}")
	@GET
	public Organization getOrg(Long id) {
		return orgDao.get(id);
	}
	
	@Path("/{id}")
	@DELETE
	@Transactional(TxType.REQUIRED)
	public void removeOrg(Long id) {
		User user = getAuthenticatedUser();
		user.removeOrg(id);
	}
	
	@Path("/{id}")
	@PATCH
	@Transactional(TxType.REQUIRED)
	public Organization updateName(Long id, @QueryParam("name") String name) {
		log.debugf("target name: {}", name);
		User user = getAuthenticatedUser();
		Organization org = user.updateOrg(id, name);
		log.debugf("We updated org: {}", org);
		return org;
	}
	
	@Path("/{id}/admins")
	@GET
	public List<User> getOrgAdmins(Long id) {
		return userDao.findAdminByOrgId(id);
	}
	
	@POST
	@Transactional(TxType.REQUIRED)
	public Organization createOrg(@QueryParam("name") String name) {
		log.debugf("target name: %s", name);
		User user = getAuthenticatedUser();
		return user.createOrg(name);
	}
}