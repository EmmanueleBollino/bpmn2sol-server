package com.github.EmmanueleBollino.bpmn2solserver.controllers.predefined;

import com.github.EmmanueleBollino.bpmn2solserver.services.predefined.ThermostatService;
import com.github.EmmanueleBollino.solcraft.soliditycomponents.SolidityFile;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/predefined/thermostat")
public class ThermostatController {
    @Autowired
    ThermostatService thermostatService;

    @GetMapping("/contract")
    public Map<String, String> getThermostatContract(@RequestParam int rooms) throws IOException {
        SolidityFile thermostat = thermostatService.getThermostatContract(rooms);
        return Collections.singletonMap(thermostat.getFileName(), thermostat.print());
    }

    @RequestMapping(value = "/diagram", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public String getThermostatDiagram(@RequestParam int rooms) throws IOException {
        return Bpmn.convertToString(thermostatService.getThermostatDiagram(rooms));
    }

    @RequestMapping(value = "/compiled", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getThermostatCompiled(@RequestParam int rooms) throws InterruptedException, IOException, URISyntaxException {
        return thermostatService.getThermostatCompiled(rooms);
    }
}
