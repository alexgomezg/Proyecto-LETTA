package es.uvigo.esei.daa.entities;

/**
 * This class represents an Event.
 * @param id identifier of the event.
 * @param name name of the event.
 * @param description a brief description of the event.
 * @param day the day and hour in which the event will take place.
 * @param tag the category the event is related to. 
 * @param capacity the maximum number of users that can take part in the event.
 * @param important it refers to wether the event is classified as important or not.
 * @param image a base 64 string that represents an image attached to the event.
 * @param organizer the id of the user that created the event.
 *
 */

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;

public class Event {
	private int id;
	private String name;
	private String location;
	private String description;
	private Timestamp day;
	private String tag;
	private int capacity;
	private boolean important;
	private String image;
	private int organizer;

	public Event() {}
	
	public Event(int id, String name, String description, String location, Timestamp day, String tag, int capacity, boolean important, String image, int organizer) {
		this.id = id;
		this.setName(name);
		this.setDescription(description);
		this.setLocation(location);
		this.day = day;
		this.setTag(tag);
		this.capacity = capacity;
		this.important = important;
		this.setImage(image);
		this.organizer = organizer;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = requireNonNull(name, "Name can't be null");
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = requireNonNull(description, "Description can´t be null");
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = requireNonNull(location, "Location can't be null");
	}

	public Timestamp getDay() {
		return day;
	}
	
	public void setDay(Timestamp day) {
		this.day = day;
	}

	public boolean getImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}
	
	public boolean isImportant() {
		return important;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = requireNonNull(tag, "Tag can't be null");
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public int getOrganizer() {
		return organizer;
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
		if (!(obj instanceof Event))
			return false;
		Event other = (Event) obj;
		if (this.getId() != other.getId())
			return false;
		return true;
	}
}
