package org.nge.smartsag.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class Coordinates {

	@Column(name = "latitude", nullable = false)
	private Double lat;
	
	@Column(name = "longitude", nullable = false)
	private Double lng;

	public static Coordinates from(Double x, Double y) {
		Coordinates c = new Coordinates();
		c.lat = x;
		c.lng = y;
		return c;
	}
}