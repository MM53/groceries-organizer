package de.dhbw.plugins.ui.thymleaf;

import de.dhbw.application.cookbook.ManageCookbookService;
import de.dhbw.application.cookbook.ReadCookbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class TagManagerController {

    private ManageCookbookService manageCookbookService;
    private ReadCookbookService readCookbookService;

    @Autowired
    public TagManagerController(ManageCookbookService manageCookbookService, ReadCookbookService readCookbookService) {
        this.manageCookbookService = manageCookbookService;
        this.readCookbookService = readCookbookService;
    }

    @GetMapping("/cookbook/tags")
    public String listTags(Model model) {
        model.addAttribute("template", "tagsList");
        model.addAttribute("tags", readCookbookService.listTags());
        return "layout/main";
    }

    @PostMapping("/cookbook/tags")
    public RedirectView createTag(@RequestParam String name) {
        manageCookbookService.createTag(name);
        return new RedirectView("/cookbook/tags");
    }

    @PostMapping("/cookbook/tags/delete")
    public RedirectView deleteTag(@RequestParam String name) {
        manageCookbookService.deleteTag(name);
        return new RedirectView("/cookbook/tags");
    }
}
