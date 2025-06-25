package edu.ifmg.com.resources;

import edu.ifmg.com.entities.Reservation;
import edu.ifmg.com.services.AccommodationService;
import edu.ifmg.com.services.ClientService;
import edu.ifmg.com.services.ReportService;
import edu.ifmg.com.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/report")
public class ReportResource {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private ReportService reportService;

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

    @GetMapping("/reservations")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<String>> getReservationReport() {
        List<String> report = reservationService.reservationList();

        if (report.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }

    @GetMapping("/invoice/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<String> generateInvoice(@PathVariable Long clientId) {
        try {
            String taxCoupon = reservationService.generateInvoice(clientId);
            return ResponseEntity.ok(taxCoupon);
        } catch (IllegalArgumentException | ResponseStatusException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAll() {
        String message = reportService.deleteAllData();
        return ResponseEntity.ok(message);
    }

    @GetMapping("/highestValue/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<String> getReservationWithHighestAccommodationValue(@PathVariable Long clientId) {
        Optional<Reservation> optionalReservation = reservationService.getReservationWithHighestAccommodationValue(clientId);

        if (optionalReservation.isPresent()) {
            Reservation r = optionalReservation.get();
            String response = String.format(
                    "RELATÓRIO - ESTADIA DE MAIOR VALOR: %s Valor: R$ %.2f",
                    r.getAccommodation().getDescription(), r.getAccommodation().getValue()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok("Não existem reservas cadastradas para este cliente.");
        }
    }

    @GetMapping("/lowerValue/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<String> getReservationWithLowerAccommodationValue(@PathVariable Long clientId) {
        Optional<Reservation> optionalReservation = reservationService.getReservationWithLowerAccommodationValue(clientId);

        if (optionalReservation.isPresent()) {
            Reservation r = optionalReservation.get();
            String response = String.format(
                    "RELATÓRIO - ESTADIA DE MENOR VALOR: %s Valor: R$ %.2f",
                    r.getAccommodation().getDescription(), r.getAccommodation().getValue()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok("Não existem reservas cadastradas para este cliente.");
        }
    }

    @GetMapping("/totalValue/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<String> getTotalReservationValueByClient(@PathVariable Long clientId) {
        Double total = reservationService.getTotalReservationValueByClient(clientId);
        String response = String.format("RELATÓRIO - O TOTAL DAS ESTADIAS DO CLIENTE É: R$ %.2f", total);

        return ResponseEntity.ok(response);
    }
}
