package org.example.application;

import org.example.AppConfig;
import org.example.application.items.ManageItemsService;
import org.example.application.items.ReadItemsService;
import org.example.application.storage.ReadStorageService;
import org.example.application.storage.UpdateStorageService;
import org.example.entities.ItemLocation;
import org.example.entities.aggregateRoots.ShoppingList;
import org.example.repositories.ShoppingListRepository;
import org.example.repositories.StoredItemRepository;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

@Component
public class Application {

    @Autowired
    private ManageItemsService manageItemsService;

    @Autowired
    private ReadItemsService readItemsService;

    @Autowired
    private StoredItemRepository storedItemRepository;

    @Autowired
    private UpdateStorageService updateStorageService;

    @Autowired
    private ReadStorageService readStorageService;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    public static void main(String[] args) {
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.setActiveProfiles("production");

        var context = new AnnotationConfigApplicationContext();
        context.setEnvironment(environment);
        context.register(AppConfig.class);
        context.refresh();

        var bean = context.getBean(Application.class);
        bean.run();

        context.close();
    }

    public void run() {
//        itemManager.createItem("Brot", UnitType.WEIGHT);
//        itemManager.createItem("Butter", UnitType.WEIGHT);

        manageItemsService.addName("Butter", "irische Butter");
        storedItemRepository.getAll();

//        itemStorage.storeItem("Butter", new Location("Kühlschrank"), new Amount(500, Weight.GRAM));
//        itemStorage.storeItem("Butter", new Location("Keller"), new Amount(500, Weight.GRAM));
        updateStorageService.storeItem("Butter", new Location("Kühlschrank"), new Amount(250, Weight.GRAM));

        updateStorageService.storeItem("Butter", new Location("Kühlschrank"), new Amount(250, Weight.GRAM));

        ItemLocation itemLocation = readStorageService.listItemLocations("Butter")
                                                        .stream()
                                                        .filter(location -> location.getLocation()
                                                                           .getLocation()
                                                                           .equals("Kühlschrank"))
                                                        .findAny()
                                                        .get();
        updateStorageService.takeAmount("Butter", new Amount(150, Weight.GRAM), itemLocation.getId());

        ShoppingList shoppingList = new ShoppingList("Lebensmittel");
        shoppingList.addShoppingListItem("Brot", new Amount(500, Weight.GRAM));
        shoppingListRepository.save(shoppingList);

        System.out.println(shoppingListRepository.getAll().get(0).getName());

        System.out.println(readStorageService.listItemLocations("Butter"));
        System.out.println(readItemsService.listItems());
        System.out.println(readItemsService.getItem("Butter"));
    }
}
