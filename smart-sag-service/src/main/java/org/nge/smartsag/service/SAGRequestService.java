package org.nge.smartsag.service;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.reactive.NoCache;
import org.nge.smartsag.dao.RideDao;
import org.nge.smartsag.dao.SAGRequestDao;
import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.SAGRequest;
import org.nge.smartsag.domain.SAGRequestStatusType;
import org.nge.smartsag.domain.UnknownDomainException;
import org.nge.smartsag.domain.User;
import org.nge.smartsag.notifications.SAGRequestEventType;

@Path("/sagrequests")
@RolesAllowed("users")
@NoCache
@ApplicationScoped
public class SAGRequestService extends ContextedUserSupport {
	
	@Inject
	RideDao rideDao;
	
	@Inject
	SAGRequestDao sagRequestDao;
	
	@Inject
	Event<SAGRequest> sagEvents;
	
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
		getEventFor(sagRequest.getStatus()).fire(sagRequest);
		return sagRequest;
	}
	
	@Path("/{id}")
	@GET
	public SAGRequest getSAGRequest(@PathParam("id") Long id) {
		return sagRequestDao.getRequest(id);
	}
	
	@Path("/{id}")
	@PATCH
	@Transactional
	public SAGRequest updateStatus(@PathParam("id") Long id, UpdateStatusRequest request) {
		User user = getAuthenticatedUser();
		SAGRequest sagRequest = sagRequestDao.getRequestForUpdate(id);
		sagRequest.updateStatus(user, request.getStatus());
		getEventFor(sagRequest.getStatus()).fire(sagRequest);
		return sagRequest;
	}
	
	@Path("/search")
	@GET
	public SAGRequest search(@QueryParam("refId") String refId) {
		SAGRequest request = sagRequestDao.getRequestFromRefId(refId);
		if (request == null) {
			throw new UnknownDomainException(SAGRequest.class);
		}
		return request;
	}
	
	protected Event<SAGRequest> getEventFor(SAGRequestStatusType status) {
		return sagEvents.select(SAGRequestEventType.getLiteral(status));
	}
}