package vn.hp.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hp.jobhunter.domain.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    boolean existsByEmail(String email);
}
