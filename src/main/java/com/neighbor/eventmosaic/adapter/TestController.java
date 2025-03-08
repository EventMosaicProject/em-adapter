package com.neighbor.eventmosaic.adapter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping
    public String test() {
        log.debug("Это DEBUG сообщение из em-adapter");
        log.info("Это INFO сообщение из em-adapter");
        log.warn("Это WARN сообщение из em-adapter");

        // Правильное логирование исключения
        try {
            // Искусственно создаем глубокий стек вызовов для демонстрации
            generateException(3);
        } catch (Exception e) {
            // Передаем исключение как отдельный параметр
            log.error("Это ERROR сообщение из em-adapter с исключением", e);
        }

        // Тестирование деления на ноль для ArithmeticException
        try {
            int result = 10 / 0; // Вызовет ArithmeticException
            log.info("Результат: " + result); // Эта строка не выполнится
        } catch (ArithmeticException e) {
            log.error("Ошибка деления на ноль", e);
        }
        
        return "Тестовые логи сгенерированы!";
    }

    
    private void generateException(int depth) {
        if (depth <= 0) {
            throw new RuntimeException("Тестовая ошибка верхнего уровня", 
                new IllegalArgumentException("Вложенная причина ошибки"));
        }
        generateException(depth - 1);
    }
}