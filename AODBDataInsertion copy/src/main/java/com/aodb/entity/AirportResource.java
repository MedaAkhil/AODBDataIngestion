package com.aodb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AirportResource")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirportResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ResourceID;
    
    @ManyToOne
    @JoinColumn(name = "FlightLegID", nullable = false)
    private LegData FlightLegID;
    
    @Column(nullable = false)
    private String Usage;
    
    @Column(nullable = false)
    private String DepartureOrArrival;
    
    @Column(nullable = false)
    private String AircraftParkingPositionQualifier;
    
    @Column(nullable = false)
    private String PassengerGate;
    
    private Integer PassengerGateRepeatIndex;
    
    private String AircraftTerminal;
    
    private String PublicTerminal;
    
    private Integer PublicTerminalRepeatIndex;
    
    private String InfoType;
    
    private Integer CheckRepeatIndex;
    
    private String Area;
    
    private String Code;
    
    private LocalDateTime StartTime;
    
    private LocalDateTime EndTime;
    
    private String Terminal;
    
    private String AirlineIATACode;
    
    private String AirlinePaxGHA;
    
    private String BaggageProcess;
    
    private LocalDateTime BaggageOpenTime;
    
    private LocalDateTime BaggageCloseTime;
    
    private Integer BaggageRepeatIndex;
    
    private String SegregationName;
    
    private String BaggageBeltCode;
}
