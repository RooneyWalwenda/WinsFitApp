package appointment;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userid;
    private String username;
    private String password;
    private String roleName;
    private String email;
    private Long institutionId;
    private String institutionName;
    private boolean defaultPassword; // Add this field

    public boolean isDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(boolean defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();


    // Constructor with email and role
    public Users(String email, UserRole role) {
        this.email = email;
        this.roleName = role.name();
    }

    // Getters and Setters
    public int getUserid() {
        return userid;
    }
    public Users() {
        // Default constructor
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    // ✅ Add Getter Method for `id`
    public int getId() {
        return userid;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public UserRole getRole() {
        // Logic to convert the roleName to UserRole enum
        if (roleName != null) {
            return UserRole.valueOf(roleName.toUpperCase());
        }
        return null;
    }

    public void setRole(UserRole role) {
        this.roleName = role.name(); // Assuming roleName is the field representing the role
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }
}
