
package com.aodb.controller;

import com.aodb.service.XmlProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xml")
public class XmlProcessingController {

    @Autowired
    private XmlProcessingService xmlProcessorService;

    @GetMapping("/test")
    public String testApi() {
        return "API is working!";
    }

    @PostMapping("/process")
    public String processXmlFiles() {
        String directoryPath = "C:\\Users\\Sravya Reddy\\OneDrive\\Desktop\\Jupy\\AODB-AIP Logs\\cleaned_logs";
        xmlProcessorService.processXmlFiles(directoryPath);
        return "XML files processed successfully!";
    }
}
