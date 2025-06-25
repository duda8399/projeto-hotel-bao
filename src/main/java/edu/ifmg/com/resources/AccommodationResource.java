package edu.ifmg.com.resources;

import edu.ifmg.com.dto.AccommodationDTO;
import edu.ifmg.com.services.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.net.URI;

@RestController
@RequestMapping(value = "/accommodation")
@Tag(name = "Acomodações", description = "API para gerenciamento de acomodações")
public class AccommodationResource {

    @Autowired
    private AccommodationService accommodationService;

    @Operation(
            summary = "Listar acomodações com paginação",
            description = "Retorna uma lista paginada de acomodações disponíveis.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                            content = @Content(schema = @Schema(implementation = AccommodationDTO.class)))
            }
    )
    @GetMapping
    public ResponseEntity<Page<AccommodationDTO>> findAll(
            @Parameter(description = "Número da página", example = "0") @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Tamanho da página", example = "10") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC") @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @Parameter(description = "Campo para ordenação", example = "id") @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<AccommodationDTO> accommodations = accommodationService.findAll(pageable);
        return ResponseEntity.ok().body(accommodations);
    }

    @Operation(
            summary = "Buscar acomodação por ID",
            description = "Retorna os detalhes de uma acomodação específica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Acomodação encontrada",
                            content = @Content(schema = @Schema(implementation = AccommodationDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Acomodação não encontrada", content = @Content)
            }
    )
    @GetMapping(value = "/{id}")
    public ResponseEntity<AccommodationDTO> findById(
            @Parameter(description = "ID da acomodação", example = "1") @PathVariable Long id) {
        AccommodationDTO accommodation = accommodationService.findById(id);
        return ResponseEntity.ok().body(accommodation);
    }

    @Operation(
            summary = "Criar nova acomodação",
            description = "Cria uma nova acomodação no sistema. Requer permissão de ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Acomodação criada com sucesso",
                            content = @Content(schema = @Schema(implementation = AccommodationDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationDTO> insert(
            @RequestBody(
                    description = "Dados da nova acomodação",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AccommodationDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody AccommodationDTO dto) {
        AccommodationDTO newAccommodation = accommodationService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newAccommodation.getId()).toUri();
        return ResponseEntity.created(uri).body(newAccommodation);
    }

    @Operation(
            summary = "Atualizar acomodação",
            description = "Atualiza os dados de uma acomodação existente. Requer permissão de ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Acomodação atualizada com sucesso",
                            content = @Content(schema = @Schema(implementation = AccommodationDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Acomodação não encontrada", content = @Content)
            }
    )
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationDTO> update(
            @Parameter(description = "ID da acomodação", example = "1") @PathVariable Long id,
            @RequestBody(
                    description = "Novos dados da acomodação",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AccommodationDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody AccommodationDTO dto) {
        AccommodationDTO updatedAccommodation = accommodationService.update(id, dto);
        return ResponseEntity.ok().body(updatedAccommodation);
    }

    @Operation(
            summary = "Deletar acomodação",
            description = "Remove uma acomodação existente. Requer permissão de ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Acomodação deletada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Acomodação não encontrada", content = @Content)
            }
    )
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da acomodação", example = "1") @PathVariable Long id) {
        accommodationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
