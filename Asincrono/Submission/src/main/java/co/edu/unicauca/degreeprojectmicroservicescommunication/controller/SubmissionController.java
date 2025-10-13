package co.edu.unicauca.degreeprojectmicroservicescommunication.controller;

import co.edu.unicauca.degreeprojectmicroservicescommunication.dto.AnteproyectoRequest;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Anteproyecto;
import co.edu.unicauca.degreeprojectmicroservicescommunication.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/anteproyecto")
    public ResponseEntity<?> createAnteproyecto(@RequestBody AnteproyectoRequest request) {
        try {
            Anteproyecto saved = submissionService.crearAnteproyecto(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
