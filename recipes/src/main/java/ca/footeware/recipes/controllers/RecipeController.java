package ca.footeware.recipes.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
 *
 * @author Footeware.ca
 *
 */
@Controller
public class RecipeController {

	@Autowired
	private RecipeService recipeService;

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

	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, Model model) {
		recipeService.delete(id);
		return "search";
	}

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

	@GetMapping("/add")
	public String getAddRecipePage() {
		return "add";
	}

	@GetMapping("/browse")
	public String getBrowsePage(Model model) {
		Iterable<Recipe> recipes = recipeService.findAll();
		List<Recipe> recipeList = new ArrayList<>();
		for (Recipe recipe : recipes) {
			recipeList.add(recipe);
		}
		recipeList.sort((o1, o2) -> o1.compareTo(o2));
		Map<Integer, String> map = recipeList.stream().collect(Collectors.toMap(Recipe::getId, Recipe::getName));

		model.addAttribute("recipes", map);
		return "browse";
	}

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
		return "/edit";
	}

	@GetMapping("/recipes/{id}")
	public String getImages(@PathVariable Integer id, Model model) {
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
		return "search";
	}

	@GetMapping("/search")
	public String getSearchPage() {
		return "search";
	}

	@GetMapping("/")
	public String getSearchPage(Model model) throws IOException {
		return "index";
	}

	@PostMapping("/search")
	public String search(@RequestParam String searchTerm, RedirectAttributes redirectAttributes) {
		Set<Recipe> recipes1 = recipeService.findByTag(searchTerm.trim());
		Set<Recipe> recipes2 = recipeService.findByName(searchTerm.trim());
		recipes1.addAll(recipes2);

		Comparator<Recipe> comparator = (o1, o2) -> o1.compareTo(o2);
		List<Recipe> recipes = new ArrayList<>(recipes1);
		Collections.sort(recipes, comparator);

		Map<Integer, String> recipeMap = new LinkedHashMap<>();
		for (Recipe recipe : recipes) {
			recipeMap.put(recipe.getId(), recipe.getName());
		}

		redirectAttributes.addFlashAttribute("searchTerm", searchTerm);
		redirectAttributes.addFlashAttribute("recipes", recipeMap);

		return "redirect:/search";
	}

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