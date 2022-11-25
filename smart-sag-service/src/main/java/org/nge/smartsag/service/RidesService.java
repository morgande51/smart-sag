package org.nge.smartsag.service;

import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.NoCache;
import org.nge.smartsag.aws.LocationSearchService;
import org.nge.smartsag.dao.RideDao;
import org.nge.smartsag.domain.Address;
import org.nge.smartsag.domain.Organization;
import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.SAGRequest;
import org.nge.smartsag.domain.UnknownDomainException;
import org.nge.smartsag.domain.User;

@Path("/rides")
@RolesAllowed("users")
@NoCache
@ApplicationScoped
public class RidesService extends ContextedUserSupport {
	
	private static final Logger log = Logger.getLogger(RidesService.class);
	
	@Inject
	RideDao rideDao;
	
	@Inject
	LocationSearchService locationService;
	
//	@Path("/search")
	@GET
	public Ride search(@QueryParam("refId") String refId) {
		Ride ride = rideDao.getRideFromRefId(refId);
		if (ride == null) {
			throw new UnknownDomainException(Ride.class);
		}
		return ride;
	}
	
	@POST
	@Transactional
	public Ride addRide(CreateRideRequest request) {
		log.debugf("Ride request: %s", request);
		User user = getAuthenticatedUser();
		Organization org = request.isPopUpRide() ? user.getPopUpOrg() : user.getAdminOrg(request.getHostOrg());
		Address geocodedAddress = locationService.getAddressFor(request.getRideLocation());
		Ride ride = org.createRide(
				request.getName(), 
				user, 
				request.getStartAt(), 
				request.getEndAt(), 
				geocodedAddress);
		return ride;
	}
	
	@Path("/{id}")
	@GET
	public Ride getRide(Long id) {
		User user = getAuthenticatedUser();
		Ride ride = rideDao.getRide(id);
		if (ride == null) {
			throw new UnknownDomainException(Ride.class);
		}
		ride.verifyAccess(user);
		return ride;
	}
	
	@Path("/{id}/marshals")
	@GET
	public ChildrenReference<Long, User> getMarshals(Long id) {
		Ride ride = getRide(id);
		return new ChildrenReference<>(ride.getMarshals(), "users");
	}
	
	@Path("/{id}/marshals")
	@POST
	@Transactional
	public ChildrenReference<Long, User> addHost(Long id, @QueryParam("email") String email) {
		Ride ride = getRide(id);
		User marshal = userDao.findByEmail(email);
		User admin = getAuthenticatedUser();
		ride.addMarshal(admin, marshal);
		return new ChildrenReference<>(ride.getMarshals(), "users");
	}
	
	@Path("/{id}/support")
	@GET
	public ChildrenReference<Long, User> getSupport(Long id) {
		Ride ride = getRide(id);
		return new ChildrenReference<>(ride.getSagSupporters(), "users");
	}
	
	@Path("/{id}/support")
	@POST
	@Transactional
	public ChildrenReference<Long, User> addSupport(Long id, @QueryParam("email") String email) {
		Ride ride = getRide(id);
		User sag = userDao.findByEmail(email);
		User admin = getAuthenticatedUser();
		ride.addSAG(admin, sag);
		return new ChildrenReference<>(ride.getSagSupporters(), "users");
	}
	
	@Path("/{id}/sagRequests")
	@GET
	public ChildrenReference<Long, SAGRequest> getSAGRequest(Long id, @QueryParam("active") @DefaultValue("true") boolean active) {
		Ride ride = getRide(id);
		Set<SAGRequest> sagRequest = active ? ride.getActiveSAGRequest() : ride.getSagRequests();
		return new ChildrenReference<>(sagRequest, "sag");
	}
}