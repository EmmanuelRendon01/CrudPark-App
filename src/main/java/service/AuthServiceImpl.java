package service;

import dao.implementation.OperatorRepositoryImpl;
import dao.repository.OperatorRepository;
import dao.repository.IAuthService;
import model.Operator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Implementation of the IAuthService interface.
 * It coordinates data access and business rules for authentication,
 * handling hashed passwords using BCrypt.
 */
public class AuthServiceImpl implements IAuthService {

    private final OperatorRepository operatorRepository;

    /**
     * Default constructor that initializes the repository dependency.
     */
    public AuthServiceImpl() {
        // In a real application, this might be handled by a dependency injection framework.
        this.operatorRepository = new OperatorRepositoryImpl();
    }

    /**
     * Constructor for dependency injection, useful for testing purposes.
     * @param operatorRepository A repository instance, can be a mock.
     */
    public AuthServiceImpl(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    /**
     * Executes the login business logic with hashed password verification.
     * 1. Finds the operator by their email.
     * 2. Checks if the operator exists and is marked as active.
     * 3. Securely verifies if the provided plain-text password matches the stored hash using BCrypt.
     *
     * @param email The operator's email (username).
     * @param password The operator's plain-text password as entered in the login form.
     * @return An Optional containing the Operator if authentication is successful, otherwise an empty Optional.
     */
    @Override
    public Optional<Operator> login(String email, String password) {
        // Step 1: Find the user by their unique email.
        Optional<Operator> operatorOpt = operatorRepository.findByEmail(email);

        // Step 2: Proceed only if a user with that email was found.
        if (operatorOpt.isPresent()) {
            Operator operator = operatorOpt.get();

            // Step 3: Check business rules: user must be active AND the password must be correct.
            // BCrypt.checkpw securely compares the plain-text password with the stored hash.
            if (operator.isActive() && BCrypt.checkpw(password, operator.getPassword())) {
                // Authentication successful.
                return Optional.of(operator);
            }
        }

        // If the user was not found, is not active, or the password was incorrect, return empty.
        return Optional.empty();
    }
}