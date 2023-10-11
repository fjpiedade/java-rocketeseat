package phi.raiz.tech.todolist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyFirstController {

    @GetMapping
    public String firstMessage(){
        return "It's working";
    }
}
