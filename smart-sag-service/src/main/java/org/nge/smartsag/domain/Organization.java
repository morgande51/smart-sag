package org.nge.smartsag.domain;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

@Data
public class Organization implements Serializable {
	
	private Long id;
	
	private String name;
	
	private User primaryContact;
	
	private Set<User> admins;
	
	private Set<Ride> rides;

	private static final long serialVersionUID = 1L;
}