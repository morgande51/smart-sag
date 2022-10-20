package org.nge.smartsag.service;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.nge.smartsag.dao.RideDao;
import org.nge.smartsag.dao.UserDao;
import org.nge.smartsag.domain.Address;
import org.nge.smartsag.domain.Organization;
import org.nge.smartsag.domain.Ride;
import org.nge.smartsag.domain.SAGRequest;
import org.nge.smartsag.domain.UnknownDomainException;
import org.nge.smartsag.domain.User;

import lombok.Getter;

@Path("/rides")
@ApplicationScoped
public class RidesService implements ContextedUserSupport {
	
	@Inject
	@Getter
	UserDao userDao;
	
	@Inject
	RideDao rideDao;
	
	@POST
	@Transactional
	public Ride addRide(CreateRideRequest request) {
		User user = getAuthenticatedUser();
		Organization org = request.isPopUpRide() ? user.getPopUpOrg() : user.getAdminOrg(request.getHostOrg());
		
		Ride ride = org.createRide(
				request.getName(), 
				user, 
				request.getStartAt(), 
				request.getEndAt(), 
				getAddressFor(request.getRideLocation()));
		return ride;
	}
	
	@Path("/{id}")
	@GET
	public Ride getRide(Long id) {
		User user = getAuthenticatedUser();
		Ride ride = rideDao.getRide(id);
		if (ride == null) {
			throw new UnknownDomainException();
		}
		ride.verifyAccess(user);
		return ride;
	}
	
	@Path("/{id}/hosts")
	@GET
	public ChildrenReference<Long, User> getRideHosts(Long id) {
		Ride ride = getRide(id);
		return new ChildrenReference<>(ride.getHosts(), "users");
	}
	
	@Path("/{id}/hosts")
	@POST
	@Transactional
	public ChildrenReference<Long, User> getRideHosts(Long id, @QueryParam("email") String email) {
		Ride ride = getRide(id);
		User host = userDao.findByEmail(email);
		User admin = getAuthenticatedUser();
		ride.addHost(admin, host);
		return new ChildrenReference<>(ride.getHosts(), "users");
	}
	
	@Path("/{id}/support")
	@GET
	public ChildrenReference<Long, User> getRideSAGSupport(Long id) {
		Ride ride = getRide(id);
		return new ChildrenReference<>(ride.getSagSupporters(), "users");
	}
	
	@Path("/{id}/support")
	@POST
	@Transactional
	public ChildrenReference<Long, User> getRideSAGSupport(Long id, @QueryParam("email") String email) {
		Ride ride = getRide(id);
		User sag = userDao.findByEmail(email);
		User admin = getAuthenticatedUser();
		ride.addSAG(admin, sag);
		return new ChildrenReference<>(ride.getSagSupporters(), "users");
	}
	
	@Path("/{id}/sags")
	@GET
	public ChildrenReference<Long, SAGRequest> getRideSAGRequest(Long id, @QueryParam("active") @DefaultValue("true") boolean active) {
		Ride ride = getRide(id);
		Set<SAGRequest> sagRequest = active ? ride.getActiveSAGRequest() : ride.getSagRequests();
		return new ChildrenReference<>(sagRequest, "sag");
	}
	
	protected Address getAddressFor(String location) {
		return null;
	}
}