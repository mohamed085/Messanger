package com.messenger.repository;

import com.messenger.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * \n" +
            "FROM user As user\n" +
            "LEFT JOIN user_requests As requests ON requests.user_id = user.id AND requests.requests_id = ?1\n" +
            "LEFT JOIN user_requests AS requseted ON requseted.requests_id = user.id AND requseted.requests_id = ?1\n" +
            "LEFT JOIN user_friends AS friend ON friend.friend_id = user.id AND friend.user_id = ?1\n" +
            "WHERE requests.user_id IS null AND requseted.requests_id IS NULL AND friend.user_id IS NULL AND user.id <> ?1",
            countQuery = "SELECT count(*) FROM USER",
            nativeQuery = true)
    List<User> discoverNewUsers(Long id);
}
