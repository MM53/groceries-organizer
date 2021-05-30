package org.example.plugins.ui.thymleaf;

import org.example.application.items.ManageItemsService;
import org.example.application.items.ReadItemsService;
import org.example.units.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ItemManagerController {

    private ManageItemsService manageItemsService;
    private ReadItemsService readItemsService;

    @Autowired
    public ItemManagerController(ManageItemsService manageItemsService, ReadItemsService readItemsService) {
        this.manageItemsService = manageItemsService;
        this.readItemsService = readItemsService;
    }

    @GetMapping("/items")
    public String listItems(Model model) {
        model.addAttribute("template", "itemsList");
        model.addAttribute("items", readItemsService.listItems());
        return "layout/main";
    }

    @PostMapping("/items")
    public RedirectView createItem(@RequestParam String name, @RequestParam String unitType) {
        manageItemsService.createItem(name, UnitType.valueOf(unitType));
        return new RedirectView("/items");
    }

    @GetMapping("/items/{name}")
    public String listItems(@PathVariable("name") String name, Model model) {
        model.addAttribute("template", "manageItem");
        model.addAttribute("item", readItemsService.getItem(name));
        return "layout/main";
    }

    @PostMapping("/items/delete")
    public RedirectView deleteItem(@RequestParam String name) {
        manageItemsService.deleteItem(name);
        return new RedirectView("/items");
    }

    @PostMapping("/items/{name}/names")
    public RedirectView addItemName(@PathVariable("name") String name, @RequestParam String alternativeName) {
        manageItemsService.addName(name, alternativeName);
        return new RedirectView("/items/" + name);
    }

    @PostMapping("/items/{name}/removeName")
    public RedirectView removeItemName(@PathVariable("name") String name, @RequestParam String alternativeName) {
        manageItemsService.removeName(name, alternativeName);
        return new RedirectView("/items/" + name);
    }
}
