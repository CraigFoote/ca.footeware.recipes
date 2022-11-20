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
 * @author Footeware.ca
 */
@Service
public class RecipeService {

	@Autowired
	private RecipeRepository repository;

	/**
	 * @param id {@link Integer}
	 */
	public void delete(Integer id) {
		repository.deleteById(id);
	}

	/**
	 * @return {@link Iterable} of {@link Recipe}
	 */
	public Iterable<Recipe> findAll() {
		return repository.findAll();
	}

	/**
	 * @return {@link Set} of {@link Recipe}
	 */
	public Set<Recipe> findByName(String term) {
		return repository.findByNameContainingIgnoreCase(term);
	}

	/**
	 * @return {@link Set} of {@link Recipe}
	 */
	public Set<Recipe> findByTag(String term) {
		return repository.findByTagsContainingIgnoreCase(term);
	}

	/**
	 * @param id {@link Integer}
	 */
	public Recipe get(Integer id) {
		if (repository.findById(id).isPresent()) {
			return repository.findById(id).get();
		}
		throw new IllegalArgumentException("No recipe found with id=" + id);
	}

	/**
	 * @param recipe {@link Recipe}
	 */
	public Recipe save(Recipe recipe) {
		return repository.save(recipe);
	}

	/**
	 * @param recipe {@link Recipe}
	 * @return {@link Recipe}
	 */
	public Recipe update(Recipe recipe) {
		return repository.save(recipe);
	}
}
