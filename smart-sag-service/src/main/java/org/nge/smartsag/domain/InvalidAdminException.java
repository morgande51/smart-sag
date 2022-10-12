package org.nge.smartsag.domain;

import org.nge.smartsag.SmartException;

import lombok.Getter;

public class InvalidAdminException extends SmartException {
	
	@Getter
	private User user;

	public InvalidAdminException(User user) {
		this.user = user;
	}

	private static final long serialVersionUID = 1L;
}