package com.neighbor.eventmosaic.adapter.config;

import com.neighbor.eventmosaic.adapter.parser.CsvParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    /**
     * Создает мапу парсеров, где ключом является поддерживаемый класс
     *
     * @param parsers список всех доступных парсеров
     * @return мапа парсеров
     */
    @Bean
    public Map<Class<?>, CsvParser<?>> csvParsers(List<CsvParser<?>> parsers) {
        return parsers.stream()
                .collect(Collectors.toMap(
                        CsvParser::getSupportedClass,
                        Function.identity()
                ));
    }
}
