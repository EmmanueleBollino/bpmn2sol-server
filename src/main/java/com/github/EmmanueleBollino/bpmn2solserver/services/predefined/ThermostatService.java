package com.github.EmmanueleBollino.bpmn2solserver.services.predefined;

import com.github.BackCamino.EthereumThermostat.bpmn2sol.translators.ChoreographyTranslator;
import com.github.EmmanueleBollino.bpmn2solserver.services.CompileService;
import com.github.EmmanueleBollino.solcraft.soliditycomponents.SolidityFile;
import org.apache.poi.util.ReplacingInputStream;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ThermostatService {
    private static String THERMOSTAT_MODEL_FILE = "./diagrams/thermostat.bpmn";

    @Autowired
    private CompileService compileService;

    public SolidityFile getThermostatContract(int rooms) throws IOException {
        return new ChoreographyTranslator(getThermostatDiagram(rooms)).translate();
    }

    public BpmnModelInstance getThermostatDiagram(int rooms) throws IOException {
        if(rooms<1) throw new IllegalArgumentException("Rooms must be greater than zero");
        InputStream filteredInputStream=new ReplacingInputStream(new FileInputStream(THERMOSTAT_MODEL_FILE),"___ROOMS___",new Integer(rooms).toString());
        return Bpmn.readModelFromStream(filteredInputStream);
    }

    public String getThermostatCompiled(int rooms) throws InterruptedException, IOException, URISyntaxException {
        return compileService.compile(getThermostatContract(rooms));
    }
}