package es.uvigo.esei.daa.rest;
/**
 * REST resource used for managing Users
 */
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.UsersDAO;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersRest{
    private final static Logger LOG = Logger.getLogger(UsersRest.class.getName());

    private final UsersDAO dao;

    private @Context SecurityContext security;

    /**
     * Constructs a new instance of {@link UsersResource}.
     */
    public UsersRest() {
        this(new UsersDAO());
    }

    // Needed for testing purposes
    UsersRest(UsersDAO dao) {
        this(dao, null);
    }

    // Needed for testing purposes
    UsersRest(UsersDAO dao, SecurityContext security) {
        this.dao = dao;
        this.security = security;
    }

    @GET
    public Response get(
    		@QueryParam("login") String login,@QueryParam("password") String password
    ) throws DAOException {

        try {
            return Response.ok(dao.get(login,password)).build();
        } catch (IllegalArgumentException iae) {
            LOG.log(Level.FINE, "Invalid user login in get method", iae);

            return Response.status(Response.Status.BAD_REQUEST)
                .entity(iae.getMessage())
            .build();
        }
    }
}
