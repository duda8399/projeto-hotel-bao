package edu.ifmg.com.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "reservation")
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant checkInDate;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant checkOutDate;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

    public Reservation() {}

    public Reservation(Client client, Accommodation accommodation, Instant checkInDate, Instant checkOutDate) {
        this.client = client;
        this.accommodation = accommodation;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public Instant getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Instant checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Instant getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Instant checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
