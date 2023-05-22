package com.aninfo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cbu;
    private Double amount;
    private String type;

    public String getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }

    public Long getCbu() {
        return cbu;
    }
}
