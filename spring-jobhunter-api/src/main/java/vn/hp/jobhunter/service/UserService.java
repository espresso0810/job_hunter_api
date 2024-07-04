package vn.hp.jobhunter.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hp.jobhunter.domain.User;
import vn.hp.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder pe) {
        this.userRepository = userRepository;
        this.passwordEncoder = pe;
    }

    public User handleCreateUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public void deleteUser(long id){
        this.userRepository.deleteById(id);
    }

    public User getUserById(long id){
        Optional<User> userOptional= this.userRepository.findById(id);
        return userOptional.orElse(null);
    }

    public List<User> fetchAllUser(){
        return this.userRepository.findAll();
    }

    public User updateUser(User updatedUser){
//        return this.userRepository.findById(updatedUser.getId()).map(existingUser ->{
//            existingUser.setName(updatedUser.getName());
//            existingUser.setPassword(updatedUser.getPassword());
//            existingUser.setEmail(updatedUser.getEmail());
//            return this.userRepository.save(existingUser);
//        }).orElse(null);
        User currentUser = this.getUserById(updatedUser.getId());
        if (currentUser != null){
            currentUser.setEmail(updatedUser.getEmail());
            currentUser.setName(updatedUser.getName());
            currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User getUserByUsername(String username){
        return this.userRepository.findByEmail(username);
    }
}
