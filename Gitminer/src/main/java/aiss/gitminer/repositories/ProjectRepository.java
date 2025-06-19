package aiss.gitminer.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import aiss.gitminer.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Page<Project> findByName(String name, Pageable paging);
}
