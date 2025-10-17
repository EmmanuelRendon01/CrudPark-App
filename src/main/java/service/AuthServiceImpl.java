package service;

import dao.implementation.OperatorRepositoryImpl;
import dao.repository.OperatorRepository;
import model.Operator;
import dao.repository.IAuthService;

import java.util.Optional;

/**
 * Implementation of the IAuthService interface.
 * It coordinates data access and business rules for authentication.
 */
public class AuthServiceImpl implements IAuthService {

    private final OperatorRepository operatorRepository;

    /**
     * Constructor that injects the repository dependency.
     * In a real application, this would be handled by a dependency injection framework.
     */
    public AuthServiceImpl() {
        // Manual dependency injection for now.
        this.operatorRepository = new OperatorRepositoryImpl();
    }

    // This constructor is better for testing, as it allows mocking the repository.
    public AuthServiceImpl(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }


    /**
     * Executes the login business logic.
     * 1. Finds the operator by username.
     * 2. Checks if the operator exists and is active.
     * 3. Verifies if the provided password matches the stored one.
     *
     * @param username The operator's username.
     * @param password The operator's plain text password.
     * @return An Optional<Operator> if authentication is successful.
     */
    @Override
    public Optional<Operator> login(String username, String password) {
        // Step 1: Use the repository to find the user by username.
        Optional<Operator> operatorOpt = operatorRepository.findByUsername(username);

        // Check if the operator exists.
        if (operatorOpt.isPresent()) {
            Operator operator = operatorOpt.get();

            // Step 2 & 3: Apply business rules.
            // In a real system, passwords should be hashed. Here we do a simple string comparison.
            // Example with hashing would be: passwordEncoder.matches(password, operator.getPassword())
            if (operator.isActive() && operator.getPassword().equals(password)) {
                return Optional.of(operator); // Authentication successful.
            }
        }

        // If any check fails, return empty.
        return Optional.empty();
    }
}
