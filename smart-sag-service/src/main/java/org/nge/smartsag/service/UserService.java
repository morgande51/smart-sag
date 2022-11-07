package org.nge.smartsag.service;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.NoCache;
import org.nge.smartsag.dao.RideDao;
import org.nge.smartsag.dao.SAGRequestDao;
import org.nge.smartsag.dao.UserDao;
import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.SAGRequest;
import org.nge.smartsag.domain.User;

@Path("/users")
@RolesAllowed("users")
@NoCache
@ApplicationScoped
public class UserService extends ContextedUserSupport {
	
	private static final Logger log = Logger.getLogger(UserService.class);
	
	@Inject
	UserDao userDao;
	
	@Inject
	SAGRequestDao sagDao;
	
	@Inject
	RideDao rideDao;
	
	@GET
	public User findUser(@QueryParam("email") String email, @QueryParam("phone") String phone) {
		User user = null;
		if (email == null && phone == null) {
			throw new IllegalArgumentException("email or phone is required");
		}
		
		if (phone != null) {
			user = userDao.findByPhone(phone);
		}
		else {
			user = userDao.findByEmail(email);
		}
		return user;
	}
	
	@Path("/{id}")
	@GET
	public User getUser(Long id) {
		return userDao.getUser(id);
	}
	
	@Path("/me")
	@GET
	public Me whoAmiI() {
		User me = getAuthenticatedUser();
		List<SAGRequest> sagRequests = sagDao.findSAGRequestForUser(me.getId(), true);
		List<Ride> rides = rideDao.findSAGSupportRideForUser(me.getId(), true);
		
		Me response;
		if (sagRequests != null && !sagRequests.isEmpty()) {
			response = new Me(me, sagRequests.get(0));
		}
		else if (rides != null && !rides.isEmpty()) {
			response = new Me(me, rides.get(0));
		}
		else {
			response = new Me(me);
		}
		
		log.debugf("Context Response: %s", response);
		return response;
	}
}