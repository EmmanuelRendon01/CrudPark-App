package dao.repository;

import model.Estancia;

import java.util.List;
import java.util.Optional;

public interface IEstanciaRepository {
    /**
     * Saves a new stay record to the database.
     * @param estancia The Estancia object to save.
     * @return The saved Estancia, now with the generated ID.
     */
    Estancia save(Estancia estancia);

    /**
     * Finds an active stay by plate number.
     * @param plate The vehicle's plate.
     * @return An Optional containing the Estancia if found, otherwise empty.
     */
    Optional<Estancia> findActiveByPlate(String plate);

    /**
     * Finds all stays that are currently active (status 'DENTRO').
     * @return A list of active Estancia objects.
     */
    List<Estancia> findAllActive();

    void update(Estancia estancia);
}