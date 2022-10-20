package org.nge.smartsag.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.nge.smartsag.domain.Ride;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class RideDaoImpl implements RideDao, PanacheRepository<Ride> {

	@Override
	public Ride getRide(Long id) {
		return find(Ride.GET_WITH_HOST_AND_SAG, id).firstResult();
	}
	
	@Override
	public List<Ride> findSAGSupportRideForUser(Long userId) {
		return list(Ride.FIND_FOR_SAG_SUPPORT, userId);
	}
	
	@Override
	public List<Ride> findSAGSupportRideForUser(Long userId, boolean active) {
		return stream(Ride.FIND_FOR_SAG_SUPPORT, userId)
				.filter(r -> r.isActive() == active)
				.collect(Collectors.toList());
	}
}