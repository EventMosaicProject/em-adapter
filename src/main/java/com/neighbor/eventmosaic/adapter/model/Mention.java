package com.neighbor.eventmosaic.adapter.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель упоминания из GDELT CSV файла (15 полей)
 */
@Data
@NoArgsConstructor
public class Mention {

    /**
     * Идентификатор события, которое было упомянуто
     */
    private Long globalEventId;

    /**
     * Временная метка (ГГГГММДДЧЧММСС) первой регистрации события
     */
    private Long eventTimeDate;

    /**
     * Временная метка (ГГГГММДДЧЧММСС) текущего обновления
     */
    private Long mentionTimeDate;

    /**
     * Тип источника документа (1-6)
     */
    private Integer mentionType;

    /**
     * Идентификатор источника документа
     */
    private String mentionSourceName;

    /**
     * Уникальный внешний идентификатор документа
     */
    private String mentionIdentifier;

    /**
     * Номер предложения в статье, где было упомянуто событие
     */
    private Integer sentenceId;

    /**
     * Позиция Actor1 в статье (в символах)
     */
    private Integer actor1CharOffset;

    /**
     * Позиция Actor2 в статье (в символах)
     */
    private Integer actor2CharOffset;

    /**
     * Позиция действия в статье (в символах)
     */
    private Integer actionCharOffset;

    /**
     * Флаг, указывающий было ли событие найдено в необработанном тексте (1) или потребовалась обработка (0)
     */
    private Integer inRawText;

    /**
     * Уверенность в извлечении события (процент)
     */
    private Integer confidence;

    /**
     * Длина исходного документа в символах
     */
    private Integer mentionDocLen;

    /**
     * Тон документа (-100 до +100)
     */
    private Double mentionDocTone;

    /**
     * Информация о переводе документа
     */
    private String mentionDocTranslationInfo;
}
