
package appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "purpose")
public class Purpose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int purpose_id;
    private String purpose_name;
    
    // Getters and Setters

    public int getPurpose_id() {
        return purpose_id;
    }

    public void setPurpose_id(int purpose_id) {
        this.purpose_id = purpose_id;
    }

    public String getPurpose_name() {
        return purpose_name;
    }

    public void setPurpose_name(String purpose_name) {
        this.purpose_name = purpose_name;
    }
}
