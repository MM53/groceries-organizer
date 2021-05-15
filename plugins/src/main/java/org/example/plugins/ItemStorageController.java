package org.example.plugins;

import org.example.adapter.AmountAdapter;
import org.example.adapter.StoredItemWeb;
import org.example.application.ItemStorage;
import org.example.entities.ItemLocation;
import org.example.exceptions.ItemNotFoundException;
import org.example.valueObjects.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ItemStorageController {

    private ItemStorage itemStorage;

    @Autowired
    public ItemStorageController(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @GetMapping("/storage")
    public String listStoredItems(Model model) {
        model.addAttribute("template", "storedItemsList");
        final List<StoredItemWeb> storedItems = itemStorage.listStoredItems()
                                                           .stream()
                                                           .map(StoredItemWeb::new)
                                                           .collect(Collectors.toList());
        model.addAttribute("storedItems", storedItems);
        return "layout/main";
    }

    @PostMapping("/storage/{itemReference}/store")
    public RedirectView storeAmount(@PathVariable("itemReference") String itemReference, @RequestParam String location, @RequestParam String amount) {
        try {
            itemStorage.storeItem(itemReference, new Location(location), AmountAdapter.ExtractFromString(amount));
        } catch (ItemNotFoundException e) {
            itemStorage.createAndStoreItem(itemReference, new Location(location), AmountAdapter.ExtractFromString(amount));
        }
        return new RedirectView("/storage");
    }

    @PostMapping("/storage/{itemReference}/take")
    public RedirectView takeAmounts(@PathVariable("itemReference") String itemReference, @RequestParam String location, @RequestParam String amount) {
        ItemLocation itemLocation = itemStorage.getItemLocation(itemReference, new Location(location));
        itemStorage.takeAmount(itemReference, AmountAdapter.ExtractFromString(amount), itemLocation.getId());
        return new RedirectView("/storage");
    }

    @PostMapping("/storage/{itemReference}/minimumAmount")
    public RedirectView setMinimumAmount(@PathVariable("itemReference") String itemReference, @RequestParam String amount) {
        itemStorage.setMinimumAmount(itemReference, AmountAdapter.ExtractFromString(amount));
        return new RedirectView("/storage");
    }
}
