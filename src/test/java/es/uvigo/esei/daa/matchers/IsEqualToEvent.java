package es.uvigo.esei.daa.matchers;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.esei.daa.entities.Event;

public class IsEqualToEvent extends IsEqualToEntity<Event> {
	public IsEqualToEvent(Event entity) {
		super(entity);
	}

	@Override
	protected boolean matchesSafely(Event actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("id", Event::getId, actual)
				&& checkAttribute("name", Event::getName, actual)
				&& checkAttribute("description", Event::getDescription, actual)
				&& checkAttribute("location", Event::getLocation, actual)
				&& checkAttribute("day", Event::getDay, actual)
				&& checkAttribute("tag", Event::getTag, actual)
				&& checkAttribute("capacity", Event::getCapacity, actual)
				&& checkAttribute("important", Event::getImportant, actual)
				&& checkAttribute("image", Event::getImage, actual)
				&& checkAttribute("organizer", Event::getOrganizer, actual);
		}
	}

	/**
	 * Factory method that creates a new {@link IsEqualToEntity} matcher with
	 * the provided {@link Person} as the expected value.
	 * 
	 * @param person the expected person.
	 * @return a new {@link IsEqualToEntity} matcher with the provided
	 * {@link Person} as the expected value.
	 */
	@Factory
	public static IsEqualToEvent equalsToEvent(Event event) {
		return new IsEqualToEvent(event);
	}
	
	/**
	 * Factory method that returns a new {@link Matcher} that includes several
	 * {@link IsEqualToPerson} matchers, each one using an {@link Person} of the
	 * provided ones as the expected value.
	 * 
	 * @param persons the persons to be used as the expected values.
	 * @return a new {@link Matcher} that includes several
	 * {@link IsEqualToPerson} matchers, each one using an {@link Person} of the
	 * provided ones as the expected value.
	 * @see IsEqualToEntity#containsEntityInAnyOrder(java.util.function.Function, Object...)
	 */
	@Factory
	public static Matcher<Iterable<? extends Event>> containsEventsInAnyOrder(Event ... events) {
		return containsEntityInAnyOrder(IsEqualToEvent::equalsToEvent, events);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Event>> containsEventsInOrder(Event ... events) {
		return containsEntityInOrder(IsEqualToEvent::equalsToEvent, events);
	}

}