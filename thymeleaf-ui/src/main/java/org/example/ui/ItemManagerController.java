package org.example.ui;

import org.example.entities.aggregateRoots.Item;
import org.example.units.UnitType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class ItemManagerController {

    @GetMapping("/items")
    public String listItems(Model model) {
        model.addAttribute("template", "itemsList");
        model.addAttribute("items", Arrays.asList("Item1", "Item2", "Item3", "Item4"));
        return "layout/main";
    }

    @PostMapping("/items")
    public String createItem(@RequestParam String itemName, @RequestParam String unitType, Model model) {
        model.addAttribute("template", "manageItem");
        model.addAttribute("item", new Item(itemName, UnitType.valueOf(unitType)));
        return "layout/main";
    }

    @GetMapping("/items/{id}")
    public String listItems(@PathVariable("id") String id, Model model) {
        model.addAttribute("template", "manageItem");
        model.addAttribute("item", new Item(id, UnitType.WEIGHT));
        return "layout/main";
    }

    @PostMapping("/items/{id}/names")
    public String addItemName(@PathVariable("id") String id, @RequestParam String name, Model model) {
        model.addAttribute("template", "manageItem");
        final Item item = new Item(id, UnitType.WEIGHT);
        item.addAlternativeName(name);
        model.addAttribute("item", item);
        return "layout/main";
    }
}
