package org.nge.smartsag.domain;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "org")
@Data
@EqualsAndHashCode(exclude = {"primaryContact","admins","rides"})
@ToString(exclude = {"primaryContact","admins","rides"})
@NamedQueries(@NamedQuery(name = "Organization.findByAdmin", query = "select o from Organization o join o.admins a where a.email like ?1"))
public class Organization {

	@Id
	@SequenceGenerator(name = "orgSeq", sequenceName = "user_org_seq", allocationSize = 1, initialValue = 1000)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orgSeq")
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "contact_user", nullable = false)
	private User primaryContact;
	
	@ManyToMany
	@JoinTable(name = "org_admin",
	           joinColumns = @JoinColumn(name = "user_org_id", referencedColumnName = "id"),
			   inverseJoinColumns = @JoinColumn(name = "sag_user_id", referencedColumnName = "id"))
	@JsonbTransient
	private Set<User> admins;
	
	@OneToMany(mappedBy = "hostedBy", orphanRemoval = true, cascade = CascadeType.ALL)
	@JsonbTransient
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
	
	public boolean canRemove() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static Organization createOrg(String name, User user) {
		Organization org = new Organization();
		org.setName(name);
		org.setPrimaryContact(user);
		org.setAdmins(Collections.singleton(user));
		return org;
	}
	
	public static final String FIND_BY_ADMIN = "#Organization.findByAdmin";
}