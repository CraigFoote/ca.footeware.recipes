package ca.footeware.recipes.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import ca.footeware.recipes.models.Recipe;
import ca.footeware.recipes.services.RecipeService;

/**
 * Interacts with the recipe service.
 *
 * @author Footeware.ca
 */
@Controller
public class RecipeController {

	@Autowired
	private RecipeService recipeService;

	/**
	 * Removes extraneous characters from a String, creating ", "-delimiting.
	 *
	 * @param rawTags {@link String}
	 * @return {@link String}
	 */
	private String cleanTags(String rawTags) {
		String[] splitTags = rawTags.trim().split(",");
		List<String> tagList = Arrays.asList(splitTags);
		List<String> tagListTrimmed = new ArrayList<>();
		for (String tag : tagList) {
			String trimmed = tag.trim();
			if (!trimmed.isEmpty()) {
				tagListTrimmed.add(trimmed);
			}
		}
		StringBuilder builder = new StringBuilder();
		ListIterator<String> iter = tagListTrimmed.listIterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	/**
	 * Create a recipe.
	 *
	 * @param images             {@link String}[]
	 * @param recipeName         {@link String}[]
	 * @param recipeText         {@link String}[]
	 * @param tags               {@link String}[]
	 * @param redirectAttributes {@link RedirectAttributes}
	 * @return {@link String}
	 */
	@PostMapping("/create")
	public String createRecipe(@RequestParam(required = false) String[] images, @RequestParam String recipeName,
			@RequestParam String recipeText, @RequestParam String tags, RedirectAttributes redirectAttributes) {
		String imagesString = null;
		if (images != null) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < images.length; i++) {
				builder.append(images[i]);
				if (i < images.length) {
					builder.append("\n");
				}
			}
			imagesString = builder.toString();
		}
		String tagString = cleanTags(tags);
		Recipe recipe = new Recipe(recipeName, recipeText, imagesString, tagString);
		recipeService.save(recipe);
		redirectAttributes.addFlashAttribute("message", "Success! Create another?");
		return "redirect:/add";
	}

	/**
	 * Delete a recipe by id.
	 *
	 * @param id    {@link Integer}
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, Model model) {
		recipeService.delete(id);
		return "search";
	}

	/**
	 * Edit a recipe.
	 *
	 * @param id            {@link Integer}
	 * @param recipeName    {@link String}
	 * @param recipeText    {@link String}
	 * @param tags          {@link String}
	 * @param encodedImages {@link String}
	 * @param model         {@link Model}
	 * @return {@link String}
	 */
	@PostMapping("/edit")
	public String editRecipe(@RequestParam Integer id, @RequestParam String recipeName, @RequestParam String recipeText,
			@RequestParam String tags, String[] encodedImages, Model model) {
		Recipe recipe = recipeService.get(id);
		recipe.setName(recipeName.trim());
		recipe.setBody(recipeText);

		String tagsString = cleanTags(tags);
		recipe.setTags(tagsString);

		if (encodedImages != null && encodedImages.length != 0) {
			String encodedImagesJoined = String.join("\n", encodedImages);
			recipe.setImages(encodedImagesJoined);
		} else {
			recipe.setImages(null);
		}
		Recipe updatedRecipe = recipeService.save(recipe);

		model.addAttribute("id", updatedRecipe.getId());
		model.addAttribute("name", updatedRecipe.getName());
		model.addAttribute("body", updatedRecipe.getBody());
		model.addAttribute("tags", updatedRecipe.getTags().split(", "));
		if (updatedRecipe.getImages() != null) {
			model.addAttribute("encodedImages", updatedRecipe.getImages().split("\n"));
		} else {
			model.addAttribute("encodedImages", updatedRecipe.getImages());
		}

		return "recipe";
	}

	/**
	 * Get the web page for adding a recipe.
	 *
	 * @return {@link String}
	 */
	@GetMapping("/add")
	public String getAddRecipePage() {
		return "add";
	}

	/**
	 * Display all recipe names.
	 *
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@GetMapping("/browse")
	public String getBrowsePage(Model model) {
		Iterable<Recipe> recipes = recipeService.findAll();
		List<Recipe> recipeList = new ArrayList<>();
		for (Recipe recipe : recipes) {
			recipeList.add(recipe);
		}
		recipeList.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
		Map<Integer, String> map = new LinkedHashMap<>();
		for (Recipe recipe : recipeList) {
			map.put(recipe.getId(), recipe.getName());
		}
		model.addAttribute("recipes", map);
		return "browse";
	}

	/**
	 * Get the web page for editing a recipe.
	 *
	 * @param id    {@link Integer}
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/edit/{id}")
	public String getEditPage(@PathVariable Integer id, Model model) {
		Recipe recipe = recipeService.get(id);
		model.addAttribute("id", recipe.getId());
		model.addAttribute("name", recipe.getName());
		model.addAttribute("body", recipe.getBody());
		model.addAttribute("tags", recipe.getTags());

		List<String> imagesList = null;
		Object newImages = model.asMap().get("encodedImages");
		if (newImages != null && newImages instanceof List<?>) {
			List<?> images = (List<?>) newImages;
			imagesList = (List<String>) images;
		}
		if (imagesList == null) {
			if (recipe.getImages() != null && !recipe.getImages().isEmpty()) {
				imagesList = Arrays.asList(recipe.getImages().split("\n"));
			}
		}
		model.addAttribute("encodedImages", imagesList);
		return "edit";
	}

	/**
	 * Get the home web page.
	 *
	 * @return {@link String}
	 */
	@GetMapping("/")
	public String getHomePage() {
		return "index";
	}

	/**
	 * Get the web page for logging in.
	 *
	 * @return {@link String}
	 */
	@GetMapping("/login")
	public String getLoginPage() {
		return "login";
	}

	/**
	 * Get a recipe by id.
	 *
	 * @param id    {@link Integer}
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@GetMapping("/recipes/{id}")
	public String getRecipe(@PathVariable Integer id, Model model) {
		Recipe recipe = recipeService.get(id);
		String recipeBody = recipe.getBody();
		String[] splitTags = recipe.getTags().split(", ");
		if (recipe.getImages() != null && !recipe.getImages().isEmpty()) {
			String[] splitImages = recipe.getImages().split("\n");
			model.addAttribute("encodedImages", splitImages);
		}
		model.addAttribute("id", recipe.getId());
		model.addAttribute("name", recipe.getName());
		model.addAttribute("body", recipeBody);
		model.addAttribute("tags", splitTags);
		return "recipe";
	}

	/**
	 * Search for recipes by tag name.
	 *
	 * @param tag   {@link String}
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@GetMapping("/tags/{tag}")
	public String getRecipesByTag(@PathVariable String tag, Model model) {
		Set<Recipe> recipesByTag = recipeService.findByTag(tag);

		Comparator<Recipe> comparator = (o1, o2) -> o1.compareTo(o2);
		List<Recipe> recipes = new ArrayList<>(recipesByTag);
		Collections.sort(recipes, comparator);

		Map<Integer, String> recipeMap = new LinkedHashMap<>();
		for (Recipe recipe : recipes) {
			recipeMap.put(recipe.getId(), recipe.getName());
		}

		model.addAttribute("searchTerm", tag);
		model.addAttribute("recipes", recipeMap);
		model.addAttribute("tags", getAllTags());
		return "search";
	}

	/**
	 * Get the web page for searching.
	 * 
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@GetMapping("/search")
	public String getSearchPage(Model model) {
		Set<String> sortedTags = getAllTags();
		model.addAttribute("tags", sortedTags);
		return "search";
	}

	/**
	 * Get the set of all tags.
	 * 
	 * @return {@link Set} of {@link String}
	 */
	private Set<String> getAllTags() {
		Iterable<Recipe> recipes = recipeService.findAll();
		Iterator<Recipe> iter = recipes.iterator();
		List<String> tagList = new ArrayList<>();
		while (iter.hasNext()) {
			Recipe recipe = iter.next();
			String tags = recipe.getTags();
			String[] splitTags = tags.split(", ");
			tagList.addAll(Arrays.asList(splitTags));
		}
		tagList.sort((o1, o2) -> o1.compareTo(o2));
		Set<String> sortedTags = new LinkedHashSet<>();
		for (String tag : tagList) {
			sortedTags.add(tag);
		}
		return sortedTags;
	}

	/**
	 * Search by name or tags.
	 *
	 * @param searchTerm         {@link String}
	 * @param redirectAttributes {@link RedirectAttributes}
	 * @return {@link String}
	 */
	@PostMapping("/search")
	public String search(@RequestParam String searchTerm, RedirectAttributes redirectAttributes) {
		String trimmed = searchTerm.trim();
		Set<Recipe> recipes1 = recipeService.findByTag(trimmed);
		Set<Recipe> recipes2 = recipeService.findByName(trimmed);
		Set<Recipe> recipes3 = recipeService.findByBody(trimmed);
		recipes1.addAll(recipes2);
		recipes1.addAll(recipes3);

		Comparator<Recipe> comparator = (o1, o2) -> o1.compareTo(o2);
		List<Recipe> recipes = new ArrayList<>(recipes1);
		Collections.sort(recipes, comparator);

		Map<Integer, String> recipeMap = new LinkedHashMap<>();
		for (Recipe recipe : recipes) {
			recipeMap.put(recipe.getId(), recipe.getName());
		}

		redirectAttributes.addFlashAttribute("searchTerm", trimmed);
		redirectAttributes.addFlashAttribute("recipes", recipeMap);
		redirectAttributes.addFlashAttribute("tags", getAllTags());

		return "redirect:/search";
	}

	/**
	 * Base64 encode and "\n"-delimit uploaded images.
	 *
	 * @param files              {@link MultipartFile}[]
	 * @param pageName           {@link StringIndexOutOfBoundsException}
	 * @param redirectAttributes {@link RedirectAttributes}
	 * @return {@link String}
	 * @throws IOException when computers take over
	 */
	@PostMapping("/uploadImage")
	public RedirectView uploadImage(@RequestParam MultipartFile[] files, @RequestParam String pageName,
			RedirectAttributes redirectAttributes) throws IOException {
		List<String> encodedImages = new ArrayList<>();
		String encodedString;
		for (MultipartFile file : files) {
			byte[] bytes = file.getBytes();
			if (bytes.length > 0) {
				encodedString = Base64.getEncoder().encodeToString(bytes);
				encodedImages.add(encodedString);
			}
		}
		redirectAttributes.addFlashAttribute("encodedImages", encodedImages);
		RedirectView redirectView = new RedirectView();
		redirectView.setContextRelative(true);
		redirectView.setUrl(pageName);
		return redirectView;
	}
}