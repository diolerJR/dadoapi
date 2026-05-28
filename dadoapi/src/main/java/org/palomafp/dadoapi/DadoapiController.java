package org.palomafp.dadoapi;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/dadoapi")
@CrossOrigin(origins = "*")
public class DadoapiController {

    @GetMapping("/playround")
    public Map<String, Object> playRound(
            @RequestParam(value = "prediccion", defaultValue = "") String prediccion,
            @RequestParam(value = "dadoActual", defaultValue = "1") int dadoActual) {

        String userPrediction = normalizePrediction(prediccion);
        Map<String, Object> response = new HashMap<>();

        if (!isValidPrediction(userPrediction)) {
            response.put("mensaje", "Escribe una prediccion valida: mayor o menor.");
            response.put("estado", "error");
            response.put("dadoActual", dadoActual);
            return response;
        }

        int nextDice = randomDice();
        boolean isEqual = nextDice == dadoActual;
        boolean isCorrect = "mayor".equals(userPrediction)
                ? nextDice > dadoActual
                : nextDice < dadoActual;

        String mensaje;
        if (isEqual) {
            mensaje = "Salio un " + nextDice + ". Empate: no cuenta como acierto.";
        } else if (isCorrect) {
            mensaje = "Salio un " + nextDice + ". Acertaste!";
        } else {
            mensaje = "Salio un " + nextDice + ". Fallaste.";
        }

        response.put("mensaje", mensaje);
        response.put("estado", "success");
        response.put("dadoActual", nextDice);
        response.put("dadoAnterior", dadoActual);
        response.put("prediccion", userPrediction);
        return response;
    }

    private String normalizePrediction(String prediction) {
        if (prediction == null) {
            return "";
        }

        String normalized = Normalizer.normalize(prediction.trim().toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    private boolean isValidPrediction(String prediction) {
        return "mayor".equals(prediction) || "menor".equals(prediction);
    }

    private int randomDice() {
        return ThreadLocalRandom.current().nextInt(1, 7);
    }
}
