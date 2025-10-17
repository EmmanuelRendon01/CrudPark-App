package dao.repository;

import model.Operator;

import java.util.Optional;

public interface OperatorRepository {

    /**
     * Finds an operator by their username.
     * This method is crucial for the login process.
     *
     * @param username The username of the operator to find.
     * @return An Optional containing the found Operator if they exist, otherwise an empty Optional.
     */
    Optional<Operator> findByUsername(String username);

    // Other methods like save, update, findById, etc., could be added here later.
}

