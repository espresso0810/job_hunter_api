package vn.hp.jobhunter.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.domain.User;
import vn.hp.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hp.jobhunter.service.UserService;
import vn.hp.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("users")
    public ResponseEntity<User> createNewUser(@RequestBody User user) {
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException{
        if (id > 100){
            throw new IdInvalidException("Id khong duoc qua 100");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok("Deleted user");
    }

    @GetMapping("users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        User user = this.userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(@RequestParam("current") Optional<String> currentOptional,
                                            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String sCurrent = currentOptional.orElse("");
        String sPageSize = pageSizeOptional.orElse("");
        int current = Integer.parseInt(sCurrent) - 1;
        int pageSize = Integer.parseInt(sPageSize);
        Pageable pageable = PageRequest.of(current, pageSize);
        return ResponseEntity.ok(this.userService.fetchAllUser(pageable));
    }

    @PutMapping("users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.ok(this.userService.updateUser(user));
    }
}
