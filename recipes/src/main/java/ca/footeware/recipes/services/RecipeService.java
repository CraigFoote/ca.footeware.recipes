/**
 * 
 */
package ca.footeware.recipes.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.footeware.recipes.models.Recipe;
import ca.footeware.recipes.repositories.RecipeRepository;

/**
 * @author craig
 *
 */
@Service
public class RecipeService {

	@Autowired
	private RecipeRepository repository;

	/**
	 * @param recipe
	 */
	public Recipe save(Recipe recipe) {
		return repository.save(recipe);
	}

	public Set<Recipe> findByTag(String term) {
		return repository.findByTagsContainingIgnoreCase(term);
	}

	public Set<Recipe> findByName(String term) {
		return repository.findByNameContainingIgnoreCase(term);
	}

	/**
	 * @param id
	 */
	public Recipe get(Integer id) {
		if (repository.findById(id).isPresent()) {
			return repository.findById(id).get();
		}
		throw new IllegalArgumentException("No recipe found with id=" + id);
	}

}
