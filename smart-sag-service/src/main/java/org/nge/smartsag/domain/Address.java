package org.nge.smartsag.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class Address implements Serializable {

	private String streetName;
	
	private String city;
	
	private String state;
	
	private String zip;
	
	private Coordinates coordinates;
	
	private static final long serialVersionUID = 1L;
}