package com.github.EmmanueleBollino.bpmn2solserver.services;

import com.github.EmmanueleBollino.solcraft.soliditycomponents.SolidityFile;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class CompileService {
    @Value("${solc-server}")
    private String solcServerAddress;

    private String requestCompileBody(SolidityFile solidityFile) {
        String request = "{\n" +
                "                    \"language\": \"Solidity\",\n" +
                "                    \"sources\": {\n" +
                "                        \"" + solidityFile.getFileName() + "\": {\n" +
                "                            \"content\": " + new Gson().toJson(solidityFile.print()) + "\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"settings\": {\n" +
                "                        \"outputSelection\": {\n" +
                "                            \"*\": {\n" +
                "                                \"*\": [\n" +
                "                                    \"evm.bytecode\", \"abi\"\n" +
                "                                ]\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                }";
        System.out.println(request);
        return request;
    }

    public String compile(SolidityFile solidityFile) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(solcServerAddress))
                .header("Content-Type", "application/json")
                .timeout(Duration.of(30, SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(requestCompileBody(solidityFile)))
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
