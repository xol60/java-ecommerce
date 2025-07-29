package shopdev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shopdev.entity.UserEntity;
import shopdev.service.UserService;



@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/getAllUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserEntity> getAllUser() {
        return userService.getAllUser();
    }
    @PostMapping("/createUser")
    public UserEntity createUser(@RequestBody UserEntity userEntity) {
        return userService.createUser(userEntity);
    }
    @GetMapping("/findByName")
    public Page<UserEntity> findByName(
        @RequestParam String name,
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction
        ) {
        Sort sortBy = Sort.by(direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sort);
        Pageable pageable = PageRequest.of(page, size, sortBy);
        return userService.findByName(name, pageable);
    }
}
