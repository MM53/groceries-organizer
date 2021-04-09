package org.example.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
public class ItemStorageController {

    @GetMapping("/storage")
    public String listStoredItems(Model model) {
        model.addAttribute("template", "storedItemsList");
        model.addAttribute("storedItems", Arrays.asList("Item1", "Item2", "Item3", "Item4"));
        return "layout/main";
    }
}
