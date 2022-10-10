package org.nge.smartsag.domain;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

@Data
public class User implements Serializable {

	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String phone;
	
	private Set<Organization> organizations;
	
	private static final long serialVersionUID = 1L;
}