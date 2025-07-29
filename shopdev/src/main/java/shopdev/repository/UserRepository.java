package shopdev.repository;



import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import shopdev.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public UserEntity findByUsernameAndEmail(String username, String email);
    public Page<UserEntity> findByName(String name, Pageable pageable);
    
    Optional<UserEntity> findByUsername(String username);
    @Modifying
    @Query("update UserEntity u set u.password = :password where u.username = :username")
    @Transactional
    public void updatePassword(@Param("password") String password,@Param("username") String username);
    
}