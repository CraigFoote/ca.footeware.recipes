/**
 * 
 */
package ca.footeware.recipes.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author craig
 *
 */
@Entity
public class Recipe implements Comparable<Recipe> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String body;
	private String images;
	private String tags;

	public Recipe() {
	}

	public Recipe(String name, String body, String images, String tags) {
		this.name = name;
		this.body = body;
		this.images = images;
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public int getId() {
		return id;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public int compareTo(Recipe o) {
		return name.compareTo(o.getName());
	}
}
