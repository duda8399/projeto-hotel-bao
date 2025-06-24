package edu.ifmg.com.resources;

import edu.ifmg.com.services.AccommodationService;
import edu.ifmg.com.services.ClientService;
import edu.ifmg.com.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/report")
public class ReportResource {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccommodationService accommodationService;

    @GetMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<String>> getClientReport() {
        List<String> report = clientService.customerList();

        if (report.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }

    @GetMapping("/accommodations")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<String>> getAccommodationReport() {
        List<String> report = accommodationService.accommodationList();

        if (report.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }
}
