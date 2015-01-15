package oul.web.tools.oauth.demo;

import java.net.URI;
import java.util.Date;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author moroz
 */
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import oul.web.tools.oauth.AbstractSessionFilter;
import oul.web.tools.oauth.profile.Profile;

@Path("/demo/todo")
public class TodoRS {

    @Inject
    TodoStorage storage;

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTodos() {
        return Response.ok(storage.listAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTodos(@Context HttpServletRequest request) {
        Profile user = (Profile) request.getAttribute(AbstractSessionFilter.PRIFILE_ATTRIBUTE);
        if (user != null) {
            return Response.ok(storage.list(user.getId())).build();
        } else {
            return Response.serverError()
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }

    @GET
    @Path("{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTodo(@Context HttpServletRequest request,
            @PathParam("id") int id) {
        return Response.ok(storage.list(null)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putTodo(@Context HttpServletRequest request,
            @FormParam("content") String content,
            @FormParam("update") Date update) {
        return Response.created(
                URI.create(storage.put(null, content, update))
        ).build();
    }

}
