/**
 *
 */
package ca.footeware.recipes.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
}
