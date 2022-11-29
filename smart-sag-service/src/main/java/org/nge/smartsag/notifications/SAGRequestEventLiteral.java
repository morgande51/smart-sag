package org.nge.smartsag.notifications;

import javax.enterprise.util.AnnotationLiteral;

public class SAGRequestEventLiteral extends AnnotationLiteral<SAGRequestEvent> implements SAGRequestEvent {

	private SAGRequestEventType type;
	
	public SAGRequestEventLiteral(SAGRequestEventType type) {
		this.type = type;
	}
	
	@Override
	public SAGRequestEventType value() {
		return type;
	}

	private static final long serialVersionUID = 1L;
}
