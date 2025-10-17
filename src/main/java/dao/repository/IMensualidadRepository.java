package dao.repository;

import java.util.Optional;

public interface IMensualidadRepository {
    /**
     * Checks if there is an active monthly plan for a given plate.
     * @param plate The vehicle's plate.
     * @return An Optional containing a boolean (true if active), otherwise empty.
     */
    Optional<Boolean> isCurrentlyActive(String plate);
}
