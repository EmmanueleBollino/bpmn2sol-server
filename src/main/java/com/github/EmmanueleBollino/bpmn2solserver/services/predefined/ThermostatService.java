package com.github.EmmanueleBollino.bpmn2solserver.services.predefined;

import com.github.BackCamino.EthereumThermostat.bpmn2sol.translators.ChoreographyTranslator;
import com.github.EmmanueleBollino.bpmn2solserver.services.CompileService;
import com.github.EmmanueleBollino.solcraft.soliditycomponents.*;
import org.apache.poi.util.ReplacingInputStream;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class ThermostatService {
    private static String THERMOSTAT_MODEL_FILE = "diagrams/thermostat.bpmn";

    @Autowired
    private CompileService compileService;

    public SolidityFile getThermostatContract(int rooms) {
        if (rooms == 1) {
            SolidityFile translated = new ChoreographyTranslator(getThermostatDiagram(2)).translate();
            Contract thermostat = (Contract) translated.getComponents().stream().filter(el -> el instanceof Contract).map(el -> (Contract) el).filter(el -> el.getName().equals("Thermostat")).findFirst().orElseThrow();

            List<Statement> constructorStatements = thermostat.getConstructor().getStatements().subList(0, 3);
            constructorStatements.add(new Statement("isReady();"));
            Constructor newConstructor = new Constructor("Thermostat", Visibility.PUBLIC, List.of(), constructorStatements);
            thermostat.setConstructor(newConstructor);

            thermostat.getAttributes().stream().filter(el -> el.getName().equals("associations")).findFirst().orElseThrow().setType(new Type("Association[1]"));
            thermostat.getAttributes().stream().filter(el -> el.getName().equals("heaterValues")).findFirst().orElseThrow().setType(new Type("HeaterValues[1]"));
            thermostat.getAttributes().stream().filter(el -> el.getName().equals("sensorValues")).findFirst().orElseThrow().setType(new Type("SensorValues[1]"));

            return translated;
        } else return new ChoreographyTranslator(getThermostatDiagram(rooms)).translate();
    }

    public BpmnModelInstance getThermostatDiagram(int rooms) {
        if (rooms < 1) throw new IllegalArgumentException("Rooms must be greater than zero");

        //building association string
        StringBuilder associations = new StringBuilder();
        for (int i = 0; i < rooms; i++) {
            associations.append("[").append(i).append(",").append(i).append("]").append(";");
        }
        associations.setLength(associations.length() - 1);

        //replacing parameters in diagram
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(THERMOSTAT_MODEL_FILE);
        InputStream filteredInputStreamRooms = new ReplacingInputStream(resourceAsStream, "___ROOMS___", Integer.toString(rooms));
        InputStream filteredInputStreamAssociations = new ReplacingInputStream(filteredInputStreamRooms, "___ASSOCIATIONS___", associations.toString());

        return Bpmn.readModelFromStream(filteredInputStreamAssociations);
    }

    public String getThermostatCompiled(int rooms) throws InterruptedException, IOException, URISyntaxException {
        return compileService.compile(getThermostatContract(rooms));
    }
}