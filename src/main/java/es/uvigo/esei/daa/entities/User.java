package es.uvigo.esei.daa.entities;

import static java.util.Objects.requireNonNull;


/**
    * This class represent an user
    * @param id the id if the user
	* @param password password of the user encoded using SHA-256 and with the
	* "salt" prefix added.
*/


public class User {
    private int id;
    private String login;
    private String password;

    User() {
    }

    public User(int id,String login, String password) {
        this.id=id;
        this.setLogin(login);
        this.setPassword(password);
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = requireNonNull(login, "Login can't be null");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = requireNonNull(password, "Password can't be null");
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getId();
		return result;
	}
    
    @Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (this.getId() != other.getId())
			return false;
		return true;
	}

}