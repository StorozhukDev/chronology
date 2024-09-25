package com.storozhuk.dev.chronology.trip.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "trips")
public class TripEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  @Column(nullable = false)
  private UUID createdBy;

  @ManyToMany
  @JoinTable(
      name = "trip_places",
      joinColumns = @JoinColumn(name = "trip_id"),
      inverseJoinColumns = @JoinColumn(name = "place_id"))
  private Set<PlaceEntity> places = new HashSet<>();

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<TripSharedUserEntity> sharedUsers = new HashSet<>();
}
