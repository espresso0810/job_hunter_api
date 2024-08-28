package vn.hp.jobhunter.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hp.jobhunter.domain.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    boolean existsByName(String name);
    Role findByName(String superAdmin);
}
