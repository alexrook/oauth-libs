package oul.web.tools.oauth.demo;

import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import oul.web.tools.oauth.AbstractSessionFilter;
import oul.web.tools.oauth.profile.Profile;

@Path("/demo/todo")
public class TodoRS {

    @Inject
    TodoStorage storage;

    private interface ICallback {

        Response callback(Profile user);
    }

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

        return call(user, new ICallback() {

            @Override
            public Response callback(Profile user) {
                return Response.ok(storage.list(user.getId())).build();
            }
        });
    }

    @GET
    @Path("{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTodo(@Context HttpServletRequest request,
            @PathParam("id") int id) {
        Profile user = (Profile) request.getAttribute(AbstractSessionFilter.PRIFILE_ATTRIBUTE);

        return call(user, new ICallback() {

            @Override
            public Response callback(Profile user) {
                return Response.ok(storage.list(user.getId())).build();
            }
        });

    }

    /**
     * Updates Todo
     *
     * @param request
     * @param todoId
     * @param content
     * @param update
     * @return
     */
    @POST
    public Response postTodo(@Context HttpServletRequest request,
            @FormParam("todoId") final String todoId,
            @FormParam("content") final String content,
            @FormParam("update") final Date update) {

        Profile user = (Profile) request.getAttribute(AbstractSessionFilter.PRIFILE_ATTRIBUTE);

        return call(user, new ICallback() {

            @Override
            public Response callback(Profile user) {
                try {
                    String todoID = storage.edit(todoId, content, update);
                    return Response.noContent().build();
                } catch (TodoStorage.TodoNotFoundException ex) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
        });
    }

    /**
     * put new Todo to storage
     *
     * @param request
     * @param content
     * @param update
     * @return
     */
    @PUT
    public Response putTodo(@Context HttpServletRequest request,
            @FormParam("content") final String content,
            @FormParam("update") final Date update) {
        Profile user = (Profile) request.getAttribute(AbstractSessionFilter.PRIFILE_ATTRIBUTE);

        return call(user, new ICallback() {

            @Override
            public Response callback(Profile user) {
                String todoID = storage.put(user.getId(), content, update);

                return Response.created(
                        URI.create(todoID)
                ).build();
            }
        });
    }

    private Response call(Profile user, ICallback callback) {
        if (user != null) {
            return callback.callback(user);
        } else {
            return Response.serverError()
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }

    }

}
