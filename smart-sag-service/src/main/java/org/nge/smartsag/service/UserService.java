package org.nge.smartsag.service;

import static org.nge.smartsag.service.ActiveSAGRequest.ContextType.*;

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
import org.nge.smartsag.service.ActiveSAGRequest.ContextType;

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
		List<SAGRequest> sagRequests = sagDao.findRequestForUser(me.getId(), true);
		List<Ride> supportRides = rideDao.findRidesForSAGSupport(me.getId(), true);
		List<Ride> marshalRides = rideDao.findRidesForMarshal(me.getId(), true);
		List<Ride> adminRides = rideDao.findRidesForAdmin(me.getId(), true);
		
		Me response = new Me(me);
		if (sagRequests != null && !sagRequests.isEmpty()) {
			SAGRequest sagRequest = sagRequests.get(0);
			ContextType context;
			if (sagRequest.getCyclist().getId().equals(me.getId())) {
				context = REQUEST;
			}
			else {
				context = SUPPORT;
			}
			response.setActiveSAGRequest(new ActiveSAGRequest(context, sagRequest.getId()));
		}
		
		if (supportRides != null && !supportRides.isEmpty()) {
			response.setSupportRides(supportRides);
		}
		
		if (marshalRides != null && !marshalRides.isEmpty()) {
			response.setMarshalRides(marshalRides);
		}
		
		if (adminRides != null && !adminRides.isEmpty()) {
			response.setAdminRides(adminRides);
		}
		
		log.debugf("Context Response: %s", response);
		return response;
	}
}