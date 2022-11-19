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
	 * @param id
	 */
	public void delete(Integer id) {
		repository.deleteById(id);
	}

	/**
	 * @return
	 */
	public Iterable<Recipe> findAll() {
		return repository.findAll();
	}

	public Set<Recipe> findByName(String term) {
		return repository.findByNameContainingIgnoreCase(term);
	}

	public Set<Recipe> findByTag(String term) {
		return repository.findByTagsContainingIgnoreCase(term);
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

	/**
	 * @param recipe
	 */
	public Recipe save(Recipe recipe) {
		return repository.save(recipe);
	}

	public Recipe update(Recipe recipe) {
		return repository.save(recipe);
	}

}
