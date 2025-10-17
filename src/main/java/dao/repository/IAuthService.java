package dao.repository;

import model.Operator;

import java.util.Optional;

/**
 * Service interface for authentication-related business logic.
 * It defines the contract for operations like logging in an operator.
 */
public interface IAuthService {

    /**
     * Attempts to log in an operator with the given username and password.
     * This method contains the business logic for authentication.
     *
     * @param username The operator's username.
     * @param password The operator's plain text password.
     * @return An Optional containing the authenticated Operator if successful, otherwise an empty Optional.
     */
    Optional<Operator> login(String username, String password);
}