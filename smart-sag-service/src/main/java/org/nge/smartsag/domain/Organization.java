package org.nge.smartsag.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.Data;

@Data
public class Organization implements Serializable {
	
	private Long id;
	
	private String name;
	
	private User primaryContact;
	
	private Set<User> admins;
	
	private Set<Ride> rides;
	
	public Ride createRide(String name, User admin, ZonedDateTime startAt, ZonedDateTime endAt, Address location) {
		if (!User.isUserIn(admins.stream(), Optional.of(admin))) {
			// TOODO: throw exception
		}
		if (rides == null) {
			rides = new HashSet<>();
		}
		Ride ride = Ride.createRide(admin, name, startAt, endAt, location);
		rides.add(ride);
		return ride;
	}
	
	public Ride removeRide(Long rideId, User admin) {
		if (!User.isUserIn(admins.stream(), Optional.of(admin))) {
			// TOODO: throw exception
		}
		Ride ride = rides.stream()
				.filter(r -> r.getId().equals(rideId))
				.findAny()
				.orElseThrow(() -> new RuntimeException());
		
		// make sure this ride does not have any active SAG request
		if (ride.hasActiveSAGRequest()) {
			// TODO: handle this
		}
		rides.remove(ride);
		return ride;
	}

	private static final long serialVersionUID = 1L;
}