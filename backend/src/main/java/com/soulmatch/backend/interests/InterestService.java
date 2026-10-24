
package com.soulmatch.backend.interests;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class InterestService {

    private final InterestRepository interestRepository;

    public InterestService(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    /**
     * Obtiene todos los intereses activos.
     */
    @Transactional(readOnly = true)
    public List<InterestResponse> getAllActiveInterests() {
        return interestRepository.findAllByActiveTrue()
                .stream()
                .map(InterestResponse::fromEntity)
                .toList();
    }

    /**
     * Obtiene un interés activo por su identificador.
     */
    @Transactional(readOnly = true)
    public InterestResponse getInterestById(UUID id) {
        Interest interest = findActiveInterestById(id);
        return InterestResponse.fromEntity(interest);
    }

    /**
     * Busca intereses activos por nombre.
     */
    @Transactional(readOnly = true)
    public List<InterestResponse> searchInterests(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre de búsqueda es obligatorio"
            );
        }

        return interestRepository
                .findAllByNameContainingIgnoreCaseAndActiveTrue(name.trim())
                .stream()
                .map(InterestResponse::fromEntity)
                .toList();
    }

    /**
     * Filtra intereses activos por categoría.
     */
    @Transactional(readOnly = true)
    public List<InterestResponse> getInterestsByCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría es obligatoria"
            );
        }

        return interestRepository
                .findAllByCategoryIgnoreCaseAndActiveTrue(category.trim())
                .stream()
                .map(InterestResponse::fromEntity)
                .toList();
    }

    /**
     * Crea un nuevo interés.
     */
    @Transactional
    public InterestResponse createInterest(InterestRequest request) {
        validateRequest(request);

        String normalizedName = normalizeName(request.name());

        if (interestRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un interés con ese nombre"
            );
        }

        Interest interest = new Interest();
        applyRequest(interest, request);
        interest.setActive(true);

        try {
            Interest savedInterest = interestRepository.saveAndFlush(interest);
            return InterestResponse.fromEntity(savedInterest);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo crear el interés porque ya existe un registro con datos únicos"
            );
        }
    }

    /**
     * Actualiza un interés existente.
     */
    @Transactional
    public InterestResponse updateInterest(UUID id, InterestRequest request) {
        validateRequest(request);

        Interest interest = findInterestById(id);
        String normalizedName = normalizeName(request.name());

        interestRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Ya existe otro interés con ese nombre"
                    );
                });

        applyRequest(interest, request);

        try {
            Interest updatedInterest = interestRepository.saveAndFlush(interest);
            return InterestResponse.fromEntity(updatedInterest);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo actualizar el interés por un conflicto de datos"
            );
        }
    }

    /**
     * Desactiva un interés sin eliminarlo de la base de datos.
     */
    @Transactional
    public void deactivateInterest(UUID id) {
        Interest interest = findInterestById(id);
        interest.setActive(false);
        interestRepository.save(interest);
    }

    /**
     * Busca un interés existente, esté activo o no.
     */
    private Interest findInterestById(UUID id) {
        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador del interés es obligatorio"
            );
        }

        return interestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el interés solicitado"
                ));
    }

    /**
     * Busca un interés activo por su identificador.
     */
    private Interest findActiveInterestById(UUID id) {
        Interest interest = findInterestById(id);

        if (!Boolean.TRUE.equals(interest.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró el interés solicitado"
            );
        }

        return interest;
    }

    /**
     * Valida los datos recibidos.
     */
    private void validateRequest(InterestRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos del interés son obligatorios"
            );
        }

        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del interés es obligatorio"
            );
        }

        if (request.name().trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede superar los 100 caracteres"
            );
        }

        if (request.description() != null
                && request.description().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción no puede superar los 500 caracteres"
            );
        }

        if (request.category() != null
                && request.category().length() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría no puede superar los 50 caracteres"
            );
        }

        if (request.icon() != null && request.icon().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El icono no puede superar los 100 caracteres"
            );
        }
    }

    /**
     * Copia los datos de la solicitud a la entidad.
     */
    private void applyRequest(Interest interest, InterestRequest request) {
        interest.setName(normalizeName(request.name()));
        interest.setDescription(normalizeOptionalText(request.description()));
        interest.setCategory(normalizeOptionalText(request.category()));
        interest.setIcon(normalizeOptionalText(request.icon()));
    }

    private String normalizeName(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
