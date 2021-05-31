package de.dhbw.plugins.ui.thymleaf;

import de.dhbw.adapter.ui.mapper.AmountAdapter;
import de.dhbw.adapter.ui.objects.StoredItemWeb;
import de.dhbw.application.storage.ManageStorageService;
import de.dhbw.application.storage.ReadStorageService;
import de.dhbw.application.storage.TakeAmountService;
import de.dhbw.exceptions.ItemNotFoundException;
import de.dhbw.valueObjects.Location;
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

    private ManageStorageService manageStorageService;
    private ReadStorageService readStorageService;
    private TakeAmountService takeAmountService;

    @Autowired
    public ItemStorageController(ManageStorageService manageStorageService, ReadStorageService readStorageService, TakeAmountService takeAmountService) {
        this.manageStorageService = manageStorageService;
        this.readStorageService = readStorageService;
        this.takeAmountService = takeAmountService;
    }

    @GetMapping({"", "/storage"})
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
            manageStorageService.storeItem(itemReference, new Location(location), AmountAdapter.ExtractFromString(amount));
        } catch (ItemNotFoundException e) {
            manageStorageService.createAndStoreItem(itemReference, new Location(location), AmountAdapter.ExtractFromString(amount));
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
        manageStorageService.setMinimumAmount(itemReference, AmountAdapter.ExtractFromString(amount));
        return new RedirectView("/storage");
    }
}
