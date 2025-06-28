package aiss.gitminer.repositories;

import aiss.gitminer.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, String> {
    Page<Issue> findByAuthorId(String authorId, Pageable paging);

    Page<Issue> findByState(String state, Pageable paging);

    Page<Issue> findByStateAndAuthorId(String state, String authorId, Pageable paging);
}
