package oul.web.tools.oauth.demo;

import java.net.URI;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTodo(@Context HttpServletRequest request,
            @PathParam("id") final String id) {
        Profile user = (Profile) request.getAttribute(AbstractSessionFilter.PRIFILE_ATTRIBUTE);

        return call(user, new ICallback() {

            @Override
            public Response callback(Profile user) {
                return Response.ok(storage.get(id)).build();
            }
        });

    }

    /**
     * Updates Todo
     *
     * @param request
     * @param editTodo
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postTodo(@Context HttpServletRequest request,
            final Todo editTodo) {

        Profile user = (Profile) request.getAttribute(AbstractSessionFilter.PRIFILE_ATTRIBUTE);

        return call(user, new ICallback() {

            @Override
            public Response callback(Profile user) {
                try {
                    String todoID = storage.edit(editTodo.getTodoId(), editTodo.getContent(), editTodo.getUpdate());
                    return Response.noContent().build(); //?
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
     * @param newTodo
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putTodo(@Context HttpServletRequest request,
            final Todo newTodo) {
        Profile user = (Profile) request.getAttribute(AbstractSessionFilter.PRIFILE_ATTRIBUTE);

        return call(user, new ICallback() {

            @Override
            public Response callback(Profile user) {
                String todoID = storage.put(user.getId(), newTodo.getContent(),
                        newTodo.getUpdate());

                return Response.created(
                        URI.create("/demo/todo/" + todoID)
                ).build();
            }
        });
    }

    /**
     * deletes todo
     *
     * @param request
     * @param id - todo id
     * @return deleted todo id
     */
    @DELETE
    @Path("{id}")
    public Response deleteTodo(@Context HttpServletRequest request,
            @PathParam("id") final String id) {
        Profile user = (Profile) request.getAttribute(AbstractSessionFilter.PRIFILE_ATTRIBUTE);

        return call(user, new ICallback() {

            @Override
            public Response callback(Profile user) {

                try {
                    String todoID = storage.delete(id);
                    return Response.noContent().build();
                } catch (TodoStorage.TodoNotFoundException ex) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

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
