package shopdev.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import shopdev.entity.UserEntity;

public interface UserService {

    UserEntity createUser(UserEntity userEntity);
    List<UserEntity> getAllUser();
    Page<UserEntity> findByName(String name, Pageable pageable);
    public UserDetails loadUserByUsername(String username);
}