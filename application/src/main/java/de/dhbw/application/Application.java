package de.dhbw.application;

import de.dhbw.AppConfig;
import de.dhbw.aggregates.ShoppingList;
import de.dhbw.application.items.ManageItemsService;
import de.dhbw.application.items.ReadItemsService;
import de.dhbw.application.storage.ReadStorageService;
import de.dhbw.application.storage.TakeAmountService;
import de.dhbw.application.storage.UpdateStorageService;
import de.dhbw.entities.ItemLocation;
import de.dhbw.repositories.ShoppingListRepository;
import de.dhbw.repositories.StoredItemRepository;
import de.dhbw.units.Weight;
import de.dhbw.valueObjects.Amount;
import de.dhbw.valueObjects.Location;
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
    private TakeAmountService takeAmountService;

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
        takeAmountService.takeAmount("Butter", new Amount(150, Weight.GRAM), itemLocation.getId());

        ShoppingList shoppingList = new ShoppingList("Lebensmittel");
        shoppingList.addShoppingListItem("Brot", new Amount(500, Weight.GRAM));
        shoppingListRepository.save(shoppingList);

        System.out.println(shoppingListRepository.getAll().get(0).getName());

        System.out.println(readStorageService.listItemLocations("Butter"));
        System.out.println(readItemsService.listItems());
        System.out.println(readItemsService.getItem("Butter"));
    }
}
