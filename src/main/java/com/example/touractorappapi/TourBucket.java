package com.example.touractorappapi;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Table( name = "tour_buckets" )
public class TourBucket {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name = "increment",strategy = "increment")
    private Long id;

    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "starts_at")
    private LocalDate startsAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "ends_at")
    private LocalDate endsAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDate startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDate getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDate endsAt) {
        this.endsAt = endsAt;
    }
}
