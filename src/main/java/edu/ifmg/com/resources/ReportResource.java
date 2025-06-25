package edu.ifmg.com.resources;

import edu.ifmg.com.entities.Reservation;
import edu.ifmg.com.services.AccommodationService;
import edu.ifmg.com.services.ClientService;
import edu.ifmg.com.services.ReportService;
import edu.ifmg.com.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/report")
@Tag(name = "Relatórios", description = "API para consulta de relatórios")
public class ReportResource {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private ReportService reportService;

    @Operation(
            summary = "Relatório de clientes",
            description = "Lista formatada com todos os clientes cadastrados. Requer permissão ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
                    @ApiResponse(responseCode = "204", description = "Nenhum cliente encontrado", content = @Content)
            }
    )
    @GetMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> getClientReport() {
        List<String> report = clientService.customerList();
        return report.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Relatório de acomodações",
            description = "Lista formatada com todas as acomodações cadastradas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
                    @ApiResponse(responseCode = "204", description = "Nenhuma acomodação encontrada", content = @Content)
            }
    )
    @GetMapping("/accommodations")
    public ResponseEntity<List<String>> getAccommodationReport() {
        List<String> report = accommodationService.accommodationList();
        return report.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Relatório de reservas",
            description = "Lista formatada com todas as reservas cadastradas. Requer permissão ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
                    @ApiResponse(responseCode = "204", description = "Nenhuma reserva encontrada", content = @Content)
            }
    )
    @GetMapping("/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> getReservationReport() {
        List<String> report = reservationService.reservationList();
        return report.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Gerar nota fiscal do cliente",
            description = "Gera um cupom fiscal com o total de reservas de um cliente. Requer permissão ADMIN/CLIENT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nota fiscal gerada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro ao gerar nota fiscal", content = @Content)
            }
    )
    @GetMapping("/invoice/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<String> generateInvoice(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long clientId) {
        try {
            String taxCoupon = reservationService.generateInvoice(clientId);
            return ResponseEntity.ok(taxCoupon);
        } catch (IllegalArgumentException | ResponseStatusException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Deletar todos os dados",
            description = "Remove todos os dados de clientes, reservas e acomodações. Requer permissão ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dados deletados com sucesso")
            }
    )
    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAll() {
        String message = reportService.deleteAllData();
        return ResponseEntity.ok(message);
    }

    @Operation(
            summary = "Reserva de maior valor para o cliente",
            description = "Retorna a acomodação mais cara reservada por um cliente. Requer permissão ADMIN/CLIENT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reserva de maior valor retornada")
            }
    )
    @GetMapping("/highestValue/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<String> getReservationWithHighestAccommodationValue(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long clientId) {
        Optional<Reservation> optionalReservation = reservationService.getReservationWithHighestAccommodationValue(clientId);
        return optionalReservation
                .map(r -> ResponseEntity.ok(String.format("RELATÓRIO - ESTADIA DE MAIOR VALOR: %s Valor: R$ %.2f",
                        r.getAccommodation().getDescription(), r.getAccommodation().getValue())))
                .orElseGet(() -> ResponseEntity.ok("Não existem reservas cadastradas para este cliente."));
    }

    @Operation(
            summary = "Reserva de menor valor para o cliente",
            description = "Retorna a acomodação mais barata reservada por um cliente. Requer permissão ADMIN/CLIENT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reserva de menor valor retornada")
            }
    )
    @GetMapping("/lowerValue/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<String> getReservationWithLowerAccommodationValue(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long clientId) {
        Optional<Reservation> optionalReservation = reservationService.getReservationWithLowerAccommodationValue(clientId);
        return optionalReservation
                .map(r -> ResponseEntity.ok(String.format("RELATÓRIO - ESTADIA DE MENOR VALOR: %s Valor: R$ %.2f",
                        r.getAccommodation().getDescription(), r.getAccommodation().getValue())))
                .orElseGet(() -> ResponseEntity.ok("Não existem reservas cadastradas para este cliente."));
    }

    @Operation(
            summary = "Valor total das estadias do cliente",
            description = "Calcula o total gasto em estadias por um cliente. Requer permissão ADMIN/CLIENT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total das estadias retornado")
            }
    )
    @GetMapping("/totalValue/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<String> getTotalReservationValueByClient(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long clientId) {
        Double total = reservationService.getTotalReservationValueByClient(clientId);
        String response = String.format("RELATÓRIO - O TOTAL DAS ESTADIAS DO CLIENTE É: R$ %.2f", total);
        return ResponseEntity.ok(response);
    }
}
