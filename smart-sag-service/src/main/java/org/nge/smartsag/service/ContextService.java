package org.nge.smartsag.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.nge.smartsag.dao.RideDao;
import org.nge.smartsag.dao.SAGRequestDao;
import org.nge.smartsag.dao.UserDao;
import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.SAGRequest;
import org.nge.smartsag.domain.User;

import lombok.Getter;

@ApplicationScoped
@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
public class ContextService implements ContextedUserSupport {
	
	private static final Logger log = Logger.getLogger(ContextService.class);
	
	@Inject
	@Getter
	UserDao userDao;
	
	@Inject
	SAGRequestDao sagDao;
	
	@Inject
	RideDao rideDao;
	
	@GET
	public ContextResponse whoAmiI() {
		User me = getAuthenticatedUser();
		List<SAGRequest> sagRequests = sagDao.findSAGRequestForUser(me.getId(), true);
		List<Ride> rides = rideDao.findSAGSupportRideForUser(me.getId(), true);
		
		ContextResponse response;
		if (sagRequests != null && !sagRequests.isEmpty()) {
			response = new ContextResponse(me, sagRequests.get(0));
		}
		else if (rides != null && !rides.isEmpty()) {
			response = new ContextResponse(me, rides.get(0));
		}
		else {
			response = new ContextResponse(me);
		}
		
		log.debugf("Context Response: %s", response);
		return response;
	}
	
	// TODO: remove me ASAP
	public String getAuthSubjectEmail() {
		return "sag@test.com";
	}
}