package org.example.application;

import org.example.AppConfig;
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
    private ItemManager itemManager;

    @Autowired
    private StoredItemRepository storedItemRepository;

    @Autowired
    private ItemStorage itemStorage;

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

        itemManager.addName("Butter", "irische Butter");
        storedItemRepository.getAll();

//        itemStorage.storeItem("Butter", new Location("Kühlschrank"), new Amount(500, Weight.GRAM));
//        itemStorage.storeItem("Butter", new Location("Keller"), new Amount(500, Weight.GRAM));
        itemStorage.storeItem("Butter", new Location("Kühlschrank"), new Amount(250, Weight.GRAM));

        itemStorage.storeItem("Butter", new Location("Kühlschrank"), new Amount(250, Weight.GRAM));

        ItemLocation itemLocation = itemStorage.listItemLocations("Butter")
                                               .stream()
                                               .filter(location -> location.getLocation()
                                                                           .getLocation()
                                                                           .equals("Kühlschrank"))
                                               .findAny()
                                               .get();
        itemStorage.takeAmount("Butter",new Amount(150, Weight.GRAM), itemLocation.getId());

        ShoppingList shoppingList = new ShoppingList("Lebensmittel");
        shoppingList.addShoppingListItem("Brot", new Amount(500, Weight.GRAM));
        shoppingListRepository.save(shoppingList);

        System.out.println(shoppingListRepository.getAll().get(0).getName());

        System.out.println(itemStorage.listItemLocations("Butter"));
        System.out.println(itemManager.listItems());
        System.out.println(itemManager.getItem("Butter"));
    }
}
