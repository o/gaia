package org.ungur.gaia.resources;

import com.codahale.metrics.annotation.Timed;
import org.ungur.gaia.core.DatastoreInterface;
import org.ungur.gaia.dao.Event;
import org.ungur.gaia.dao.PushEvent;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    private final DatastoreInterface adapter;

    public EventResource(DatastoreInterface adapter) {
        this.adapter = adapter;
    }

    @Timed
    @POST
    public Response push(@Valid PushEvent pushEvent) {
        adapter.push(pushEvent.getName(), pushEvent.getIncrement(), pushEvent.getTimestamp());
        return Response.status(Response.Status.CREATED).build();
    }

    @Timed
    @GET
    @Path("{event}")
    public List<Event> query(@PathParam("event") String event, @QueryParam("start") Long start, @QueryParam("end") Long end, @QueryParam("resolution") String resolution) {
        return adapter.query(event, start, end, resolution);
    }


}
