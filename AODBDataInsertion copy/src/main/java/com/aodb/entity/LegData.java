package com.aodb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LegData")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LegData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long FlightLegID;
    
    @Column(nullable = false)
    private String AirlineCode;
    
    @Column(nullable = false)
    private String FlightNumber;
    
    @Column(nullable = false)
    private String DepartureAirport;
    
    @Column(nullable = false)
    private String ArrivalAirport;
    
    @Column(nullable = false)
    private LocalDateTime OriginDate;
    
    @Column(nullable = false)
    private String InternationalStatus;
    
    @Column(nullable = false)
    private String OperationalStatus;
    
    @Column(nullable = false)
    private String PublicStatus;
    
    @Column(nullable = false)
    private String ServiceType;
    
    @Column(nullable = false)
    private String Registration;
    
    @Column(nullable = false)
    private String TailNumber;
    
    @Column(nullable = false)
    private String AircraftType;
    
    @Column(nullable = false)
    private String AircraftSubType;
    
    @Column(nullable = false)
    private String ArrivalAirportCodeContext;
    
    @Column(nullable = false)
    private String AODBFlightID;
    
    @Column(nullable = false)
    private String DepartureAirportCodeContext;
}
