package org.nge.smartsag.notifications;

import static org.nge.smartsag.notifications.SAGRequestEventType.*;
import static javax.enterprise.event.TransactionPhase.AFTER_SUCCESS;
import static javax.ws.rs.core.MediaType.SERVER_SENT_EVENTS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.logging.Logger;
import org.nge.smartsag.domain.SAGRequest;

@Path("/events/sagrequests")
//@RolesAllowed("users")
@ApplicationScoped
public class SAGRequestBroadcaster {
	
	private static final Logger log = Logger.getLogger(SAGRequestBroadcaster.class);
	
	SseBroadcaster requestsBroadcaster;
	
	Map<Long, SseBroadcaster> targetedRequestBroadcasterMap;
	
	@Context
	Sse sse;
	
	@PostConstruct
	public void init() {
		targetedRequestBroadcasterMap = new ConcurrentHashMap<>();
	}
	
	@GET
//	@Path("/subscribe")
	@Produces(SERVER_SENT_EVENTS)
	public void subscribe(@Context SseEventSink sink) {
		if (requestsBroadcaster == null) {
			requestsBroadcaster = sse.newBroadcaster();
			requestsBroadcaster.onClose((s) -> {
				log.debug("The all requests broadcaster is closing this connection");
				s.send(sse.newEvent("this broadcaster is closing..."));
			});
		}
		requestsBroadcaster.register(sink);
		//sink.send(sse.newEvent("You are registered on the all-events emitter"));
	}
	
	@GET
	@Path("/{id}")
	@Produces(SERVER_SENT_EVENTS)
	public void subscribe(@PathParam("id") Long id, @Context SseEventSink sink) {
		SseBroadcaster broadcaster;
		if (!targetedRequestBroadcasterMap.containsKey(id)) {
			broadcaster = sse.newBroadcaster();
			targetedRequestBroadcasterMap.put(id, broadcaster);
		}
		else {
			broadcaster = targetedRequestBroadcasterMap.get(id);
		}
		broadcaster.register(sink);
		//sink.send(sse.newEvent("You are registred for events for SAG: " + id));
	}
	
	public void broadcastUpdatedRequest(
			@Observes(during = AFTER_SUCCESS)
			@SAGRequestEvent(NOTCOMPLETED)
			final SAGRequest request) 
	{
		Long key = request.getId();
		log.debugf("About to broadcast update to the target: %s...", key);
		OutboundSseEvent event = sse.newEventBuilder()
				.data(request)
				.mediaType(APPLICATION_JSON_TYPE)
				.build();
		broadcastEvent(requestsBroadcaster, event);
		
		targetedRequestBroadcasterMap.entrySet().stream()
			.filter(e -> e.getKey().equals(key))
			.findAny()
			.ifPresent(e -> broadcastEvent(e.getValue(), event));
		
		log.debugf("... broadcast is completed.");
	}
	
	public void broadcastCompletedRequest(
			@Observes(during = AFTER_SUCCESS)
			@SAGRequestEvent(COMPLETED)
			final SAGRequest request) 
	{
		Long key = request.getId();
		log.debugf("About to broadcast completed request for target: %s...", key);
		OutboundSseEvent event = sse.newEventBuilder()
				.data(request)
				.mediaType(APPLICATION_JSON_TYPE)
				.build();
		broadcastEvent(requestsBroadcaster, event);
	
		Optional.ofNullable(targetedRequestBroadcasterMap.remove(key))
			.ifPresent(b -> broadcastEvent(b, event));
		
		log.debugf("... broadcast is completed.");
	}
	
	protected void broadcastEvent(SseBroadcaster broadcaster, OutboundSseEvent event) {
		try {
			broadcaster.broadcast(event);
		}
		catch (IllegalStateException e) {
			log.tracef(e, "Unable to broadcast because there are no listener open");
		}
	}
}