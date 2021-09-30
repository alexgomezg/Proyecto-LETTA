package es.uvigo.esei.daa.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.daa.dao.EventDAO;
import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.entities.Event;

/**
 * Rest resource used for managing Events
 * @author Aitor Gomez Taboada
 *
 */
@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventRest {
	
	private File deporte;
	private File cine;
	private File teatro;
	private File tv;
	private File series;
	private File libros;
	private File tiempo_libre;
	
	
	private final static Logger LOG = Logger.getLogger(EventRest.class.getName());
	private final EventDAO dao;
	
	public EventRest() {
		this(new EventDAO());
	}

	public EventRest(EventDAO dao) {
		this.dao = dao;
	}
	
	
	@GET
    public Response getHandler(@QueryParam("type") String type, @QueryParam("pag") Integer pag, @QueryParam("txtToFilt") String txtToFilt, 
    		@QueryParam("eventID") Integer eventID, @QueryParam("userID") Integer userID) {
        switch (type) {
            case "normal":
                return listNormalEvents((int) pag);
                
            case "important":
            	return listImportantEvents();
                
            case "filterNormal":
            	return listFilterNormalEvents((int) pag, txtToFilt);
            	
            case "numNormal":
                return numNormalEvents();
                
            case "numFilterNormal":
                return numFilterNormalEvents(txtToFilt);
            	
            case "isAssistantOfEvent":
               return isAssistantOfEvent((int) eventID, (int) userID);
            
            case "numUsersInEvent":
                return numUsersInEvent((int) eventID);
 
            default:
            	return null;
        }
    }

	@POST
    public Response postHandler(
    		@QueryParam("type") String type, 
    		@FormParam("name") String name,
    		@FormParam("description") String description,
    		@FormParam("location") String location,
    		@FormParam("day") Timestamp day,
    		@FormParam("tag") String tag,
    		@FormParam("important") Boolean important,
    		@FormParam("capacity") int capacity,
    		@FormParam("image") String image,
    		@FormParam("organizer_id") int organizer_id, 
    		@FormParam("eventID") int eventID, 
    		@FormParam("userID") int userID) {
        switch (type) {
            case "assistToEvent":

                return assistToEvent(eventID,userID);
                
            case "add":
				try {
					return add(name,description,location,day,tag,important,capacity,image,organizer_id);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
                
            default:
            	return null;
        }
    }
	
	@DELETE
    public Response deleteHandler(@QueryParam("type") String type,@QueryParam("eventID") Integer eventID, 
    		@QueryParam("userID") Integer userID) {
        switch (type) {
            case "unassistToEvent":
                return unassistToEvent(eventID,userID);
                
            case "delete":
            	return delete(eventID,userID);
                
            default:
            	return null;
        }
    }

	public Response listNormalEvents(int pag) {
		try {
			return Response.ok(this.dao.listNormalEvents(pag)).build();
		}catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error listing normal events", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	

	public Response listImportantEvents() {
		try {
			return Response.ok(this.dao.listImportantEvents()).build();
		}catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error listing important events", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	
    public Response listFilterNormalEvents(int pag, String txtToFilt) {
        try {
            return Response.ok(this.dao.listFilterNormalEvents(pag, txtToFilt)).build();
        }catch(IllegalArgumentException iae){
			LOG.log(Level.FINE, "Invalid event id at add method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
        }catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error listing filtering normal events", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
	

    public Response numNormalEvents() {
        try {
            return Response.ok(this.dao.numNormalEvents()).build();
        }catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error counting normal events", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
    
    

    public Response numFilterNormalEvents(String txtToFilt) {
        try {
            return Response.ok(this.dao.numFilterNormalEvents(txtToFilt)).build();
        }catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error counting normal events", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
    
    public Response isAssistantOfEvent(int id, int userid) {
        try {
            return Response.ok(this.dao.isAssistantOfEvent(userid,id)).build();
        }catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error getting assistant of user to event", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
    
	
	public Response assistToEvent(int eventid, int userid) {

		try {
			this.dao.assistToEvent(userid, eventid);
			return Response.ok(eventid).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid id at assistToEvent method");

			return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error confirming assistance to event", e);

			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	

	public Response add(
			String name,
			String description,
			String location,
			Timestamp day,
			String tag,
			Boolean important,
			int capacity,
			String image,
			int organizer_id
			) throws SQLException, IOException{
			String workingDir = System.getProperty("user.dir");
			
			workingDir = workingDir + "\\src\\main\\java\\es\\uvigo\\esei\\daa\\rest\\default-images";
			workingDir = workingDir.replace("\\target\\catalina-base", "");
			
			deporte = new File(workingDir+"\\sports.jpg");
			cine = new File(workingDir+"\\cinema.png");
			teatro = new File(workingDir+"\\theater.png");
			tv = new File(workingDir+"\\tv.png");
			series = new File(workingDir+"\\series.png");
			libros = new File(workingDir+"\\books.png");
			tiempo_libre = new File(workingDir+"\\free_time.png");
			
			FileInputStream fileInputStreamReader;
			byte[] bytes;
			LOG.log(Level.FINE, "image: "+ image);
			
			if(image.equals("none")) {

				switch (tag) {
				case "deporte": 
				 	fileInputStreamReader = new FileInputStream(deporte);
		            bytes = new byte[(int)deporte.length()];
		            fileInputStreamReader.read(bytes);
		            image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
					break;
				case "cine":
					fileInputStreamReader = new FileInputStream(cine);
		            bytes = new byte[(int)cine.length()];
		            fileInputStreamReader.read(bytes);
		            image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
					break;
				case "teatro":
					fileInputStreamReader = new FileInputStream(teatro);
		            bytes = new byte[(int)teatro.length()];
		            fileInputStreamReader.read(bytes);
		            image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
					break;
				case "tv":
					fileInputStreamReader = new FileInputStream(tv);
		            bytes = new byte[(int)tv.length()];
		            fileInputStreamReader.read(bytes);
		            image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
					break;
				case "series":
					fileInputStreamReader = new FileInputStream(series);
		            bytes = new byte[(int)series.length()];
		            fileInputStreamReader.read(bytes);
		            image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
					break;
				case "libros":
					fileInputStreamReader = new FileInputStream(libros);
		            bytes = new byte[(int)libros.length()];
		            fileInputStreamReader.read(bytes);
		            image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
					break;
				case "tiempo_libre":

					fileInputStreamReader = new FileInputStream(tiempo_libre);
		            bytes = new byte[(int)tiempo_libre.length()];
		            fileInputStreamReader.read(bytes);
		            image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
					break;
				default:
					break;
				}
				
			}
			try {
				
				final Event newEvent = this.dao.add(name, description, location, day, tag, capacity, important, image, organizer_id);
				return Response.ok(newEvent).build();
			}catch(IllegalArgumentException iae){
				LOG.log(Level.FINE, "Invalid event id at add method", iae);
				
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(iae.getMessage())
				.build();
			} catch (DAOException e) {
				LOG.log(Level.SEVERE, "Error adding an event", e);		
				return Response.serverError()
					.entity(e.getMessage())
				.build();
			}
			
		}
    
	
	public Response delete(int event_id,int organizer_id) {
			try {
				this.dao.delete(event_id,organizer_id);
				return Response.ok(event_id).build();
			}catch(IllegalArgumentException iae) {
				LOG.log(Level.FINE, "Invalid event id at delete method");
				
				return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
			}catch(DAOException e) {
				LOG.log(Level.SEVERE, "Error deleting an event", e);
				
				return Response.serverError().entity(e.getMessage()).build();
			}
	}
	

	public Response unassistToEvent(int eventid, int userid) {
		try {
			this.dao.deleteUserFromEvent(userid, eventid);
			return Response.ok(eventid).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid id at unassistToEvent method");
			return Response.status(Response.Status.BAD_REQUEST).entity(iae.getMessage()).build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error confirming unassistance to event", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	

    public Response numUsersInEvent(int eventID) {
        try {
            return Response.ok(this.dao.numUsersInEvent(eventID)).build();
        }catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error counting normal events", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
	
	
	
}
