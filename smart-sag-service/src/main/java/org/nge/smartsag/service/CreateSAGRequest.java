package org.nge.smartsag.service;

import java.io.Serializable;

import lombok.Data;

@Data
public class CreateSAGRequest implements Serializable {
	
	private long rideId;
	
	private char code;
	
	private double latitude;
	
	private double longitude;

	private static final long serialVersionUID = 1L;
}