/**
 *
 */
package ca.footeware.recipes.models;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * A cooking recipe.
 *
 * @author Footeware.ca
 */
@Entity
public class Recipe implements Comparable<Recipe> {

	private String body;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String images;
	private String name;
	private String tags;

	public Recipe() {
	}

	public Recipe(String name, String body, String images, String tags) {
		this.name = name;
		this.body = body;
		this.images = images;
		this.tags = tags;
	}

	@Override
	public int compareTo(Recipe o) {
		return name.compareTo(o.getName());
	}

	public String getBody() {
		return body;
	}

	public int getId() {
		return id;
	}

	public String getImages() {
		return images;
	}

	public String getName() {
		return name;
	}

	public String getTags() {
		return tags;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public int hashCode() {
		return Objects.hash(body, id, images, name, tags);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recipe other = (Recipe) obj;
		return Objects.equals(body, other.body) && id == other.id && Objects.equals(images, other.images)
				&& Objects.equals(name, other.name) && Objects.equals(tags, other.tags);
	}
}
