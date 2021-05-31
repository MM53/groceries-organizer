package org.example.application.cookbook;

import org.example.aggregates.Item;
import org.example.aggregates.Recipe;
import org.example.aggregates.StoredItem;
import org.example.application.configuration.TestConfig;
import org.example.application.exceptions.MissingIngredientsException;
import org.example.entities.Ingredient;
import org.example.entities.ItemLocation;
import org.example.repositories.ItemRepository;
import org.example.repositories.RecipeRepository;
import org.example.repositories.StoredItemRepository;
import org.example.units.UnitType;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = TestConfig.class)
@ActiveProfiles("testing")
class UseRecipeServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private StoredItemRepository storedItemRepository;

    @Autowired
    private UseRecipeService useRecipeService;

    @BeforeEach
    public void init() {
        Item item = new Item("Brot", UnitType.WEIGHT);
        when(itemRepository.findItemById("Brot")).thenReturn(Optional.of(item));
        when(itemRepository.findItemById(not(eq("Brot")))).thenReturn(Optional.empty());
        when(itemRepository.getAll()).thenReturn(Arrays.asList(item));
        when(itemRepository.checkExistenceById("Brot")).thenReturn(true);
    }

    private Recipe setupRecipe(String ingredientName, Amount amount) {
        Recipe recipe = new Recipe(UUID.randomUUID(), "Test recipe", Collections.emptySet(), Set.of(new Ingredient(ingredientName, amount)), "");
        when(recipeRepository.findByRecipeId(recipe.getId())).thenReturn(Optional.of(recipe));
        return recipe;
    }

    private StoredItem setupStoredItem(String ingredientName, Amount amount) {
        StoredItem storedItem = new StoredItem(UUID.randomUUID(), ingredientName, Set.of(new ItemLocation(new Location("Küche"), amount)), null);
        when(storedItemRepository.findByReferencedItem(new Item(ingredientName, UnitType.WEIGHT))).thenReturn(Optional.of(storedItem));
        return storedItem;
    }

    @Test
    public void checkRecipe_notStored() {
        Recipe recipe = setupRecipe("Butter", new Amount(500, Weight.GRAM));

        List<Ingredient> checkedIngredients = useRecipeService.checkIngredients(recipe.getId());

        assertEquals(1, checkedIngredients.size());
        assertEquals(new Amount(-500, Weight.GRAM), checkedIngredients.get(0).getAmount());
    }

    @Test
    public void checkRecipe_notEnough() {
        Recipe recipe = setupRecipe("Brot", new Amount(500, Weight.GRAM));
        setupStoredItem("Brot", new Amount(200, Weight.GRAM));

        List<Ingredient> checkedIngredients = useRecipeService.checkIngredients(recipe.getId());

        assertEquals(1, checkedIngredients.size());
        assertEquals(new Amount(-300, Weight.GRAM), checkedIngredients.get(0).getAmount());
    }

    @Test
    public void checkRecipe_enough() {
        Recipe recipe = setupRecipe("Brot", new Amount(500, Weight.GRAM));
        setupStoredItem("Brot", new Amount(600, Weight.GRAM));

        List<Ingredient> checkedIngredients = useRecipeService.checkIngredients(recipe.getId());

        assertEquals(1, checkedIngredients.size());
        assertEquals(new Amount(0, Weight.GRAM), checkedIngredients.get(0).getAmount());
    }

    @Test
    public void cookRecipe_notStored() {
        Recipe recipe = setupRecipe("Butter", new Amount(500, Weight.GRAM));

        assertThrows(MissingIngredientsException.class, () -> useRecipeService.cookRecipe(recipe.getId()));
    }

    @Test
    public void cookRecipe_notEnough() {
        Recipe recipe = setupRecipe("Brot", new Amount(500, Weight.GRAM));
        setupStoredItem("Brot", new Amount(200, Weight.GRAM));

        assertThrows(MissingIngredientsException.class, () -> useRecipeService.cookRecipe(recipe.getId()));
    }

    @Test
    public void cookRecipe_enough() {
        Recipe recipe = setupRecipe("Brot", new Amount(500, Weight.GRAM));
        setupStoredItem("Brot", new Amount(600, Weight.GRAM));

        useRecipeService.cookRecipe(recipe.getId());

        ArgumentCaptor<StoredItem> storedItemArgumentCaptor = ArgumentCaptor.forClass(StoredItem.class);
        verify(storedItemRepository, atMostOnce()).save(storedItemArgumentCaptor.capture());
        StoredItem storedItem = storedItemArgumentCaptor.getValue();

        assertEquals(1, storedItem.getItemLocations().size());
        assertEquals(new Amount(100, Weight.GRAM), new ArrayList<>(storedItem.getItemLocations()).get(0).getAmount());
    }
}