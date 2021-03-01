package org.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Application {

    public static void main(String[] args) {

        var context = new AnnotationConfigApplicationContext(AppConfig.class);

        var bean = context.getBean(Application.class);
        bean.run();

        context.close();
    }

    public void run() {
    }
}
