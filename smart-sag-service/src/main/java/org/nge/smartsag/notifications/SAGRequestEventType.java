package org.nge.smartsag.notifications;

import javax.enterprise.util.AnnotationLiteral;

import org.nge.smartsag.domain.SAGRequestStatusType;

public enum SAGRequestEventType {
	
	NOTCOMPLETED, COMPLETED;
	
	public static AnnotationLiteral<SAGRequestEvent> getLiteral(SAGRequestStatusType status) {
		SAGRequestEventType eventType;
		
		switch (status) {
			case NEW:
			case ACKNOWLEDGED:
				eventType = NOTCOMPLETED;
				break;
				
			default:
				eventType = COMPLETED;
		}
		
		return new SAGRequestEventLiteral(eventType);
	};
}