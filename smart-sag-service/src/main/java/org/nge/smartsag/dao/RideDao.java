package org.nge.smartsag.dao;

import java.util.List;

import org.nge.smartsag.domain.Ride;

public interface RideDao {

	Ride getRide(Long id);

	List<Ride> findSAGSupportRideForUser(Long userId);

	List<Ride> findSAGSupportRideForUser(Long userId, boolean active);

	Ride getRideFromRefId(String refId);
}
