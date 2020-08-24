package com.github.EmmanueleBollino.bpmn2solserver.controllers;

import com.github.BackCamino.EthereumThermostat.bpmn2sol.translators.ChoreographyTranslator;
import com.github.EmmanueleBollino.solcraft.soliditycomponents.SolidityFile;
import com.github.EmmanueleBollino.solcraft.solidityhelpers.SolidityFileSplitter;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/translator")
public class TranslatorController {
    /**
     * Translates a BPMN choreography model into Solidity contracts.
     *
     * @param model          model to translate
     * @param splitContracts if <code>true</code> the generated contracts will be split in separated files; if <code>false</code> only a solidity file will be generated.
     * @return The output is in JSON format. A list whose keys are the name of the solidity files and values are the relative contract code.
     * All codes will be returned already formatted.
     */
    @GetMapping("/choreography")
    public Map<String, String> translateChoreographyModel(@RequestAttribute MultipartFile model, @RequestParam(required = false, defaultValue = "false") boolean splitContracts) {
        ChoreographyTranslator translator;
        try {
            translator = new ChoreographyTranslator(Bpmn.readModelFromStream(model.getInputStream()));
        } catch (Exception e) {
            throw new IllegalArgumentException("This model can't be translated");
        }


        SolidityFile translated = translator.translate();
        if (splitContracts)
            return SolidityFileSplitter.splitSolidityFile(translated).stream().collect(Collectors.toMap(SolidityFile::getFileName, SolidityFile::print));
        else
            return Collections.singletonMap(translated.getFileName(), translated.print());
    }
}
