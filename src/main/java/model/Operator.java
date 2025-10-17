package model;

/**
 * Represents the Operator entity.
 * This class is a plain old Java object (POJO) that maps to the 'Operadores' table in the database.
 */
public class Operator {

    private int id;
    private String username;
    private String password; // In a real-world scenario, this should be a hash.
    private String fullName;
    private String email;
    private boolean active;

    // Constructors
    public Operator() {
    }

    public Operator(int id, String username, String password, String fullName, String email, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.active = active;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}