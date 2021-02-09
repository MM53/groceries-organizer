package org.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Application {


    public static void main(String[] args) {

        var context = new AnnotationConfigApplicationContext();
        context.scan("org.example");
        context.refresh();

        var bean = context.getBean(Application.class);
        bean.run();

        context.close();
    }

    public void run() {
    }
}
