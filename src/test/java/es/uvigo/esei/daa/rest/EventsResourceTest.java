package es.uvigo.esei.daa.rest;

import static es.uvigo.esei.daa.dataset.EventsDataset.existentId;
import static es.uvigo.esei.daa.dataset.EventsDataset.existentEvent;
import static es.uvigo.esei.daa.dataset.EventsDataset.newName;
import static es.uvigo.esei.daa.dataset.EventsDataset.newDescription;
import static es.uvigo.esei.daa.dataset.EventsDataset.newOrganizer;
import static es.uvigo.esei.daa.dataset.EventsDataset.newImage;
import static es.uvigo.esei.daa.dataset.EventsDataset.newEvent;
import static es.uvigo.esei.daa.dataset.EventsDataset.newLocation;
import static es.uvigo.esei.daa.dataset.EventsDataset.newDay;
import static es.uvigo.esei.daa.dataset.EventsDataset.newTag;
import static es.uvigo.esei.daa.dataset.EventsDataset.newCapacity;
import static es.uvigo.esei.daa.dataset.EventsDataset.newImportant;
import static es.uvigo.esei.daa.dataset.EventsDataset.nonExistentId;
import static es.uvigo.esei.daa.dataset.EventsDataset.eventsImportants;
import static es.uvigo.esei.daa.dataset.EventsDataset.eventsNormals;
import static es.uvigo.esei.daa.dataset.EventsDataset.eventsFilterNormalPag1;
import static es.uvigo.esei.daa.dataset.EventsDataset.eventsFilterNormalPag2;
import static es.uvigo.esei.daa.dataset.EventsDataset.eventsNormalsPag2;
import static es.uvigo.esei.daa.dataset.EventsDataset.EventsNoExistentPage;
import static es.uvigo.esei.daa.dataset.EventsDataset.toSearch;
import static es.uvigo.esei.daa.dataset.EventsDataset.toInvalidSearch;
import static es.uvigo.esei.daa.dataset.EventsDataset.numberNormalEvents;
import static es.uvigo.esei.daa.dataset.EventsDataset.numberNormalFilterEvents; 
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasInternalServerErrorStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasBadRequestStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasOkStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasNoContent;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasUnauthorized;
import static es.uvigo.esei.daa.matchers.IsEqualToEvent.containsEventsInAnyOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToEvent.containsEventsInOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToEvent.equalsToEvent;
import static javax.ws.rs.client.Entity.entity;
import es.uvigo.esei.daa.entities.Event;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
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
import es.uvigo.esei.daa.entities.Event;
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
public class EventsResourceTest extends JerseyTest {
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
	public void testListImportantEvents() throws IOException {
		final Response response = target("event").queryParam("type", "important").request(MediaType.APPLICATION_JSON_TYPE).get();

		assertThat(response, hasOkStatus());

		final List<Event> events = response.readEntity(new GenericType<List<Event>>(){});
		
		assertThat(events, containsEventsInOrder(eventsImportants()));
	}
	

	@Test
	public void testListNormalEvents() throws IOException {
		final Response response = target("event").queryParam("type", "normal").queryParam("pag", 9).request().get();
		assertThat(response, hasOkStatus());

		final List<Event> events = response.readEntity(new GenericType<List<Event>>(){});
		assertThat(events, containsEventsInOrder(eventsNormalsPag2()));
	}
	
	@Test
	public void testListNormalEventsNoExistentPage() throws IOException {
		final Response response = target("event").queryParam("type", "normal").queryParam("pag", 81).request().get();
		assertThat(response, hasOkStatus());

		final List<Event> events = response.readEntity(new GenericType<List<Event>>(){});
		
		assertThat(events, containsEventsInAnyOrder(EventsNoExistentPage()));
	}
	
	@Test
    public void testListNormalEventsMissingPag() {
            final Response response = target("event/").queryParam("type", "normal").request().get();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	
	@Test
    public void testNumNormalEvents() {
            final Response response = target("event/").queryParam("type", "numNormal").request().get();
            assertThat(response, hasOkStatus());
            final Integer numberEvents = response.readEntity(Integer.class);
            assertThat(numberEvents, is(equalTo(numberNormalEvents())));
    }
	
	@Test 
    public void testNumNormalFilterEvents() { 
            final Response response = target("event/").queryParam("type", "numFilterNormal").queryParam("txtToFilt","quedadita").request().get();
            assertThat(response, hasOkStatus());
            
            final Integer numberEvents = response.readEntity(Integer.class);
            assertThat(numberEvents, is(equalTo(numberNormalFilterEvents())));
    }
	
	
	@Test
    public void testNumUsersInEvent() {
            final Response response = target("event/").queryParam("type", "numUsersInEvent").queryParam("eventID",1).request().get();
            assertThat(response, hasOkStatus());
            final Integer numberEvents = response.readEntity(Integer.class);
            assertThat(numberEvents, is(equalTo(2)));
    }
	
	@Test
    public void testNumUsersInEventMissingEventID() {
            final Response response = target("event/").queryParam("type", "numUsersInEvent").request().get();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	
	@Test
    public void testInvalidTypeParamGetHandler() {
            final Response response = target("event/").queryParam("type", "inventado").request().get();
            assertThat(response, hasNoContent());
    }
	
	
	@Test
    public void testInvalidTypeParamPostHandler() {
		final Form form = new Form();
		form.param("description", newDescription());
		form.param("location", newLocation());
		form.param("day", newDay().toString());
		form.param("tag", newTag());
		form.param("capacity", Integer.toString(newCapacity()));
		form.param("important", Boolean.toString(newImportant()));
		form.param("image", newImage());
		form.param("organizer_id", Integer.toString(newOrganizer()));
		
		final Response response = target("event/").queryParam("type", "inventado").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
            assertThat(response, hasNoContent());
    }
	
	@Test
    public void testInvalidTypeParamDeleteHandler() {
            final Response response = target("event/").queryParam("type", "inventado").request().delete();
            assertThat(response, hasNoContent());
    }
	
	
	@Test
    public void testIsAssistantOfEvent() {
            final Response response = target("event/").queryParam("type", "isAssistantOfEvent").queryParam("eventID",1).queryParam("userID",1).request().get();
            assertThat(response, hasOkStatus());
            final Integer numberEvents = response.readEntity(Integer.class);
            assertThat(numberEvents, is(equalTo(1)));
    }
	
	@Test
    public void testIsNotAssistantOfEvent() {
            final Response response = target("event/").queryParam("type", "isAssistantOfEvent").queryParam("eventID",155).queryParam("userID",1).request().get();
            assertThat(response, hasOkStatus());
            final Integer numberEvents = response.readEntity(Integer.class);
            assertThat(numberEvents, is(equalTo(0)));
    }
	
	@Test
    public void testIsAssistantOfEventMissingEventID() {
            final Response response = target("event/").queryParam("type", "isAssistantOfEvent").queryParam("userID",1).request().get();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	@Test
    public void testIsAssistantOfEventMissingUserID() {
            final Response response = target("event/").queryParam("type", "isAssistantOfEvent").queryParam("eventID",1).request().get();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	
	@Test
	public void testFilterNormalEventsPag1() throws IOException {
		final Response response = target("event").queryParam("type", "filterNormal").queryParam("pag", 0).queryParam("txtToFilt", toSearch()).request().get();
		assertThat(response, hasOkStatus());
		
		final List<Event> events = response.readEntity(new GenericType<List<Event>>(){});
		
		assertThat(events, containsEventsInOrder(eventsFilterNormalPag1()));

	}
	
	@Test
	public void testFilterNormalEventsPag2() throws IOException {
		final Response response = target("event").queryParam("type", "filterNormal").queryParam("pag", 9).queryParam("txtToFilt", toSearch()).request().get();
		assertThat(response, hasOkStatus());
		
		final List<Event> events = response.readEntity(new GenericType<List<Event>>(){});
		
		assertThat(events, containsEventsInOrder(eventsFilterNormalPag2()));
	}
	
	@Test
	public void testFilterInvalid() throws IOException {
		final Response response = target("event").queryParam("type", "filterNormal").queryParam("pag", 0).queryParam("txtToFilt", toInvalidSearch()).request().get();
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
    public void testFilterNormalEventsMissingPag() {
            final Response response = target("event/").queryParam("type", "filterNormal").queryParam("txtToFilt", toSearch()).request().get();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	@Test
    public void testFilterNormalEventsMissingTxtToFilt() {
            final Response response = target("event/").queryParam("type", "filterNormal").queryParam("pag", 0).request().get();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	@Test
	@ExpectedDatabase("/datasets/dataset-delete.xml")
	public void testDelete() throws IOException {
		final Response response = target("event").queryParam("type", "delete").queryParam("eventID", existentId()).queryParam("userID", "1").request().delete();
		
		assertThat(response, hasOkStatus());
		
		final Integer deletedId = response.readEntity(Integer.class);
		
		assertThat(deletedId, is(equalTo(existentId())));
	}
	
	@Test
	public void testDeleteInvalidId() throws IOException {
		final Response response = target("event/").queryParam("type", "delete").queryParam("eventID", nonExistentId()).queryParam("userID", "1").request().delete();

		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testDeleteInvalidOrganizer() throws IOException {
		final Response response = target("event/").queryParam("type", "delete").queryParam("eventID", existentId()).queryParam("userID", "5").request().delete();

		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
    public void testDeleteMissingUserID() {
            final Response response = target("event/").queryParam("type", "delete").queryParam("eventID",existentId()).request().delete();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	@Test
    public void testDeleteMissingEventID() {
            final Response response = target("event/").queryParam("type", "delete").queryParam("userID","5").request().delete();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	
	@Test
    @ExpectedDatabase("/datasets/dataset-add.xml")
    public void testAdd() throws IOException {
        final Form form = new Form();
        form.param("name", newName());
        form.param("description", newDescription());
        form.param("location", newLocation());
        form.param("day", newDay().toString());
        form.param("tag", newTag());
        form.param("capacity", Integer.toString(newCapacity()));
        form.param("important", Boolean.toString(newImportant()));
        form.param("image", newImage());
        form.param("organizer_id", Integer.toString(newOrganizer()));

        final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(response, hasOkStatus());

        final Event event = response.readEntity(Event.class);
        assertThat(event, is(equalsToEvent(newEvent())));
    }
	

	@Test
	public void testAddMissingName() throws IOException {
		final Form form = new Form();
		form.param("description", newDescription());
		form.param("location", newLocation());
		form.param("day", newDay().toString());
		form.param("tag", newTag());
		form.param("capacity", Integer.toString(newCapacity()));
		form.param("important", Boolean.toString(newImportant()));
		form.param("image", newImage());
		form.param("organizer_id", Integer.toString(newOrganizer()));
		
		final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}
	
	
	
	@Test
	public void testAddMissingDescription() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("location", newLocation());
		form.param("day", newDay().toString());
		form.param("tag", newTag());
		form.param("capacity", Integer.toString(newCapacity()));
		form.param("important", Boolean.toString(newImportant()));
		form.param("image", newImage());
		form.param("organizer_id", Integer.toString(newOrganizer()));
		
		final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testAddMissingLocation() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("description", newDescription());
		form.param("day", newDay().toString());
		form.param("tag", newTag());
		form.param("capacity", Integer.toString(newCapacity()));
		form.param("important", Boolean.toString(newImportant()));
		form.param("image", newImage());
		form.param("organizer_id", Integer.toString(newOrganizer()));
		
		final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testAddMissingDay() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("description", newDescription());
		form.param("location", newLocation());
		form.param("tag", newTag());
		form.param("capacity", Integer.toString(newCapacity()));
		form.param("important", Boolean.toString(newImportant()));
		form.param("image", newImage());
		form.param("organizer_id", Integer.toString(newOrganizer()));
		
		final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testAddMissingTag() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("description", newDescription());
		form.param("location", newLocation());
		form.param("day", newDay().toString());
		form.param("capacity", Integer.toString(newCapacity()));
		form.param("important", Boolean.toString(newImportant()));
		form.param("image", newImage());
		form.param("organizer_id", Integer.toString(newOrganizer()));
		
		final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testAddMissingCapacity() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("description", newDescription());
		form.param("location", newLocation());
		form.param("day", newDay().toString());
		form.param("tag", newTag());
		form.param("important", Boolean.toString(newImportant()));
		form.param("image", newImage());
		form.param("organizer_id", Integer.toString(newOrganizer()));
		
		final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testAddMissingImportant() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("description", newDescription());
		form.param("location", newLocation());
		form.param("day", newDay().toString());
		form.param("capacity", Integer.toString(newCapacity()));
		form.param("tag", newTag());
		form.param("image", newImage());
		form.param("organizer_id", Integer.toString(newOrganizer()));
		
		final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testAddMissingOrganizer() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("description", newDescription());
		form.param("location", newLocation());
		form.param("day", newDay().toString());
		form.param("capacity", Integer.toString(newCapacity()));
		form.param("tag", newTag());
		form.param("important", Boolean.toString(newImportant()));
		form.param("image", newImage());

		final Response response = target("event/").queryParam("type", "add").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}
	
	
	@Test
	@ExpectedDatabase("/datasets/dataset-deleteEventUser.xml")
	public void testUnassistToEvent() throws IOException {
		final Response response = target("event/").queryParam("type", "unassistToEvent").queryParam("eventID", "4").queryParam("userID", "2").request().delete();
		
		assertThat(response, hasOkStatus());
		
		final Integer deletedId = response.readEntity(Integer.class);
		
		assertThat(deletedId, is(equalTo(4)));
	}
	
	
	
	@Test
	public void testUnassistToEventInvalidEventID() throws IOException {
		final Response response = target("event/").queryParam("type", "unassistToEvent").queryParam("eventID", nonExistentId()).queryParam("userID", "1").request().delete();

		assertThat(response, hasBadRequestStatus());
	}
	
	
	@Test
	public void testUnassistToEventInvalidUserID() throws IOException {
		final Response response = target("event/").queryParam("type", "unassistToEvent").queryParam("eventID", existentId()).queryParam("userID", nonExistentId()).request().delete();

		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
    public void testUnassistToEventMissingUserID() {
            final Response response = target("event/").queryParam("type", "unassistToEvent").queryParam("eventID",existentId()).request().delete();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	@Test
    public void testUnassistToEventMissingEventID() {
            final Response response = target("event/").queryParam("type", "unassistToEvent").queryParam("userID","5").request().delete();
            assertThat(response, hasInternalServerErrorStatus());
    }
	
	
	@Test
    @ExpectedDatabase("/datasets/dataset-addEventUser.xml")
    public void testAssistToEvent() throws IOException {
        final Form form = new Form();
        form.param("eventID", "1");
        form.param("userID", "3");

        final Response response = target("event/").queryParam("type", "assistToEvent").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(response, hasOkStatus());

        final Integer addId = response.readEntity(Integer.class);
		assertThat(addId, is(equalTo(1)));
    }
	
	@Test
    public void testAssistToEventMissingEventID() throws IOException {
        final Form form = new Form();
        form.param("userID", "3");

        final Response response = target("event/").queryParam("type", "assistToEvent").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(response, hasBadRequestStatus());
    }
	
	@Test
    public void testAssistToEventMissingUserID() throws IOException {
        final Form form = new Form();
        form.param("eventID", "1");

        final Response response = target("event/").queryParam("type", "assistToEvent").request(MediaType.APPLICATION_JSON_TYPE).post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(response, hasBadRequestStatus());
    }
	
	
}