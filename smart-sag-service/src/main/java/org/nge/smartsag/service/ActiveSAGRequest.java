package org.nge.smartsag.service;

import lombok.Getter;

@Getter
public class ActiveSAGRequest {
	
	public enum ContextType {REQUEST, SUPPORT};
	
	private ContextType context;
	
	private Long id;

	public ActiveSAGRequest(ContextType context, Long id) {
		super();
		this.context = context;
		this.id = id;
	}
}
