package appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_configuration")
public class SystemConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int configuration_id;
    private String badge_template;
    private String integrations;
    private String notification_preferences;

    // Getters and Setters
    public int getConfiguration_id() {
        return configuration_id;
    }

    public void setConfiguration_id(int configuration_id) {
        this.configuration_id = configuration_id;
    }

    public String getBadge_template() {
        return badge_template;
    }

    public void setBadge_template(String badge_template) {
        this.badge_template = badge_template;
    }

    public String getIntegrations() {
        return integrations;
    }

    public void setIntegrations(String integrations) {
        this.integrations = integrations;
    }

    public String getNotification_preferences() {
        return notification_preferences;
    }

    public void setNotification_preferences(String notification_preferences) {
        this.notification_preferences = notification_preferences;
    }
}
