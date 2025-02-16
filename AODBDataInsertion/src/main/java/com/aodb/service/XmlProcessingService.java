

// v1

// package com.aodb.service;

// import com.aodb.entity.LegData;
// import com.aodb.entity.AirportResource;
// import com.aodb.entity.OperationTime;
// import com.aodb.repository.LegDataRepository;
// import com.aodb.repository.AirportResourceRepository;
// import com.aodb.repository.OperationTimeRepository;
// import org.springframework.stereotype.Service;
// import org.w3c.dom.*;
// import org.springframework.transaction.annotation.Transactional;

// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import java.io.File;
// import java.nio.file.*;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.*;

// @Service
// public class XmlProcessorService {
    
//     private final LegDataRepository legDataRepository;
//     private final AirportResourceRepository airportResourceRepository;
//     private final OperationTimeRepository operationTimeRepository;

//     public XmlProcessorService(LegDataRepository legDataRepository,
//                                AirportResourceRepository airportResourceRepository,
//                                OperationTimeRepository operationTimeRepository) {
//         this.legDataRepository = legDataRepository;
//         this.airportResourceRepository = airportResourceRepository;
//         this.operationTimeRepository = operationTimeRepository;
//     }
//     @Transactional
//     public void processXmlFiles(String folderPath) {
//         try {
//             List<LegData> legDataList = new ArrayList<>();
//             Files.walk(Paths.get(folderPath))
//             .filter(Files::isRegularFile)
//             .filter(path -> path.toString().endsWith(".xml"))
//             .forEach(path -> {
//                     System.out.println("Processing XML File: " + path.toString());
//                     List<LegData> processedData = processXmlFile(path.toString());
//                     System.out.println("Processed Data from " + path + ": " + processedData);
//                     legDataList.addAll(processXmlFile(path.toString()));
//                 });

//             if (!legDataList.isEmpty()) {
//                 legDataList.sort(Comparator.comparing(LegData::getOriginDate));
//                 legDataRepository.saveAll(legDataList);
//                 System.out.println(" XML Data Inserted Successfully!");
//             } else {
//                 System.out.println(" No data found to insert!");
//             }

//         } catch (Exception e) {
//             System.err.println(" Error processing XML files: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     public List<LegData> processXmlFile(String filePath) {
//         List<LegData> legDataList = new ArrayList<>();
//         try {
//             File xmlFile = new File(filePath);
//             DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//             factory.setNamespaceAware(true); // Enable namespace processing
//             DocumentBuilder builder = factory.newDocumentBuilder();
//             Document document = builder.parse(xmlFile);
//             document.getDocumentElement().normalize();

//             System.out.println("Processing file: " + filePath);

//             Element headerElement = (Element) document.getElementsByTagNameNS("*", "YIAPL_Header").item(0);
//             String originTimestampStr = getText(headerElement, "OriginatorTimeStamp");
//             DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//             LocalDateTime originTimestamp = LocalDateTime.parse(originTimestampStr, formatter);
    


//             NodeList flightList = document.getElementsByTagName("FlightLeg");
//             NodeList flightLegs = document.getElementsByTagNameNS("*", "FlightLeg");
//             for (int i = 0; i < flightList.getLength(); i++) {
//                 // Node node = flightList.item(i);
//                 // if (node.getNodeType() == Node.ELEMENT_NODE) {
//                     // Element element = (Element) node;

//                     // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                     // LocalDateTime originTimestamp = LocalDateTime.parse(
//                     //         element.getElementsByTagName("OriginatorTimestamp").item(0).getTextContent(), formatter);
//                     Element flightLegElement = (Element) flightLegs.item(i);
//                     Element legIdentifier = (Element) flightLegElement.getElementsByTagNameNS("*", "LegIdentifier").item(0);
        
//                     LegData legData = new LegData();
//                     legData.setAirlineCode(getText(legIdentifier, "Airline"));
//                     legData.setFlightNumber(getText(legIdentifier, "FlightNumber"));
//                     legData.setDepartureAirport(getText(legIdentifier, "DepartureAirport"));
//                     legData.setArrivalAirport(getText(legIdentifier, "ArrivalAirport"));
//                     legData.setOriginDate(originTimestamp);
//                     legData.setInternationalStatus(getText(legIdentifier, "InternationalStatus"));
//                     legData.setOperationalStatus(getText(legIdentifier, "OperationalStatus"));
//                     legData.setPublicStatus(getText(legIdentifier, "PublicStatus"));
//                     legData.setServiceType(getText(legIdentifier, "ServiceType"));
//                     legData.setRegistration(getText(legIdentifier, "Registration"));
//                     legData.setTailNumber(getText(legIdentifier, "TailNumber"));
//                     legData.setAircraftType(getText(legIdentifier, "AircraftType"));
//                     legData.setAircraftSubType(getText(legIdentifier, "AircraftSubType"));
//                     legData.setArrivalAirportCodeContext(getText(legIdentifier, "ArrivalAirportCodeContext"));
//                     legData.setAODBFlightID(getText(legIdentifier, "AODBFlightID"));
//                     legData.setDepartureAirportCodeContext(getText(legIdentifier, "DepartureAirportCodeContext"));

//                     processAirportResources(legIdentifier, legData);

//                     processOperationTimes(legIdentifier, legData);
                    
//                     legDataList.add(legData);
//                 // }
//             }
//         } catch (Exception e) {
//             System.err.println("❌ Error processing XML file " + filePath + ": " + e.getMessage());
//             e.printStackTrace();
//         }
//         return legDataList;
//     }

//     private void processAirportResources(Element flightElement, LegData legData) {
//         NodeList resourceList = flightElement.getElementsByTagName("AirportResource");
//         List<AirportResource> airportResources = new ArrayList<>();
//         for (int j = 0; j < resourceList.getLength(); j++) {
//             Element resourceElement = (Element) resourceList.item(j);
//             AirportResource resource = new AirportResource();
//             resource.setFlightLegID(legData);
//             resource.setUsage(getText(resourceElement, "Usage"));
//             resource.setDepartureOrArrival(getText(resourceElement, "DepartureOrArrival"));
//             resource.setAircraftParkingPositionQualifier(getText(resourceElement, "AircraftParkingPositionQualifier"));
//             resource.setPassengerGate(getText(resourceElement, "PassengerGate"));
//             resource.setPassengerGateRepeatIndex(getInteger(resourceElement, "PassengerGateRepeatIndex"));
//             resource.setAircraftTerminal(getText(resourceElement, "AircraftTerminal"));
//             resource.setPublicTerminal(getText(resourceElement, "PublicTerminal"));
//             resource.setPublicTerminalRepeatIndex(getInteger(resourceElement, "PublicTerminalRepeatIndex"));
//             airportResources.add(resource);
//         }
//         airportResourceRepository.saveAll(airportResources);
//     }
    
//     private void processOperationTimes(Element flightElement, LegData legData) {
//         NodeList timeList = flightElement.getElementsByTagName("OperationTime");
//         List<OperationTime> operationTimes = new ArrayList<>();
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//         for (int k = 0; k < timeList.getLength(); k++) {
//             Element timeElement = (Element) timeList.item(k);
//             OperationTime opTime = new OperationTime();
//             opTime.setFlightLegID(legData);
//             opTime.setOperationalQualifier(getText(timeElement, "OperationalQualifier"));
//             opTime.setTimeType(getText(timeElement, "TimeType"));
//             opTime.setOperationTimeValue(LocalDateTime.parse(getText(timeElement, "OperationTimeValue"), formatter));
//             operationTimes.add(opTime);
//         }
//         operationTimeRepository.saveAll(operationTimes);
//     }
    
//     private String getText(Element element, String tagName) {
//         NodeList nodeList = element.getElementsByTagName(tagName);
//         return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "";
//     }
    
//     private Integer getInteger(Element element, String tagName) {
//         String value = getText(element, tagName);
//         return value.isEmpty() ? null : Integer.parseInt(value);
//     }
// }











// v2



// package com.aodb.service;

// import com.aodb.entity.LegData;
// import com.aodb.entity.AirportResource;
// import com.aodb.entity.OperationTime;
// import com.aodb.repository.LegDataRepository;
// import com.aodb.repository.AirportResourceRepository;
// import com.aodb.repository.OperationTimeRepository;
// import org.springframework.stereotype.Service;
// import org.w3c.dom.*;
// import org.springframework.transaction.annotation.Transactional;

// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import java.io.File;
// import java.nio.file.*;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.*;

// @Service
// public class XmlProcessorService {
    
//     private final LegDataRepository legDataRepository;
//     private final AirportResourceRepository airportResourceRepository;
//     private final OperationTimeRepository operationTimeRepository;

//     public XmlProcessorService(LegDataRepository legDataRepository,
//                                AirportResourceRepository airportResourceRepository,
//                                OperationTimeRepository operationTimeRepository) {
//         this.legDataRepository = legDataRepository;
//         this.airportResourceRepository = airportResourceRepository;
//         this.operationTimeRepository = operationTimeRepository;
//     }

//     @Transactional
//     public void processXmlFiles(String folderPath) {
//         try 
//         {
//             List<LegData> legDataList = new ArrayList<>();
//             Files.walk(Paths.get(folderPath))
//                 .filter(Files::isRegularFile)
//                 .filter(path -> path.toString().endsWith(".xml"))
//                 .forEach(path -> {
//                     System.out.println("Processing XML File: " + path.toString());
//                     List<LegData> processedData = processXmlFile(path.toString());
//                     if (!processedData.isEmpty()) {
//                         legDataList.addAll(processedData);
//                     }
//                 });

//             if (!legDataList.isEmpty()) {
//                 legDataList.sort(Comparator.comparing(LegData::getOriginDate));
//                 legDataRepository.saveAll(legDataList);
//                 System.out.println("✅ XML Data Inserted Successfully!");
//             } else {
//                 System.out.println("⚠️ No valid data found to insert!");
//             }

//         } catch (Exception e) {
//             System.err.println("❌ Error processing XML files: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     public List<LegData> processXmlFile(String filePath) {
//         List<LegData> legDataList = new ArrayList<>();
//         try {
//             File xmlFile = new File(filePath);
//             DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//             factory.setNamespaceAware(true); // Enable namespace processing
//             DocumentBuilder builder = factory.newDocumentBuilder();
//             Document document = builder.parse(xmlFile);
//             document.getDocumentElement().normalize();

//             System.out.println("Processing file: " + filePath);

//             Element headerElement = (Element) document.getElementsByTagNameNS("*", "YIAPL_Header").item(0);
//             if (headerElement == null) {
//                 System.err.println("⚠️ Missing YIAPL_Header in file: " + filePath);
//                 return legDataList;
//             }
//             String originTimestampStr = getText(headerElement, "OriginatorTimeStamp");
//             DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//             LocalDateTime originTimestamp = LocalDateTime.parse(originTimestampStr, formatter);

//             NodeList flightLegs = document.getElementsByTagNameNS("*", "FlightLeg");
//             for (int i = 0; i < flightLegs.getLength(); i++) {
//                 Element flightLegElement = (Element) flightLegs.item(i);
//                 Element legIdentifier = (Element) flightLegElement.getElementsByTagNameNS("*", "LegIdentifier").item(0);
                
//                 if (legIdentifier == null) {
//                     System.err.println("⚠️ Skipping FlightLeg with missing LegIdentifier.");
//                     continue;
//                 }

//                 LegData legData = new LegData();
//                 legData.setAirlineCode(getText(legIdentifier, "Airline"));
//                 legData.setFlightNumber(getText(legIdentifier, "FlightNumber"));
//                 legData.setDepartureAirport(getText(legIdentifier, "DepartureAirport"));
//                 legData.setArrivalAirport(getText(legIdentifier, "ArrivalAirport"));
//                 legData.setOriginDate(originTimestamp);
//                 legData.setInternationalStatus(getText(legIdentifier, "InternationalStatus"));
//                 legData.setOperationalStatus(getText(legIdentifier, "OperationalStatus"));
//                 legData.setPublicStatus(getText(legIdentifier, "PublicStatus"));
//                 legData.setServiceType(getText(legIdentifier, "ServiceType"));
//                 legData.setRegistration(getText(legIdentifier, "Registration"));
//                 legData.setTailNumber(getText(legIdentifier, "TailNumber"));
//                 legData.setAircraftType(getText(legIdentifier, "AircraftType"));
//                 legData.setAircraftSubType(getText(legIdentifier, "AircraftSubType"));
//                 legData.setArrivalAirportCodeContext(getText(legIdentifier, "ArrivalAirportCodeContext"));
//                 legData.setAODBFlightID(getText(legIdentifier, "AODBFlightID"));
//                 legData.setDepartureAirportCodeContext(getText(legIdentifier, "DepartureAirportCodeContext"));

//                 processAirportResources(flightLegElement, legData);
//                 processOperationTimes(flightLegElement, legData);

//                 legDataList.add(legData);
//             }
//         } catch (Exception e) {
//             System.err.println("❌ Error processing XML file " + filePath + ": " + e.getMessage());
//             e.printStackTrace();
//         }
//         return legDataList;
//     }

//     private void processAirportResources(Element flightElement, LegData legData) {
//         NodeList resourceList = flightElement.getElementsByTagNameNS("*", "AirportResources");
//         List<AirportResource> airportResources = new ArrayList<>();
//         for (int j = 0; j < resourceList.getLength(); j++) {
//             Element resourceElement = (Element) resourceList.item(j);
//             AirportResource resource = new AirportResource();
//             resource.setFlightLegID(legData);
//             resource.setUsage(getText(resourceElement, "Usage"));
//             resource.setDepartureOrArrival(getText(resourceElement, "DepartureOrArrival"));
//             resource.setPassengerGate(getText(resourceElement, "PassengerGate"));
//             resource.setAircraftTerminal(getText(resourceElement, "AircraftTerminal"));
//             airportResources.add(resource);
//         }
//         airportResourceRepository.saveAll(airportResources);
        
//     }

//     private void processOperationTimes(Element flightElement, LegData legData) {
//         NodeList timeList = flightElement.getElementsByTagNameNS("*", "OperationTime");
//         List<OperationTime> operationTimes = new ArrayList<>();
//         DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//         for (int k = 0; k < timeList.getLength(); k++) {
//             Element timeElement = (Element) timeList.item(k);
//             OperationTime opTime = new OperationTime();
//             opTime.setFlightLegID(legData);
//             opTime.setOperationalQualifier(getText(timeElement, "OperationalQualifier"));
//             opTime.setTimeType(getText(timeElement, "TimeType"));
//             opTime.setOperationTimeValue(LocalDateTime.parse(getText(timeElement, "OperationTimeValue"), formatter));
//             operationTimes.add(opTime);
//         }
//         operationTimeRepository.saveAll(operationTimes);
//     } 
    

//     private String getText(Element parentElement, String tagName) {
//         NodeList nodeList = parentElement.getElementsByTagName(tagName);
//         if (nodeList.getLength() > 0) {
//             return nodeList.item(0).getTextContent().trim();
//         }
//         return null; // Return null if the tag does not exist
//     }
// }



//v3
//this code got executed and gave half of the legdata table.




/*package com.aodb.service;

import com.aodb.entity.LegData;
import com.aodb.entity.AirportResource;
import com.aodb.entity.OperationTime;
import com.aodb.repository.LegDataRepository;
import com.aodb.repository.AirportResourceRepository;
import com.aodb.repository.OperationTimeRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class XmlProcessorService {
    
    private final LegDataRepository legDataRepository;
    private final AirportResourceRepository airportResourceRepository;
    private final OperationTimeRepository operationTimeRepository;

    public XmlProcessorService(LegDataRepository legDataRepository,
                               AirportResourceRepository airportResourceRepository,
                               OperationTimeRepository operationTimeRepository) {
        this.legDataRepository = legDataRepository;
        this.airportResourceRepository = airportResourceRepository;
        this.operationTimeRepository = operationTimeRepository;
    }

    @Transactional
    public void processXmlFiles(String folderPath) {
        try {
            List<LegData> legDataList = new ArrayList<>();
            Files.walk(Paths.get(folderPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".xml"))
                .forEach(path -> {
                    System.out.println("Processing XML File: " + path.toString());
                    List<LegData> processedData = processXmlFile(path.toString());
                    if (!processedData.isEmpty()) {
                        legDataList.addAll(processedData);
                    }
                });

            if (!legDataList.isEmpty()) {
                legDataList.sort(Comparator.comparing(LegData::getOriginDate));
                legDataRepository.saveAll(legDataList);
                System.out.println("✅ XML Data Inserted Successfully!");
            } else {
                System.out.println("⚠️ No valid data found to insert!");
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing XML files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<LegData> processXmlFile(String filePath) {
        List<LegData> legDataList = new ArrayList<>();
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Enable namespace processing
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            System.out.println("Processing file: " + filePath);

            Element headerElement = (Element) document.getElementsByTagNameNS("*", "YIAPL_Header").item(0);
            if (headerElement == null) {
                System.err.println("⚠️ Missing YIAPL_Header in file: " + filePath);
                return legDataList;
            }
            String originTimestampStr = getText(headerElement, "OriginatorTimeStamp");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime originTimestamp = LocalDateTime.parse(originTimestampStr, formatter);

            NodeList flightLegs = document.getElementsByTagNameNS("*", "FlightLeg");
            for (int i = 0; i < flightLegs.getLength(); i++) {
                Element flightLegElement = (Element) flightLegs.item(i);
                Element legIdentifier = (Element) flightLegElement.getElementsByTagNameNS("*", "LegIdentifier").item(0);
                
                if (legIdentifier == null) {
                    System.err.println("⚠️ Skipping FlightLeg with missing LegIdentifier.");
                    continue;
                }

                LegData legData = new LegData();
                legData.setAirlineCode(getText(legIdentifier, "Airline"));
                legData.setFlightNumber(getText(legIdentifier, "FlightNumber"));
                legData.setDepartureAirport(getText(legIdentifier, "DepartureAirport"));
                legData.setArrivalAirport(getText(legIdentifier, "ArrivalAirport"));
                legData.setOriginDate(originTimestamp);legData.setOriginDate(originTimestamp);
                legData.setInternationalStatus(getText(legIdentifier, "InternationalStatus"));
                legData.setOperationalStatus(getText(legIdentifier, "OperationalStatus"));
                legData.setPublicStatus(getText(legIdentifier, "PublicStatus"));
                legData.setServiceType(getText(legIdentifier, "ServiceType"));
                legData.setRegistration(getText(legIdentifier, "Registration"));
                legData.setTailNumber(getText(legIdentifier, "TailNumber"));
                legData.setAircraftType(getText(legIdentifier, "AircraftType"));
                legData.setAircraftSubType(getText(legIdentifier, "AircraftSubType"));
                legData.setArrivalAirportCodeContext(getText(legIdentifier, "ArrivalAirportCodeContext"));
                legData.setAODBFlightID(getText(legIdentifier, "AODBFlightID"));
                legData.setDepartureAirportCodeContext(getText(legIdentifier, "DepartureAirportCodeContext"));

                processAirportResources(flightLegElement, legData);
                processOperationTimes(flightLegElement, legData);

                legDataList.add(legData);
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing XML file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
        return legDataList;
    }

    private void processAirportResources(Element flightElement, LegData legData) {
        NodeList resourceList = flightElement.getElementsByTagNameNS("*", "AirportResource");
        List<AirportResource> airportResources = new ArrayList<>();
        for (int j = 0; j < resourceList.getLength(); j++) {
            Element resourceElement = (Element) resourceList.item(j);
            AirportResource resource = new AirportResource();
            resource.setFlightLegID(legData);
            resource.setUsage(getText(resourceElement, "Usage"));
            resource.setDepartureOrArrival(getText(resourceElement, "DepartureOrArrival"));
            resource.setPassengerGate(getText(resourceElement, "PassengerGate"));
            resource.setAircraftTerminal(getText(resourceElement, "AircraftTerminal"));
            airportResources.add(resource);
        }
        airportResourceRepository.saveAll(airportResources);
    }

    private void processOperationTimes(Element flightElement, LegData legData) {
        NodeList timeList = flightElement.getElementsByTagNameNS("*", "OperationTime");
        List<OperationTime> operationTimes = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        for (int k = 0; k < timeList.getLength(); k++) {
            Element timeElement = (Element) timeList.item(k);
            OperationTime opTime = new OperationTime();
            opTime.setFlightLegID(legData);
            opTime.setOperationalQualifier(getText(timeElement, "OperationalQualifier"));
            opTime.setTimeType(getText(timeElement, "TimeType"));
            opTime.setOperationTimeValue(LocalDateTime.parse(getText(timeElement, "OperationTimeValue"), formatter));
            operationTimes.add(opTime);
        }
        operationTimeRepository.saveAll(operationTimes);
    }

    private String getText(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagNameNS("*", tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent().trim() : "";
    }
}*/







import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class XmlProcessingService {

    @Autowired
    private LegDataRepository legDataRepository;

    @Autowired
    private AirportResourceRepository airportResourceRepository;

    @Autowired
    private OperationTimeRepository operationTimeRepository;

    public void processXmlFiles(String directoryPath) {
        File folder = new File(directoryPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".xml"));
        
        List<LegData> legDataList = new ArrayList<>();
        List<AirportResource> airportResourceList = new ArrayList<>();
        List<OperationTime> operationTimeList = new ArrayList<>();

        for (File file : files) {
            processXmlFile(file.getAbsolutePath(), legDataList, airportResourceList, operationTimeList);
        }
        
        legDataList.sort(Comparator.comparing(LegData::getOriginDate));
        legDataRepository.saveAll(legDataList);
        airportResourceRepository.saveAll(airportResourceList);
        operationTimeRepository.saveAll(operationTimeList);
    }

    private void processXmlFile(String filePath, List<LegData> legDataList, List<AirportResource> airportResourceList, List<OperationTime> operationTimeList) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            document.getDocumentElement().normalize();

            NodeList flightLegList = document.getElementsByTagName("FlightLeg");
            for (int i = 0; i < flightLegList.getLength(); i++) {
                Element flightLegElement = (Element) flightLegList.item(i);
                LegData legData = extractLegData(flightLegElement);

                if (legData != null) {
                    processAirportResources(flightLegElement, legData, airportResourceList);
                    processOperationTimes(flightLegElement, legData, operationTimeList);
                    legDataList.add(legData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LegData extractLegData(Element flightLegElement) {
        LegData legData = new LegData();

        Element headerElement = (Element) flightLegElement.getElementsByTagName("HeaderElement").item(0);
        if (headerElement != null) {
            String originTimestampStr = getText(headerElement, "OriginatorTimeStamp");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            try {
                LocalDateTime originTimestamp = LocalDateTime.parse(originTimestampStr, formatter);
                legData.setOriginDate(originTimestamp);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing OriginatorTimeStamp: " + originTimestampStr);
                e.printStackTrace();
            }
        }
        return legData;
    }

    private void processAirportResources(Element flightElement, LegData legData, List<AirportResource> airportResourcesList) {
        if (legData == null) return;

        NodeList resourceList = flightElement.getElementsByTagName("AirportResource");
        for (int i = 0; i < resourceList.getLength(); i++) {
            Element resourceElement = (Element) resourceList.item(i);
            AirportResource resource = new AirportResource();
            resource.setFlightLegID(legData);
            resource.setUsage(getText(resourceElement, "Usage"));
            resource.setDepartureOrArrival(getText(resourceElement, "DepartureOrArrival"));
            airportResourcesList.add(resource);
        }
    }

    private void processOperationTimes(Element flightElement, LegData legData, List<OperationTime> operationTimeList) {
        if (legData == null) return;

        NodeList timeList = flightElement.getElementsByTagName("OperationTime");
        for (int i = 0; i < timeList.getLength(); i++) {
            Element timeElement = (Element) timeList.item(i);
            OperationTime operationTime = new OperationTime();
            operationTime.setFlightLegID(legData);
            operationTime.setOperationQualifier(getText(timeElement, "OperationQualifier"));
            operationTimeList.add(operationTime);
        }
    }

    private String getText(Element element, String tagName) {
        if (element == null) return "";
        NodeList nodeList = element.getElementsByTagName(tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent().trim() : "";
    }
}





























/*package com.aodb.service;

import com.aodb.entity.LegData;
import com.aodb.entity.AirportResource;
import com.aodb.entity.OperationTime;
import com.aodb.repository.LegDataRepository;
import com.aodb.repository.AirportResourceRepository;
import com.aodb.repository.OperationTimeRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class XmlProcessorService {
    
    private final LegDataRepository legDataRepository;
    private final AirportResourceRepository airportResourceRepository;
    private final OperationTimeRepository operationTimeRepository;

    public XmlProcessorService(LegDataRepository legDataRepository,
                               AirportResourceRepository airportResourceRepository,
                               OperationTimeRepository operationTimeRepository) {
        this.legDataRepository = legDataRepository;
        this.airportResourceRepository = airportResourceRepository;
        this.operationTimeRepository = operationTimeRepository;
    }

    @Transactional
    public void processXmlFiles(String folderPath) {
        try {
            List<LegData> legDataList = new ArrayList<>();
            List<AirportResource> airportResourcesList = new ArrayList<>();
            List<OperationTime> operationTimesList = new ArrayList<>();

            Files.walk(Paths.get(folderPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".xml"))
                .forEach(path -> {
                    System.out.println("Processing XML File: " + path);
                    processXmlFile(path.toString(), legDataList, airportResourcesList, operationTimesList);
                });

            if (!legDataList.isEmpty() || !airportResourcesList.isEmpty() || !operationTimesList.isEmpty()) {
                legDataList.sort(Comparator.comparing(LegData::getOriginDate));

                legDataRepository.saveAll(legDataList);
                airportResourceRepository.saveAll(airportResourcesList);
                operationTimeRepository.saveAll(operationTimesList);

                System.out.println("✅ XML Data Inserted Successfully into LegData, AirportResource, and OperationTime tables!");
            } else {
                System.out.println("⚠️ No valid data found to insert!");
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing XML files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void processXmlFile(String filePath, List<LegData> legDataList, List<AirportResource> airportResourcesList, List<OperationTime> operationTimesList) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Enable namespace processing
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            System.out.println("Processing file: " + filePath);

            Element headerElement = (Element) document.getElementsByTagNameNS("*", "YIAPL_Header").item(0);
            if (headerElement == null) {
                System.err.println("⚠️ Missing YIAPL_Header in file: " + filePath);
                return;
            }
            String originTimestampStr = getText(headerElement, "OriginatorTimeStamp");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime originTimestamp = LocalDateTime.parse(originTimestampStr, formatter);

            NodeList flightLegs = document.getElementsByTagNameNS("*", "FlightLeg");
            for (int i = 0; i < flightLegs.getLength(); i++) {
                Element flightLegElement = (Element) flightLegs.item(i);
                Element legIdentifier = (Element) flightLegElement.getElementsByTagNameNS("*", "LegIdentifier").item(0);
                
                if (legIdentifier == null) {
                    System.err.println("⚠️ Skipping FlightLeg with missing LegIdentifier.");
                    continue;
                }

                LegData legData = new LegData();
                legData.setAirlineCode(getText(legIdentifier, "Airline"));
                legData.setFlightNumber(getText(legIdentifier, "FlightNumber"));
                legData.setDepartureAirport(getText(legIdentifier, "DepartureAirport"));
                legData.setArrivalAirport(getText(legIdentifier, "ArrivalAirport"));
                legData.setOriginDate(originTimestamp);
                legData.setInternationalStatus(getText(legIdentifier, "InternationalStatus"));
                legData.setOperationalStatus(getText(legIdentifier, "OperationalStatus"));
                legData.setPublicStatus(getText(legIdentifier, "PublicStatus"));
                legData.setServiceType(getText(legIdentifier, "ServiceType"));
                legData.setRegistration(getText(legIdentifier, "Registration"));
                legData.setTailNumber(getText(legIdentifier, "TailNumber"));
                legData.setAircraftType(getText(legIdentifier, "AircraftType"));
                legData.setAircraftSubType(getText(legIdentifier, "AircraftSubType"));

                processAirportResources(flightLegElement, legData, airportResourcesList);
                processOperationTimes(flightLegElement, legData, operationTimesList);

                legDataList.add(legData);
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing XML file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processAirportResources(Element flightElement, LegData legData, List<AirportResource> airportResourcesList) {
        NodeList airportResourceNodes = flightElement.getElementsByTagNameNS("*", "AirportResource");
    
        for (int j = 0; j < airportResourceNodes.getLength(); j++) {
            Element airportResourceElement = (Element) airportResourceNodes.item(j);
            AirportResource airportResource = new AirportResource();
    
            airportResource.setFlightLegID(legData);
            airportResource.setUsage(getText(airportResourceElement, "Usage"));
            airportResource.setDepartureOrArrival(getText(airportResourceElement, "DepartureOrArrival"));
            airportResource.setPassengerGate(getText(airportResourceElement, "PassengerGate"));
            airportResource.setAircraftTerminal(getText(airportResourceElement, "AircraftTerminal"));
            airportResource.setPublicTerminal(getText(airportResourceElement, "PublicTerminal"));
            airportResource.setCode(getText(airportResourceElement, "Code"));
            
            airportResource.setBaggageBeltCode(getText(airportResourceElement, "BaggageBeltCode"));

            airportResourcesList.add(airportResource);
        }
    }

    private void processOperationTimes(Element flightElement, LegData legData, List<OperationTime> operationTimesList) {
        NodeList operationTimeNodes = flightElement.getElementsByTagNameNS("*", "OperationTime");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        for (int j = 0; j < operationTimeNodes.getLength(); j++) {
            Element operationTimeElement = (Element) operationTimeNodes.item(j);
            OperationTime operationTime = new OperationTime();

            operationTime.setFlightLegID(legData);
            operationTime.setOperationalQualifier(getText(operationTimeElement, "OperationalQualifier"));
            operationTime.setTimeType(getText(operationTimeElement, "TimeType"));

            String operationTimeValueStr = getText(operationTimeElement, "OperationTimeValue");
            if (operationTimeValueStr != null && !operationTimeValueStr.isEmpty()) {
                try {
                    operationTime.setOperationTimeValue(LocalDateTime.parse(operationTimeValueStr, formatter));
                } catch (DateTimeParseException e) {
                    System.err.println("⚠️ Invalid Date Format: " + operationTimeValueStr);
                }
            }
            operationTimesList.add(operationTime);
        }
    }

    private String getText(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        return nodeList.getLength() > 0 ? nodeList.item(0).getTextContent().trim() : null;
    }
}*/

















/*package com.aodb.service;
import com.aodb.entity.LegData;
import com.aodb.entity.AirportResource;
import com.aodb.entity.OperationTime;
import com.aodb.repository.LegDataRepository;
import com.aodb.repository.AirportResourceRepository;
import com.aodb.repository.OperationTimeRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class XmlProcessorService {
    
    private final LegDataRepository legDataRepository;
    private final AirportResourceRepository airportResourceRepository;
    private final OperationTimeRepository operationTimeRepository;

    public XmlProcessorService(LegDataRepository legDataRepository,
                               AirportResourceRepository airportResourceRepository,
                               OperationTimeRepository operationTimeRepository) {
        this.legDataRepository = legDataRepository;
        this.airportResourceRepository = airportResourceRepository;
        this.operationTimeRepository = operationTimeRepository;
    }

    @Transactional
    public void processXmlFiles(String folderPath) {
        try {
            List<LegData> legDataList = new ArrayList<>();
            Files.walk(Paths.get(folderPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".xml"))
                .forEach(path -> legDataList.addAll(processXmlFile(path.toString())));

            if (!legDataList.isEmpty()) {
                legDataList.sort(Comparator.comparing(LegData::getOriginDate));
                legDataRepository.saveAll(legDataList);
                System.out.println("✅ XML Data Inserted Successfully!");
            } else {
                System.out.println("❌ No data found to insert!");
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing XML files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<LegData> processXmlFile(String filePath) {
        List<LegData> legDataList = new ArrayList<>();
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            String originTimestampStr = getText(document, "OriginatorTimeStamp");
            LocalDateTime originTimestamp = LocalDateTime.parse(originTimestampStr, DateTimeFormatter.ISO_DATE_TIME);

            NodeList flightLegs = document.getElementsByTagNameNS("*", "FlightLeg");
            for (int i = 0; i < flightLegs.getLength(); i++) {
                Element flightLegElement = (Element) flightLegs.item(i);
                LegData legData = parseLegData(flightLegElement, originTimestamp);
                processAirportResources(flightLegElement, legData);
                processOperationTimes(flightLegElement, legData);
                legDataList.add(legData);
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing XML file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
        return legDataList;
    }

    private LegData parseLegData(Element flightLegElement, LocalDateTime originTimestamp) {
        Element legIdentifier = (Element) flightLegElement.getElementsByTagNameNS("*", "LegIdentifier").item(0);
        LegData legData = new LegData();
        legData.setOriginDate(originTimestamp);
        legData.setAirlineCode(getText(legIdentifier, "Airline"));
        legData.setFlightNumber(getText(legIdentifier, "FlightNumber"));
        legData.setDepartureAirport(getText(legIdentifier, "DepartureAirport"));
        legData.setArrivalAirport(getText(legIdentifier, "ArrivalAirport"));
        legData.setInternationalStatus(getText(legIdentifier, "InternationalStatus"));
        legData.setOperationalStatus(getText(legIdentifier, "OperationalStatus"));
        legData.setPublicStatus(getText(legIdentifier, "PublicStatus"));
        legData.setServiceType(getText(legIdentifier, "ServiceType"));
        legData.setRegistration(getText(legIdentifier, "Registration"));
        legData.setTailNumber(getText(legIdentifier, "TailNumber"));
        legData.setAircraftType(getText(legIdentifier, "AircraftType"));
        legData.setAircraftSubType(getText(legIdentifier, "AircraftSubType"));
        legData.setAODBFlightID(getText(legIdentifier, "AODBFlightID"));
        return legData;
    }

    private void processAirportResources(Element flightElement, LegData legData) {
        NodeList resourceList = flightElement.getElementsByTagName("AirportResource");
        for (int j = 0; j < resourceList.getLength(); j++) {
            Element resourceElement = (Element) resourceList.item(j);
            AirportResource resource = new AirportResource();
            resource.setFlightLegID(legData);
            resource.setUsage(getText(resourceElement, "Usage"));
            resource.setDepartureOrArrival(getText(resourceElement, "DepartureOrArrival"));
            airportResourceRepository.save(resource);
        }
    }

    private void processOperationTimes(Element flightElement, LegData legData) {
        NodeList timeList = flightElement.getElementsByTagName("OperationTime");
        for (int k = 0; k < timeList.getLength(); k++) {
            Element timeElement = (Element) timeList.item(k);
            OperationTime opTime = new OperationTime();
            opTime.setFlightLegID(legData);
            opTime.setOperationalQualifier(getText(timeElement, "OperationalQualifier"));
            opTime.setTimeType(getText(timeElement, "TimeType"));
            opTime.setOperationTimeValue(LocalDateTime.parse(getText(timeElement, "OperationTimeValue"), DateTimeFormatter.ISO_DATE_TIME));
            operationTimeRepository.save(opTime);
        }
    }

    private String getText(Document document, String tagName) {
        NodeList nodeList = document.getElementsByTagNameNS("*", tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "";
    }

    private String getText(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "";
    }
}
*/
/*package com.aodb.service;

import com.aodb.entity.LegData;
import com.aodb.entity.AirportResource;
import com.aodb.entity.OperationTime;
import com.aodb.repository.LegDataRepository;
import com.aodb.repository.AirportResourceRepository;
import com.aodb.repository.OperationTimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class XmlProcessorService {
    private final LegDataRepository legDataRepository;
    private final AirportResourceRepository airportResourceRepository;
    private final OperationTimeRepository operationTimeRepository;

    public XmlProcessorService(LegDataRepository legDataRepository,
                               AirportResourceRepository airportResourceRepository,
                               OperationTimeRepository operationTimeRepository) {
        this.legDataRepository = legDataRepository;
        this.airportResourceRepository = airportResourceRepository;
        this.operationTimeRepository = operationTimeRepository;
    }

    @Transactional
    public void processXmlFiles(String folderPath) {
        try {
            List<LegData> legDataList = new ArrayList<>();
            Files.walk(Paths.get(folderPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".xml"))
                .forEach(path -> legDataList.addAll(processXmlFile(path.toString())));

            if (!legDataList.isEmpty()) {
                legDataList.sort(Comparator.comparing(LegData::getOriginDate)); // ✅ Sort by timestamp
                legDataRepository.saveAll(legDataList);
                System.out.println("✅ XML Data Inserted Successfully!");
            } else {
                System.out.println("⚠️ No valid data found!");
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing XML files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<LegData> processXmlFile(String filePath) {
        List<LegData> legDataList = new ArrayList<>();
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList flightLegs = document.getElementsByTagName("FlightLeg");
            for (int i = 0; i < flightLegs.getLength(); i++) {
                Element flightLegElement = (Element) flightLegs.item(i);

                LegData legData = new LegData();
                legData.setAirlineCode(getText(flightLegElement, "Airline"));
                legData.setFlightNumber(getText(flightLegElement, "FlightNumber"));
                legData.setDepartureAirport(getText(flightLegElement, "DepartureAirport"));
                legData.setArrivalAirport(getText(flightLegElement, "ArrivalAirport"));
                legData.setOriginDate(parseTimestamp(getText(flightLegElement, "OriginatorTimeStamp")));

                // Insert into DB
                legDataRepository.save(legData);
                legDataList.add(legData);
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing XML: " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
        return legDataList;
    }

    private String getText(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "";
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) return null;
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }
}*/





























// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.w3c.dom.*;
// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import java.io.File;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.List;
// import com.aodb.entity.LegData;
// import com.aodb.entity.AirportResource;
// import com.aodb.entity.OperationTime;
// import com.aodb.repository.LegDataRepository;
// import com.aodb.repository.AirportResourceRepository;
// import com.aodb.repository.OperationTimeRepository;

// import org.springframework.transaction.annotation.Transactional;

// import javax.xml.parsers.*;

// import java.nio.file.*;

// import java.time.format.DateTimeFormatter;
// import java.util.*;

// @Service
// public class XmlProcessorService {

//     @Autowired
//     private LegDataRepository legDataRepository;
    
//     @Autowired
//     private AirportResourceRepository airportResourceRepository;
    
//     @Autowired
//     private OperationTimeRepository operationTimeRepository;

//     public void processXmlFile(String filePath) {
//         try {
//             File xmlFile = new File(filePath);
//             DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//             factory.setNamespaceAware(true);
//             DocumentBuilder builder = factory.newDocumentBuilder();
//             Document document = builder.parse(xmlFile);
//             document.getDocumentElement().normalize();

//             Element headerElement = (Element) document.getElementsByTagNameNS("*", "YIAPL_Header").item(0);
//             String originTimestampStr = getText(headerElement, "OriginatorTimeStamp");
//             LocalDateTime originTimestamp = LocalDateTime.parse(originTimestampStr, DateTimeFormatter.ISO_DATE_TIME);

//             NodeList flightLegs = document.getElementsByTagNameNS("*", "FlightLeg");
//             List<LegData> legDataList = new ArrayList<>();
//             List<AirportResource> airportResources = new ArrayList<>();
//             List<OperationTime> operationTimes = new ArrayList<>();

//             for (int i = 0; i < flightLegs.getLength(); i++) {
//                 Element flightLegElement = (Element) flightLegs.item(i);
//                 Element legIdentifier = (Element) flightLegElement.getElementsByTagNameNS("*", "LegIdentifier").item(0);
//                 Element legDataElement = (Element) flightLegElement.getElementsByTagNameNS("*", "LegData").item(0);
//                 Element tpaExtension = (Element) flightLegElement.getElementsByTagNameNS("*", "TPA_Extension").item(0);
//                 Element airportResourceElement = (Element) flightLegElement.getElementsByTagNameNS("*", "AirportResource").item(0);
//                 Element operationTimeElement = (Element) flightLegElement.getElementsByTagNameNS("*", "OperationTime").item(0);
                
//                 LegData legData = new LegData();
//                 legData.setAirlineCode(getText(legIdentifier, "Airline"));
//                 legData.setFlightNumber(getText(legIdentifier, "FlightNumber"));
//                 legData.setDepartureAirport(getText(legIdentifier, "DepartureAirport"));
//                 legData.setDepartureAirportCodeContext(getAttribute(legIdentifier, "DepartureAirport", "CodeContext"));
//                 legData.setArrivalAirport(getText(legIdentifier, "ArrivalAirport"));
//                 legData.setArrivalAirportCodeContext(getAttribute(legIdentifier, "ArrivalAirport", "CodeContext"));
//                 legData.setOriginDate(LocalDate.parse(getText(legIdentifier, "OriginDate")).atStartOfDay());
//                 legData.setOperationalStatus(getText(legDataElement, "OperationalStatus"));
//                 legData.setAODBFlightID(getText(tpaExtension, "AODBFlightID"));
//                 legDataRepository.save(legData);
                
//                 AirportResource airportResource = new AirportResource();
//                 airportResource.setFlightLegID(legData);
//                 airportResource.setResourceID(getText(airportResourceElement, "ResourceID"));
//                 airportResource.setUsage(getText(airportResourceElement, "Usage"));
//                 airportResource.setAircraftTerminal(getText(airportResourceElement, "AircraftTerminal"));
//                 airportResource.setPassengerGate(getText(airportResourceElement, "PassengerGate"));
//                 airportResource.setBaggageBeltCode(getText(airportResourceElement, "BaggageBeltCode"));
//                 airportResourceRepository.save(airportResource);
                
//                 OperationTime operationTime = new OperationTime();
//                 operationTime.setFlightLegID(legData.getFlightLegID());
//                 operationTime.setOperationalQualifier(getText(operationTimeElement, "OperationalQualifier"));
//                 operationTime.setRepeatIndex(getText(operationTimeElement, "RepeatIndex"));
//                 operationTime.setTimeType(getText(operationTimeElement, "TimeType"));
//                 operationTime.setOperationTimeValue(LocalDateTime.parse(getText(operationTimeElement, "OperationTimeValue"), DateTimeFormatter.ISO_DATE_TIME));

//                 operationTimeRepository.save(operationTime);
//             }

//             System.out.println("✅ Successfully processed and stored data from: " + filePath);
//         } catch (Exception e) {
//             System.err.println("❌ Error processing XML file: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     private String getText(Element parent, String tagName) {
//         if (parent != null) {
//             NodeList list = parent.getElementsByTagNameNS("*", tagName);
//             if (list.getLength() > 0) {
//                 return list.item(0).getTextContent();
//             }
//         }
//         return null;
//     }
    
//     private String getAttribute(Element parent, String tagName, String attributeName) {
//         if (parent != null) {
//             NodeList list = parent.getElementsByTagNameNS("*", tagName);
//             if (list.getLength() > 0) {
//                 Element element = (Element) list.item(0);
//                 return element.getAttribute(attributeName);
//             }
//         }
//         return null;
//     }
// }
