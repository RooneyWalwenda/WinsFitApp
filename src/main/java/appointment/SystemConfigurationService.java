package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SystemConfigurationService {

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    public List<SystemConfiguration> getAllSystemConfigurations() {
        return systemConfigurationRepository.findAll();
    }

    public SystemConfiguration getSystemConfigurationById(int id) {
        Optional<SystemConfiguration> optionalSystemConfiguration = systemConfigurationRepository.findById(id);
        return optionalSystemConfiguration.orElse(null);
    }

    public SystemConfiguration createSystemConfiguration(SystemConfiguration systemConfiguration) {
        return systemConfigurationRepository.save(systemConfiguration);
    }

    public SystemConfiguration updateSystemConfiguration(int id, SystemConfiguration newSystemConfiguration) {
        Optional<SystemConfiguration> optionalSystemConfiguration = systemConfigurationRepository.findById(id);
        if (optionalSystemConfiguration.isPresent()) {
            SystemConfiguration existingSystemConfiguration = optionalSystemConfiguration.get();
            existingSystemConfiguration.setBadge_template(newSystemConfiguration.getBadge_template());
            existingSystemConfiguration.setIntegrations(newSystemConfiguration.getIntegrations());
            existingSystemConfiguration.setNotification_preferences(newSystemConfiguration.getNotification_preferences());
            return systemConfigurationRepository.save(existingSystemConfiguration);
        }
        return null;
    }

    public void deleteSystemConfiguration(int id) {
        systemConfigurationRepository.deleteById(id);
    }
}
