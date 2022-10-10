package org.nge.smartsag.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class Coordinates implements Serializable {

	private Double lat;
	
	private Double lng;
	
	private static final long serialVersionUID = 1L;
}