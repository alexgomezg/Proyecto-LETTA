package es.uvigo.esei.daa.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Base64;
import javax.sql.rowset.serial.SerialException;


import es.uvigo.esei.daa.entities.Event;

/**
 * DAO class for the {@link Event} entities.
 *
 * @author Alejandro Gómez González
 *
 */
public class EventDAO extends DAO {

    private final static Logger LOG = Logger.getLogger(EventDAO.class.getName());

    /**
     * Returns an event stored persisted in the system.
     *
     * @param id identifier of the event.
     * @return a event with the provided identifier.
     * @throws DAOException if an error happens while retrieving the person.
     * @throws IllegalArgumentException if the provided id does not corresponds
     * with any persisted event.
     */
    public Event get(int id)
            throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM events WHERE id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        throw new IllegalArgumentException("Invalid id");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting an event", e);
            throw new DAOException(e);
        }
    }

    /**
     * Returns a list with all the events persisted in the system.
     *
     * @return a list with all the events persisted in the system.
     * @throws DAOException if an error happens while retrieving the people.
     */
    public List<Event> list() throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM events";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                try (final ResultSet result = statement.executeQuery()) {
                    final List<Event> events = new LinkedList<>();

                    while (result.next()) {
                        events.add(rowToEntity(result));
                    }

                    return events;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing events", e);
            throw new DAOException(e);
        }
    }
    
    
    public List<Event> listNormalEvents(int numPagina) throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM events WHERE day>=? and important=FALSE ORDER BY day limit ?,9";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                statement.setTimestamp(1, timestamp);
                statement.setInt(2, numPagina);
                try (final ResultSet result = statement.executeQuery()) {

                    final List<Event> events = new LinkedList<>();
                    while (result.next()) {
                        events.add(rowToEntity(result));
                    }
            		
                    return events;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing events", e);
            throw new DAOException(e);
        }
    }

    
    public List<Event> listFilterNormalEvents(int numPagina,String txtToFilt) throws DAOException {
    	if (txtToFilt.length() < 2) {
        	
            throw new IllegalArgumentException("Min 2 characters");
        }
        try (final Connection conn = this.getConnection()) {

            final String query = "SELECT * FROM events WHERE day>=? and important=FALSE and (LOWER(name) like LOWER(?) or LOWER(description) like LOWER(?)) ORDER BY day limit ?,9";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                statement.setTimestamp(1, timestamp);
                statement.setString(2,"%"+txtToFilt+"%");
                statement.setString(3,"%"+txtToFilt+"%");
                statement.setInt(4, numPagina);
                try (final ResultSet result = statement.executeQuery()) {

                    final List<Event> eventsName = new LinkedList<>();
                    final List<Event> eventsDescription = new LinkedList<>();
                    while (result.next()) {
                        Event e =rowToEntity(result);
                        if(e.getName().toLowerCase().contains(txtToFilt.toLowerCase())){
                            eventsName.add(e);
                        }else{
                            eventsDescription.add(e);
                        }
                    }

                   eventsName.addAll(eventsDescription);
                    return eventsName;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing events", e);
            throw new DAOException(e);
        }
    }
    
    
    
    public List<Event> listImportantEvents() throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT * FROM events WHERE day>=? and important=TRUE ORDER BY day";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
            	
            	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            	statement.setTimestamp(1, timestamp);
            	
                try (final ResultSet result = statement.executeQuery()) {
                	                	
                    final List<Event> events = new LinkedList<>();
                    
                    while (result.next()) {
                        events.add(rowToEntity(result));
                    }

                    return events;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listing events", e);
            throw new DAOException(e);
        }
    }
    
    

    private Event rowToEntity(ResultSet row) throws SQLException {
    	byte[] imagenBytes = row.getBytes("image");
    	String image64 = Base64.getEncoder().encodeToString(imagenBytes);
        return new Event(
                row.getInt("id"),
                row.getString("name"),
                row.getString("description"),
                row.getString("location"),
                row.getTimestamp("day"),
                row.getString("tag"),
                row.getInt("capacity"),
                row.getBoolean("important"),
                image64,
                row.getInt("organizer"));
    }
    
    public int numNormalEvents() throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT count(id) FROM events WHERE day>=? and important=FALSE";
            int numEvents=0;
            try (final PreparedStatement statement = conn.prepareStatement(query)) {

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                statement.setTimestamp(1, timestamp);
                try (final ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        numEvents=result.getInt(1);
                    }

                    return numEvents;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error counting num events", e);
            throw new DAOException(e);
        }
    }
    
    public int numFilterNormalEvents(String txtToFilt) throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT count(id) FROM events WHERE day>=? and important=FALSE and (LOWER(name) like LOWER(?) or LOWER(description) like LOWER(?))";
            int numEvents=0;
            try (final PreparedStatement statement = conn.prepareStatement(query)) {

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                statement.setTimestamp(1, timestamp);
                statement.setString(2,"%"+txtToFilt+"%");
                statement.setString(3,"%"+txtToFilt+"%");
                try (final ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        numEvents=result.getInt(1);
                    }

                    return numEvents;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error counting num events", e);
            throw new DAOException(e);
        }
    }
    
    public void assistToEvent(int userId, int eventId) throws DAOException, IllegalArgumentException{

    	if(eventId <= 0 || userId <= 0) {
    		throw new IllegalArgumentException("Ambos ids deben de ser mayores que 0");
    	}
    	try(final Connection conn = this.getConnection()){
    		final String query = "INSERT INTO eventuser VALUES(?,?)";
    		try(final PreparedStatement statement = conn.prepareStatement(query)){
    			statement.setInt(1, userId);
    			statement.setInt(2, eventId);
    			if (statement.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Invalid id");
                }
    		}
    	}catch(SQLException e) {
    		LOG.log(Level.SEVERE, "Error confirming assistance to an event", e);
    		throw new DAOException(e);
    	}
    	
    }
    
    public int isAssistantOfEvent(int userId, int eventId) throws DAOException, IllegalArgumentException{
        if(eventId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Ambos ids deben de ser mayores que 0");
        }
        try(final Connection conn = this.getConnection()){
            int numMatches=0;
            final String query = "SELECT * FROM eventuser where userid=? and eventid=?";
            try(final PreparedStatement statement = conn.prepareStatement(query)){
                statement.setInt(1, userId);
                statement.setInt(2, eventId);
                   try (final ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        numMatches++;
                    }

                    return numMatches;
                }
            }
        }catch(SQLException e) {
            LOG.log(Level.SEVERE, "Error confirming assistance to an event", e);
            throw new DAOException(e);
        }

    }
    
    public void deleteUserFromEvent(int userId, int eventId) throws DAOException, IllegalArgumentException{

    	if(eventId <= 0 || userId <= 0) {
    		throw new IllegalArgumentException("Ambos ids deben de ser mayores que 0");
    	}

    	try(final Connection conn = this.getConnection()){
    		final String query = "DELETE FROM eventuser WHERE userid = ? AND eventid = ?";
    		
    		try(final PreparedStatement statement = conn.prepareStatement(query)){
    			statement.setInt(1, userId);
    			statement.setInt(2, eventId);

    			if (statement.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Invalid id");
                }
    		}
    	}catch(SQLException e) {
    		
    		LOG.log(Level.SEVERE, "Error unsigning to an event", e);
    		throw new DAOException(e);
    	}
    	
    }
    
    public Event add(String name, String description, String location,java.sql.Timestamp day, String tag,int capacity,Boolean important, String image, int organizer_id)
            throws DAOException, IllegalArgumentException, SerialException, SQLException {
    	if (name == null || description == null || location == null || day == null || tag == null || capacity<=0 || capacity>20 || important == null || organizer_id == 0) {
        	throw new IllegalArgumentException("name, location, day, tag can't be null; capacity can't be <=0 or >20; organizer_id can't be ==0");
        }

        byte[] decoded = Base64.getDecoder().decode(image);
        Blob blob = new javax.sql.rowset.serial.SerialBlob(decoded);
        try (Connection conn = this.getConnection()) {
        	Date date = new Date(day.getTime());
            Calendar calendar = Calendar.getInstance();
            Calendar calendar1 = Calendar.getInstance();
            calendar.setTime(date);
            calendar1.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY,1);
            calendar1.add(Calendar.HOUR_OF_DAY,1);
            java.sql.Timestamp day_plus_hour = new java.sql.Timestamp(calendar.getTimeInMillis());
            java.sql.Timestamp day_minus_hour = new java.sql.Timestamp(calendar1.getTimeInMillis());
            String query_check = "SELECT * FROM events WHERE location=? and (day=? or day=? or day=?)";
        	int cont=0;
            try (final PreparedStatement statement_check = conn.prepareStatement(query_check)) {
            	 statement_check.setString(1,location);
            	 statement_check.setTimestamp(2,day);
            	 statement_check.setTimestamp(3,day_plus_hour);
            	 statement_check.setTimestamp(4,day_minus_hour);
                try (final ResultSet result = statement_check.executeQuery()) {
                    while ((result.next())&&(cont==0)) {
                        cont++;
                    }
                }
            }
        	if(cont==0) {
            final String query = "INSERT INTO events VALUES(null,?,?,?,?,?,?,?,?,?)";

            try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, name);
                statement.setString(3, location);
                statement.setString(2, description);
                statement.setTimestamp(4,day);
                statement.setString(5, tag);
                statement.setInt(6, capacity);
                statement.setBoolean(7, important);
                statement.setBlob(8, blob);
                statement.setInt(9, organizer_id);
                if (statement.executeUpdate() == 1) {
                    try (ResultSet resultKeys = statement.getGeneratedKeys()) {
                        if (resultKeys.next()) {
                            return new Event(resultKeys.getInt(1), name, description, location,day,tag,capacity,important, image, organizer_id);
                        } else {
                            LOG.log(Level.SEVERE, "Error retrieving inserted id");
                            throw new SQLException("Error retrieving inserted id");
                        }
                    }
                } else {
                    LOG.log(Level.SEVERE, "Error inserting value");
                    throw new SQLException("Error inserting value");
                }
            }
            
        	}else{
                   LOG.log(Level.SEVERE, "Error retrieving inserted id");
                   throw new SQLException("Error retrieving inserted id"); 
                }
            
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error adding an ebvent", e);
            throw new DAOException(e);
        }
    }

    
    public void delete(int event_id, int organizer_id)throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection()) {
            final String query = "DELETE FROM events WHERE organizer = ? AND id=?";
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
            	statement.setInt(1, organizer_id);    
            	statement.setInt(2, event_id);

                    if (statement.executeUpdate() != 1) {
                        throw new IllegalArgumentException("Invalid event_id or organizer_id");
                    }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting a Event", e);
            throw new DAOException(e);
        }
    }
    
    
    public int numUsersInEvent(int id_event) throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT count(eventid) FROM eventuser WHERE eventid=?";
            int numUsersInEvent=0;
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, id_event);

                try (final ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        numUsersInEvent=result.getInt(1);
                    }

                    return numUsersInEvent;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error counting num users in event", e);
            throw new DAOException(e);
        }
    }
    

    
    


}
