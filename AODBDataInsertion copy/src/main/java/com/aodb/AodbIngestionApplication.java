package com.aodb;
import com.aodb.service.XmlProcessingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class AodbIngestionApplication {

    @Autowired
    private XmlProcessingService xmlProcessorService;

    public static void main(String[] args) {
        SpringApplication.run(AodbIngestionApplication.class, args);
    }

    @PostConstruct
    public void run() {
        xmlProcessorService.processXmlFiles("C:\\Users\\Sravya Reddy\\OneDrive\\Desktop\\Jupy\\AODB-AIP Logs\\cleanlogsBackup");
    }
}
