package es.uvigo.esei.daa.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.daa.entities.Event;
import es.uvigo.esei.daa.entities.User;
import java.util.Base64;

/**
 * 
 * DAO class for the {@link Users} entities.
 *
 */
public class UsersDAO extends DAO {
	private final static Logger LOG = Logger.getLogger(UsersDAO.class.getName());

	public User get(String login,String password) throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM users WHERE login=?";
			byte[] decodedBytes = Base64.getDecoder().decode(password);
            String base64Password =new String(decodedBytes);
			final String shaPassword = encodeSha256(base64Password);
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, login);
				
				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
                    if(result.getString(3).equals(shaPassword)){
						return rowToEntity(result);
                    }
                    else {
						throw new IllegalArgumentException("Invalid password");
						
					}
					} else {
						throw new IllegalArgumentException("Invalid id");
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error checking login", e);
			throw new DAOException(e);
		}
	}
	
	private final static String encodeSha256(String text) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] digested = digest.digest(text.getBytes());
			
			return hexToString(digested);
		} catch (NoSuchAlgorithmException e) {
			LOG.log(Level.SEVERE, "SHA-256 not supported", e);
			throw new RuntimeException(e);
		}
	}
	
	private final static String hexToString(byte[] hex) {
		final StringBuilder sb = new StringBuilder();
		
		for (byte b : hex) {
			sb.append(String.format("%02x", b & 0xff));
		}
		
		return sb.toString();
	}

	private User rowToEntity(ResultSet result) throws SQLException {
		return new User(
             result.getInt("id"),
			result.getString("login"),
			result.getString("password")
		);
	}

}
