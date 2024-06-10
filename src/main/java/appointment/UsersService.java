package appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users loginUser(String email, String password, String roleName) {
        Optional<Users> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword()) && user.getRoleName().equalsIgnoreCase(roleName)) {
                return user;
            }
        }
        return null;
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users getUserById(int id) {
        Optional<Users> optionalUser = usersRepository.findById(id);
        return optionalUser.orElse(null);
    }

    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public Users updateUser(int id, Users newUser) {
        Optional<Users> optionalUser = usersRepository.findById(id);
        if (optionalUser.isPresent()) {
            Users existingUser = optionalUser.get();
            existingUser.setUsername(newUser.getUsername());
            existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            existingUser.setRoleName(newUser.getRoleName());
            return usersRepository.save(existingUser);
        }
        return null;
    }

    public void deleteUser(int id) {
        usersRepository.deleteById(id);
    }
}
