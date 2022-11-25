package org.nge.smartsag.dao;

import java.util.List;

import org.nge.smartsag.domain.Ride;

public interface RideDao {

	Ride getRide(Long id);
	
	Ride getRideFromRefId(String refId);

	List<Ride> findRidesForSAGSupport(Long userId);

	List<Ride> findRidesForSAGSupport(Long userId, boolean active);

	List<Ride> findRidesForMarshal(Long userId);

	List<Ride> findRidesForMarshal(Long userId, boolean active);
	
	List<Ride> findRidesForAdmin(Long userId);
	
	List<Ride> findRidesForAdmin(Long userId, boolean active);
}
