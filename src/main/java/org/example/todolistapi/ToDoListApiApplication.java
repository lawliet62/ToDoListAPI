package org.example.todolistapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ToDoListApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToDoListApiApplication.class, args);
    }

}
