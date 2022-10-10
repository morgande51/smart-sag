package org.nge.smartsag.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class SAGRequestId implements Serializable {
	
	private User cyclist;
	
	private Ride ride;
	
	private static final long serialVersionUID = 1L;
}