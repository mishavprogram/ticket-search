package com.mykhailo.ticket_search.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "tickets")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String number;

    @Column(length = 300, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private LocalDate closedDate;//TODO think if I need unclosed tickets in system

    public TicketEntity() {
    }

    public TicketEntity(String number, String title, String description, LocalDate closedDate) {
        this.number = number;
        this.title = title;
        this.description = description;
        this.closedDate = closedDate;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getClosedDate() {
        return closedDate;
    }

    @Override
    public String toString() {
        return "TicketEntity{" +
                "number='" + number + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}