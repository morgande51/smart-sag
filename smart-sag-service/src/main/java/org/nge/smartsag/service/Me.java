package org.nge.smartsag.service;

import org.nge.smartsag.domain.User;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Me {
	
	@Setter
	private User account;
	
	@Setter
	private ActiveSAGRequest activeSAGRequest;
	
	public Me(User account) {
		this.account = account;
	}
}