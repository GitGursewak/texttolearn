package com.gursewak.texttolearn;

import com.gursewak.texttolearn.model.Course;
import com.gursewak.texttolearn.model.Module;
import com.gursewak.texttolearn.model.Lesson;
import com.gursewak.texttolearn.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TextToLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextToLearnApplication.class, args);
    }

}