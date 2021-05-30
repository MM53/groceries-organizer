package org.example.plugins.ui.thymleaf;

import org.example.adapter.ui.mapper.AmountAdapter;
import org.example.aggregates.ShoppingList;
import org.example.application.shoppingList.ReadShoppingListService;
import org.example.application.shoppingList.UpdateShoppingListEntriesService;
import org.example.entities.ShoppingListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class ShoppingListController {

    private final ReadShoppingListService readShoppingListService;
    private final UpdateShoppingListEntriesService updateShoppingListEntriesService;

    @Autowired
    public ShoppingListController(ReadShoppingListService readShoppingListService, UpdateShoppingListEntriesService updateShoppingListEntriesService) {
        this.readShoppingListService = readShoppingListService;
        this.updateShoppingListEntriesService = updateShoppingListEntriesService;
    }

    @GetMapping("/shopping-list")
    public String showShoppingList(Model model) {
        model.addAttribute("template", "shoppingList");
        model.addAttribute("shoppingLists", readShoppingListService.getShoppingListNames());
        model.addAttribute("activeShoppingList", ShoppingList.DEFAULT_SHOPPING_LIST);
        model.addAttribute("entries", getEntries(ShoppingList.DEFAULT_SHOPPING_LIST));
        return "layout/main";
    }

    @GetMapping("/shopping-list/{list-name}")
    public String showShoppingList(@PathVariable("list-name") String listName, Model model) {
        model.addAttribute("template", "shoppingList");
        model.addAttribute("shoppingLists", readShoppingListService.getShoppingListNames());
        model.addAttribute("activeShoppingList", listName);
        model.addAttribute("entries", getEntries(listName));
        return "layout/main";
    }

    private List<ShoppingListItem> getEntries(String listName) {
        return readShoppingListService.listEntries(listName)
                                      .stream()
                                      .sorted((o1, o2) -> Boolean.compare(o1.isBought(), o2.isBought()))
                                      .toList();
    }

    @PostMapping("/shopping-list/{list-name}/entries")
    public RedirectView addEntry(@PathVariable("list-name") String listName, @RequestParam("item-name") String itemName, @RequestParam String amount) {
        updateShoppingListEntriesService.addEntry(listName, itemName, AmountAdapter.ExtractFromString(amount));
        return new RedirectView("/shopping-list/" + listName);
    }

    @PostMapping("/shopping-list/{list-name}/entries/bought")
    public RedirectView buyEntry(@PathVariable("list-name") String listName, @RequestParam("item-name") String itemName) {
        updateShoppingListEntriesService.buyEntry(listName, itemName);
        return new RedirectView("/shopping-list/" + listName);
    }

    @PostMapping("/shopping-list/{list-name}/entries/requested")
    public RedirectView unbuyEntry(@PathVariable("list-name") String listName, @RequestParam("item-name") String itemName) {
        updateShoppingListEntriesService.unbuyEntry(listName, itemName);
        return new RedirectView("/shopping-list/" + listName);
    }

    @PostMapping("/shopping-list/{list-name}/entries/clear")
    public RedirectView clearAll(@PathVariable("list-name") String listName) {
        updateShoppingListEntriesService.clearBoughtShoppingListItems(listName);
        return new RedirectView("/shopping-list/" + listName);
    }
}
