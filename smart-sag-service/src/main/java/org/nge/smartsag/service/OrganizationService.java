package org.nge.smartsag.service;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.nge.smartsag.dao.OrganizationDao;
import org.nge.smartsag.domain.Organization;
import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.User;

@Path("/orgs")
@ApplicationScoped
public class OrganizationService extends ContextedUserSupport {
	
	@Inject
	OrganizationDao orgDao;
	
	@POST
	@Transactional(TxType.REQUIRED)
	public Organization createOrg(@QueryParam("name") String name) {
		User user = getAuthenticatedUser();
		return user.createOrg(name);
	}
	
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
		user.removePrimayOrg(id);
	}
	
	@Path("/{id}")
	@PATCH
	@Transactional(TxType.REQUIRED)
	public Organization updateName(Long id, @QueryParam("name") String name) {
		User user = getAuthenticatedUser();
		Organization org = user.updatePrimayOrg(id, name);
		return org;
	}
	
	@Path("/{id}/admins")
	@GET
	public ChildrenReference<Long, User> getOrgAdmins(Long id) {
		return new ChildrenReference<>(userDao.findAdminByOrgId(id), "users");
	}
	
	@Path("/{id}/admins")
	@POST
	@Transactional
	public ChildrenReference<Long, User> addOrgAdmin(Long id, String adminEmail) {
		User orgAdmin = getAuthenticatedUser();
		User target = userDao.findByEmail(adminEmail);
		Organization org = orgDao.get(id);
		org.addAdmin(orgAdmin, target);
		return new ChildrenReference<>(org.getAdmins(), "users");
	}
	
	@Path("/{id}/rides")
	@GET
	public ChildrenReference<Long, Ride> getOrgRides(Long id, @QueryParam("active") @DefaultValue("true") boolean active) {
		Organization org = orgDao.get(id);
		Set<Ride> rides = active ? org.getActiveRides() : org.getRides();
		return new ChildrenReference<>(rides, "rides");
	}
}