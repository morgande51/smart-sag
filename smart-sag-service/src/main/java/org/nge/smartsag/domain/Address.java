package org.nge.smartsag.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.Data;

@Embeddable
@Data
public class Address {
	
	@Column(name = "street_number", nullable = false)
	private String streetNumber;
	
	@Column(name = "street_name", nullable = false)
	private String streetName;
	
	@Column(nullable = false)
	private String city;
	
	@Column(nullable = false)
	private String state;
	
	@Column(nullable = false)
	private String zip;
	
	@Embedded
	private Coordinates coordinates;

	public static Address from(
			String number, 
			String name, 
			String city, 
			String state, 
			String zip, 
			Double x,
			Double y) 
	{
		Address addy = new Address();
		addy.city = city;
		addy.streetName = name;
		addy.streetNumber = number;
		addy.state = state;
		addy.zip = zip;
		addy.coordinates = Coordinates.from(x, y);
		return addy;
	}
}