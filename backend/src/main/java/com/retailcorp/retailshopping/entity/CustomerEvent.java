package com.retailcorp.retailshopping.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.retailcorp.retailshopping.util.JsonStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "customer_events")
@Data
@Convert(attributeName = "eventData", converter = JsonStringConverter.class)
public class CustomerEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    private Long customerId;
    private LocalDateTime eventTimestamp=LocalDateTime.now();
    
    // @Column(columnDefinition = "jsonb")
    // @Convert(converter = JsonStringConverter.class)
    // @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", columnDefinition = "jsonb")
    private String eventData; // store JSON as String
    
    private LocalDateTime loadTimestamp;
}

