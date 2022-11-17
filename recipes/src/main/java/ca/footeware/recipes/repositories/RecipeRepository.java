/**
 * 
 */
package ca.footeware.recipes.repositories;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import ca.footeware.recipes.models.Recipe;

/**
 * @author Footeware.ca
 *
 */
public interface RecipeRepository extends CrudRepository<Recipe, Integer> {

	/**
	 * Find all recipes with tag matching provided search term.
	 * 
	 * @param term {@link String}
	 * @return {@link Set} of {@link Recipe}
	 */
	Set<Recipe> findByTagsContainingIgnoreCase(String term);

	/**
	 * Find all recipes with name matching provided search term.
	 * 
	 * @param term {@link String}
	 * @return {@link Set} of {@link Recipe}
	 */
	Set<Recipe> findByNameContainingIgnoreCase(String term);

}
