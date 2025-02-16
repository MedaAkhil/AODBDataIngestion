/*package com.aodb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "OperationTime")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long OperationTimeID;
    
    @ManyToOne
    @JoinColumn(name = "FlightLegID", nullable = false)
    private LegData FlightLegID;
    
    @Column(nullable = false)
    private String OperationalQualifier;
    
    private Integer RepeatIndex;
    
    @Column(nullable = false)
    private String TimeType;
    
    @Column(nullable = false)
    private LocalDateTime OperationTimeValue;
}*/
package com.aodb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_time")
public class OperationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationTimeID;

    @ManyToOne
    @JoinColumn(name = "flightLegID", nullable = false)
    private LegData flightLegID;

    private String operationalQualifier;
    private String repeatIndex;
    private String timeType;
    private LocalDateTime operationTimeValue;

    // ✅ Getters and Setters
    public Long getOperationTimeID() { return operationTimeID; }
    public void setOperationTimeID(Long operationTimeID) { this.operationTimeID = operationTimeID; }

    public LegData getFlightLegID() { return flightLegID; }
    public void setFlightLegID(LegData flightLegID) { this.flightLegID = flightLegID; }

    public String getOperationalQualifier() { return operationalQualifier; }
    public void setOperationalQualifier(String operationalQualifier) { this.operationalQualifier = operationalQualifier; }

    public String getRepeatIndex() { return repeatIndex; }
    public void setRepeatIndex(String repeatIndex) { this.repeatIndex = repeatIndex; }

    public String getTimeType() { return timeType; }
    public void setTimeType(String timeType) { this.timeType = timeType; }

    public LocalDateTime getOperationTimeValue() { return operationTimeValue; }
    public void setOperationTimeValue(LocalDateTime operationTimeValue) { this.operationTimeValue = operationTimeValue; }
}
