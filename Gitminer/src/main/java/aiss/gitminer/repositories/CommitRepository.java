package aiss.gitminer.repositories;

import aiss.gitminer.model.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository  extends JpaRepository<Commit, String> {
}
