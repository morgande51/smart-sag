package org.nge.smartsag.service;

import static org.nge.smartsag.service.ActiveSAGRequest.ContextType.*;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
	
	private static enum RIDE_SUPPORT_TYPES {MARSHAL, HOST, SUPPORT};
	
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
		
		log.debugf("Context Response: %s", response);
		return response;
	}
	
	@Path("/me/{rideType}")
	@GET
	public ChildrenReference<Long, Ride> getMyRides(
			@PathParam("rideType") String rt,
			@QueryParam("active") @DefaultValue("true") boolean active) 
	{
		log.debugf("We have the following ride type: %s", rt);
		User me = getAuthenticatedUser();
		List<Ride> rides;
		
		switch (RIDE_SUPPORT_TYPES.valueOf(rt)) {
			case HOST:
				rides = rideDao.findRidesForAdmin(me.getId(), active);
				break;
				
			case MARSHAL:
				rides = rideDao.findRidesForMarshal(me.getId(), active);
				break;
				
			default:
				rides = rideDao.findRidesForSAGSupport(me.getId(), active);
		}
		
		return new ChildrenReference<>(rides, "rides");
	}
}