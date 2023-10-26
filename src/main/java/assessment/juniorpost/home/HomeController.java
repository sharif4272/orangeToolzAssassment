package assessment.juniorpost.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class HomeController {

    @GetMapping("/")
    public String viewHomePage(){
        return "home";
    }
}
