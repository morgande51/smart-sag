package org.nge.smartsag.service;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.NoCache;
import org.nge.smartsag.dao.RideDao;
import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.SAGRequest;
import org.nge.smartsag.domain.User;

@Path("/sagrequests")
@RolesAllowed("users")
@NoCache
@ApplicationScoped
public class SAGRequestService extends ContextedUserSupport {
	
	@Inject
	RideDao rideDao;
	
	@Inject
	Event<SAGRequest> sagEvent;
	
	@POST
	@Transactional
	public SAGRequest createSAGRequest(CreateSAGRequest request) {
		User cyclist = getAuthenticatedUser();
		Ride ride = rideDao.getRide(request.getRideId());
		SAGRequest sagRequest = ride.requestSAG(
				cyclist, 
				request.getLatitude(), 
				request.getLongitude(),
				request.getCode());
		sagEvent.fire(sagRequest);
		return sagRequest;
	}
}