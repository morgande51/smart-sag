package org.nge.smartsag.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.Data;

@Embeddable
@Data
public class Address {
	
	@Column(name = "street_name", nullable = false)
	private String streetName;
	
	@Column(nullable = false)
	private String city;
	
	@Column(length = 2, nullable = false)
	private String state;
	
	@Column(nullable = false)
	private Integer zip;
	
	@Embedded
	private Coordinates coordinates;
}