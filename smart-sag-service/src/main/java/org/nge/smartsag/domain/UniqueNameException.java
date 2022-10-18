package org.nge.smartsag.domain;

import org.nge.smartsag.SmartException;

import lombok.Getter;

public class UniqueNameException extends SmartException {
	
	@Getter
	private String name;
	
	public UniqueNameException(String name) {
		super();
		this.name = name;
	}

	private static final long serialVersionUID = 1L;
}
