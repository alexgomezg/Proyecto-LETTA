package es.uvigo.esei.daa.rest;

import static es.uvigo.esei.daa.dataset.UsersDataset.existentId;
import static es.uvigo.esei.daa.dataset.UsersDataset.existentUser;
import static es.uvigo.esei.daa.dataset.UsersDataset.newLogin;
import static es.uvigo.esei.daa.dataset.UsersDataset.newPassword;
import static es.uvigo.esei.daa.dataset.UsersDataset.nonExistentId;
import static es.uvigo.esei.daa.dataset.UsersDataset.users;
import static es.uvigo.esei.daa.dataset.UsersDataset.user;
//import static es.uvigo.esei.daa.dataset.UsersDataset.adminLogin;
//import static es.uvigo.esei.daa.dataset.UsersDataset.normalLogin;
//import static es.uvigo.esei.daa.dataset.UsersDataset.userToken;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasBadRequestStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasOkStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasUnauthorized;
import static es.uvigo.esei.daa.matchers.IsEqualToUser.containsUsersInAnyOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToUser.equalsToUser;
import static javax.ws.rs.client.Entity.entity;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
//import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import es.uvigo.esei.daa.LETTATestApplication;
import es.uvigo.esei.daa.entities.User;
import es.uvigo.esei.daa.listeners.ApplicationContextBinding;
import es.uvigo.esei.daa.listeners.ApplicationContextJndiBindingTestExecutionListener;
import es.uvigo.esei.daa.listeners.DbManagement;
import es.uvigo.esei.daa.listeners.DbManagementTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:contexts/mem-context.xml")
@TestExecutionListeners({
	DbUnitTestExecutionListener.class,
	DbManagementTestExecutionListener.class,
	ApplicationContextJndiBindingTestExecutionListener.class
})
@ApplicationContextBinding(
	jndiUrl = "java:/comp/env/jdbc/letta",
	type = DataSource.class
)
@DbManagement(
	create = "classpath:db/hsqldb.sql",
	drop = "classpath:db/hsqldb-drop.sql"
)
@DatabaseSetup("/datasets/dataset.xml")
@ExpectedDatabase("/datasets/dataset.xml")
public class UsersResourceTest extends JerseyTest {
	@Override
	protected Application configure() {
		return new LETTATestApplication();
	}

	@Override
	protected void configureClient(ClientConfig config) {
		super.configureClient(config);
		
		// Enables JSON transformation in client
		config.register(JacksonJsonProvider.class);
		config.property("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
	}

	
	@Test
	public void testGet() throws IOException {
		String pass = "password5";
		String encodedPassword = Base64.getEncoder().encodeToString(pass.getBytes());
		
        final Response response = target("users").queryParam("login", "login5").queryParam("password", encodedPassword).request().get();
		assertThat(response, hasOkStatus());
		final User user = existentUser();
		assertThat(user, is(equalsToUser(existentUser())));
	}

	@Test
	public void testGetInvalidId() throws IOException {
		final Response response = target("users").queryParam("login", "login7").queryParam("password", "password7").request().get();
		
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testGetInvalidPassword() throws IOException {
		final Response response = target("users").queryParam("login", "login5").queryParam("password", "password7").request().get();
		
		assertThat(response, hasBadRequestStatus());
	}
}