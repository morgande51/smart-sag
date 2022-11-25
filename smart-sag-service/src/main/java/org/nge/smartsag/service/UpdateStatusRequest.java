package org.nge.smartsag.service;

import java.io.Serializable;

import org.nge.smartsag.domain.SAGRequestStatusType;

import lombok.Data;

@Data
public class UpdateStatusRequest implements Serializable {
	
	private SAGRequestStatusType status;
	
	private static final long serialVersionUID = 1L;
}