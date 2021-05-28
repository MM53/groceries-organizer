package org.example.plugins;

import org.example.adapter.AmountAdapter;
import org.example.adapter.StoredItemWeb;
import org.example.application.storage.ReadStorageService;
import org.example.application.storage.TakeAmountService;
import org.example.application.storage.UpdateStorageService;
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
import java.util.UUID;

@Controller
public class ItemStorageController {

    private UpdateStorageService updateStorageService;
    private ReadStorageService readStorageService;
    private TakeAmountService takeAmountService;

    @Autowired
    public ItemStorageController(UpdateStorageService updateStorageService, ReadStorageService readStorageService, TakeAmountService takeAmountService) {
        this.updateStorageService = updateStorageService;
        this.readStorageService = readStorageService;
        this.takeAmountService = takeAmountService;
    }

    @GetMapping("/storage")
    public String listStoredItems(Model model) {
        model.addAttribute("template", "storedItemsList");
        final List<StoredItemWeb> storedItems = readStorageService.listStoredItems()
                                                                  .stream()
                                                                  .map(StoredItemWeb::new)
                                                                  .toList();
        model.addAttribute("storedItems", storedItems);
        return "layout/main";
    }

    @PostMapping("/storage/{itemReference}/store")
    public RedirectView storeAmount(@PathVariable("itemReference") String itemReference, @RequestParam String location, @RequestParam String amount) {
        try {
            updateStorageService.storeItem(itemReference, new Location(location), AmountAdapter.ExtractFromString(amount));
        } catch (ItemNotFoundException e) {
            updateStorageService.createAndStoreItem(itemReference, new Location(location), AmountAdapter.ExtractFromString(amount));
        }
        return new RedirectView("/storage");
    }

    @PostMapping("/storage/{itemReference}/take")
    public RedirectView takeAmounts(@PathVariable("itemReference") String itemReference, @RequestParam("location-id") String locationId, @RequestParam String amount) {
        takeAmountService.takeAmount(itemReference, AmountAdapter.ExtractFromString(amount), UUID.fromString(locationId));
        return new RedirectView("/storage");
    }

    @PostMapping("/storage/{itemReference}/minimumAmount")
    public RedirectView setMinimumAmount(@PathVariable("itemReference") String itemReference, @RequestParam String amount) {
        updateStorageService.setMinimumAmount(itemReference, AmountAdapter.ExtractFromString(amount));
        return new RedirectView("/storage");
    }
}
