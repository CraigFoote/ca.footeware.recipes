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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@GetMapping("/")
	public String getSearchPage(Model model) throws IOException {
		return "index";
	}

	@GetMapping("/add")
	public String getAddRecipePage() {
		return "add";
	}

	@PostMapping("/uploadImage")
	public String uploadImage(@RequestParam MultipartFile[] files, RedirectAttributes redirectAttributes)
			throws IOException {
		List<String> encodedImages = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			byte[] bytes = file.getBytes();
			String encodedString = Base64.getEncoder().encodeToString(bytes);
			encodedImages.add(encodedString);
		}
		redirectAttributes.addFlashAttribute("encodedImages", encodedImages);
		return "redirect:/add";
	}

	@PostMapping("/create")
	public String createRecipe(@RequestParam(required = false) String images, @RequestParam String recipeName,
			@RequestParam String recipeText, @RequestParam String tags, RedirectAttributes redirectAttributes)
			throws IOException {

		String cleanText = recipeText.replace("\r\n", "&#13;&#10;");

		String[] splitTags = tags.trim().split(",");
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
		String tagString = builder.toString();

		Recipe recipe = new Recipe(recipeName, cleanText, images, tagString);
		recipeService.save(recipe);

		redirectAttributes.addFlashAttribute("message", "Success! Create another?");
		return "redirect:/add";
	}

	@GetMapping("/search")
	public String getSearchPage() {
		return "search";
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

	@GetMapping("/recipes/{id}")
	public String getImages(@PathVariable Integer id, Model model) {
		Recipe recipe = recipeService.get(id);
		String recipeBody = recipe.getBody();
		String lineWrappedBody = recipeBody.replace("&#13;&#10;", "<br/>");
		
		String[] splitTags = recipe.getTags().split(", ");
		
		model.addAttribute("name", recipe.getName());
		model.addAttribute("body", lineWrappedBody);
		model.addAttribute("tags", splitTags);
		model.addAttribute("images", recipe.getImages());
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
}