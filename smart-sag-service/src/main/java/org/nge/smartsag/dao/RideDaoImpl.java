package org.nge.smartsag.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.nge.smartsag.domain.Ride;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class RideDaoImpl implements RideDao, PanacheRepository<Ride> {

	private static final String REFERENCE_ID = "referenceId";
	private static final String ID = "id";
	private static final String USER_ID = "userId";
	
	@Override
	public Ride getRide(Long id) {
		return find(Ride.GET_WITH_HOST_AND_SAG, Parameters.with(ID, id)).firstResult();
	}
	
	@Override
	public Ride getRideFromRefId(String refId) {
		return find(Ride.GET_FROM_REF_ID, Parameters.with(REFERENCE_ID, refId)).firstResult();
	}
	
	@Override
	public List<Ride> findRidesForSAGSupport(Long userId) {
		return list(Ride.FIND_FOR_SAG_SUPPORT, Parameters.with(USER_ID, userId));
	}
	
	@Override
	public List<Ride> findRidesForSAGSupport(Long userId, boolean active) {
		return stream(Ride.FIND_FOR_SAG_SUPPORT, Parameters.with(USER_ID, userId))
				.filter(r -> r.isActive() == active)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Ride> findRidesForMarshal(Long userId) {
		return list(Ride.FIND_FOR_MARSHALS, Parameters.with(USER_ID, userId));
	}
	
	@Override
	public List<Ride> findRidesForMarshal(Long userId, boolean active) {
		return stream(Ride.FIND_FOR_MARSHALS, Parameters.with(USER_ID, userId))
				.filter(r -> r.isActive() == active)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Ride> findRidesForAdmin(Long userId) {
		return list(Ride.FIND_FOR_ADMINS, Parameters.with(USER_ID, userId));
	}
	
	@Override
	public List<Ride> findRidesForAdmin(Long userId, boolean active) {
		return stream(Ride.FIND_FOR_ADMINS, Parameters.with(USER_ID, userId))
				.filter(r -> r.isActive() == active)
				.collect(Collectors.toList());
	}
}