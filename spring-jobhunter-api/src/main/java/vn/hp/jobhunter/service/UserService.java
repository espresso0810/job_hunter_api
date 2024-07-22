package vn.hp.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hp.jobhunter.domain.User;
import vn.hp.jobhunter.domain.response.*;
import vn.hp.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ResultPaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable){
        Page<User> userPage = this.userRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data like password
        List<ResUserDTO> listUser = userPage.getContent()
                .stream().map(item-> new ResUserDTO(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getUpdatedAt(),
                        item.getCreatedAt()))
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    public User updateUser(User reqUser){
//        return this.userRepository.findById(updatedUser.getId()).map(existingUser ->{
//            existingUser.setName(updatedUser.getName());
//            existingUser.setPassword(updatedUser.getPassword());
//            existingUser.setEmail(updatedUser.getEmail());
//            return this.userRepository.save(existingUser);
//        }).orElse(null);
        User currentUser = this.getUserById(reqUser.getId());
        if (currentUser != null){
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setName(reqUser.getName());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User getUserByUsername(String username){
        return this.userRepository.findByEmail(username);
    }

    public boolean isExistedEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateDTO(User user){
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName (user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender (user.getGender());
        res.setAddress (user.getAddress());
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user){
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName (user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt (user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender (user.getGender());
        res.setAddress (user.getAddress());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user){
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName (user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender (user.getGender());
        res.setAddress (user.getAddress());
        return res;
    }

    public void updateUserToken(String token, String email){
        User currentUser = this.getUserByUsername(email);
        if (currentUser != null){
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRFTokenAndEmail(String email, String token){
        return this.userRepository.findByEmailAndRefreshToken(email, token);
    }
}
