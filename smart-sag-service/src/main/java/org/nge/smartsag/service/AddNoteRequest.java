package org.nge.smartsag.service;

import java.io.Serializable;

import org.nge.smartsag.domain.Role;

import lombok.Data;

@Data
public class AddNoteRequest implements Serializable {
	
	private Role role;
	
	private String note;

	private static final long serialVersionUID = 1L;
}