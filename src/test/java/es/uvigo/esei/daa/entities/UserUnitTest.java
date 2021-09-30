package es.uvigo.esei.daa.entities;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class UserUnitTest {
	@Test
	public void testUserIntStringString() {
		final int id = 1;
		final String login = "user";
		final String password = "pass";
		
		final User person = new User(id, login, password);
		
		assertThat(person.getId(), is(equalTo(id)));
		assertThat(person.getLogin(), is(equalTo(login)));
		assertThat(person.getPassword(), is(equalTo(password)));
	}

	@Test(expected = NullPointerException.class)
	public void testUserIntStringStringNullLogin() {
            User user = new User(1, null, "pass");
	}
	
	@Test(expected = NullPointerException.class)
	public void testPersonIntStringStringNullPassword() {
		User user = new User(1, "user", null);
	}

	@Test
	public void testSetLogin() {
		final int id = 1;
		final String password = "pass";
		
		final User user = new User(id, "User", password);
		user.setLogin("user111");
		
		assertThat(user.getId(), is(equalTo(id)));
		assertThat(user.getLogin(), is(equalTo("user111")));
		assertThat(user.getPassword(), is(equalTo(password)));
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullLogin() {
		final User person = new User(1, "User", "Pass");
		
		person.setLogin(null);
	}

	@Test
	public void testSetPassword() {
		final int id = 1;
		final String login = "user";
		
		final User user = new User(id, login, "pass");
		user.setPassword("any");
		
		assertThat(user.getId(), is(equalTo(id)));
		assertThat(user.getLogin(), is(equalTo(login)));
		assertThat(user.getPassword(), is(equalTo("any")));
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullPassword() {
		final User user = new User(1, "user", "pass");
		
		user.setPassword(null);
	}

	@Test
	public void testEqualsObject() {
		final User userA = new User(1, "user A", "password A");
		final User userB = new User(1, "user B", "password B");
		
		assertTrue(userA.equals(userB));
	}

	@Test
	public void testEqualsHashcode() {
		EqualsVerifier.forClass(User.class)
			.withIgnoredFields("login", "password")
			.suppress(Warning.STRICT_INHERITANCE)
			.suppress(Warning.NONFINAL_FIELDS)
		.verify();
	}
}
