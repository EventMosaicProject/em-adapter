package com.neighbor.eventmosaic.adapter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO события из GDELT CSV файла (61 поле)
 */
@Data
@NoArgsConstructor
public class Event {

    /**
     * Глобально уникальный идентификатор события
     */
    private Long globalEventId;

    /**
     * Дата события в формате ГГГГММДД
     */
    private Integer day;

    /**
     * Дата события в формате ГГГГММ
     */
    private Integer monthYear;

    /**
     * Год события
     */
    private Integer year;

    /**
     * Дробная дата события в формате ГГГГ.ДДДД
     */
    private Double fractionDate;

    /**
     * Полный код CAMEO для Actor1
     */
    private String actor1Code;

    /**
     * Имя Actor1
     */
    private String actor1Name;

    /**
     * Код страны Actor1 (3-символьный)
     */
    private String actor1CountryCode;

    /**
     * Код известной группы для Actor1
     */
    private String actor1KnownGroupCode;

    /**
     * Код этнической принадлежности Actor1
     */
    private String actor1EthnicCode;

    /**
     * Первичный код религиозной принадлежности Actor1
     */
    private String actor1Religion1Code;

    /**
     * Вторичный код религиозной принадлежности Actor1
     */
    private String actor1Religion2Code;

    /**
     * Первичный код типа/роли Actor1
     */
    private String actor1Type1Code;

    /**
     * Вторичный код типа/роли Actor1
     */
    private String actor1Type2Code;

    /**
     * Третичный код типа/роли Actor1
     */
    private String actor1Type3Code;

    /**
     * Полный код CAMEO для Actor2
     */
    private String actor2Code;

    /**
     * Имя Actor2
     */
    private String actor2Name;

    /**
     * Код страны Actor2 (3-символьный)
     */
    private String actor2CountryCode;

    /**
     * Код известной группы для Actor2
     */
    private String actor2KnownGroupCode;

    /**
     * Код этнической принадлежности Actor2
     */
    private String actor2EthnicCode;

    /**
     * Первичный код религиозной принадлежности Actor2
     */
    private String actor2Religion1Code;

    /**
     * Вторичный код религиозной принадлежности Actor2
     */
    private String actor2Religion2Code;

    /**
     * Первичный код типа/роли Actor2
     */
    private String actor2Type1Code;

    /**
     * Вторичный код типа/роли Actor2
     */
    private String actor2Type2Code;

    /**
     * Третичный код типа/роли Actor2
     */
    private String actor2Type3Code;

    /**
     * Флаг, указывающий является ли событие корневым
     */
    private Integer isRootEvent;

    /**
     * Необработанный код действия CAMEO
     */
    private String eventCode;

    /**
     * Код второго уровня в таксономии CAMEO
     */
    private String eventBaseCode;

    /**
     * Код первого (корневого) уровня в таксономии CAMEO
     */
    private String eventRootCode;

    /**
     * Классификация события (1-4)
     */
    private Integer quadClass;

    /**
     * Оценка влияния события по шкале Гольдштейна (-10 до +10)
     */
    private Double goldsteinScale;

    /**
     * Количество упоминаний события
     */
    private Integer numMentions;

    /**
     * Количество источников информации с упоминаниями события
     */
    private Integer numSources;

    /**
     * Количество документов с упоминаниями события
     */
    private Integer numArticles;

    /**
     * Средний тон всех документов (-100 до +100)
     */
    private Double avgTone;

    /**
     * Тип географического объекта Actor1
     */
    private Integer actor1GeoType;

    /**
     * Полное имя географического объекта Actor1
     */
    private String actor1GeoFullName;

    /**
     * Код страны географического объекта Actor1
     */
    private String actor1GeoCountryCode;

    /**
     * Код административного подразделения 1 для Actor1
     */
    private String actor1GeoAdm1Code;

    /**
     * Код административного подразделения 2 для Actor1
     */
    private String actor1GeoAdm2Code;

    /**
     * Широта географического объекта Actor1
     */
    private Double actor1GeoLat;

    /**
     * Долгота географического объекта Actor1
     */
    private Double actor1GeoLong;

    /**
     * Идентификатор географического объекта Actor1
     */
    private String actor1GeoFeatureId;

    /**
     * Тип географического объекта Actor2
     */
    private Integer actor2GeoType;

    /**
     * Полное имя географического объекта Actor2
     */
    private String actor2GeoFullName;

    /**
     * Код страны географического объекта Actor2
     */
    private String actor2GeoCountryCode;

    /**
     * Код административного подразделения 1 для Actor2
     */
    private String actor2GeoAdm1Code;

    /**
     * Код административного подразделения 2 для Actor2
     */
    private String actor2GeoAdm2Code;

    /**
     * Широта географического объекта Actor2
     */
    private Double actor2GeoLat;

    /**
     * Долгота географического объекта Actor2
     */
    private Double actor2GeoLong;

    /**
     * Идентификатор географического объекта Actor2
     */
    private String actor2GeoFeatureId;

    /**
     * Тип географического объекта действия
     */
    private Integer actionGeoType;

    /**
     * Полное имя географического объекта действия
     */
    private String actionGeoFullName;

    /**
     * Код страны географического объекта действия
     */
    private String actionGeoCountryCode;

    /**
     * Код административного подразделения 1 для действия
     */
    private String actionGeoAdm1Code;

    /**
     * Код административного подразделения 2 для действия
     */
    private String actionGeoAdm2Code;

    /**
     * Широта географического объекта действия
     */
    private Double actionGeoLat;

    /**
     * Долгота географического объекта действия
     */
    private Double actionGeoLong;

    /**
     * Идентификатор географического объекта действия
     */
    private String actionGeoFeatureId;

    /**
     * Дата добавления события в базу данных GDELT (ГГГГММДДЧЧММСС)
     */
    private Long dateAdded;

    /**
     * URL или цитата первого новостного сообщения с упоминанием события
     */
    private String sourceUrl;
}
