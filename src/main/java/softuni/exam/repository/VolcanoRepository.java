package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Volcano;

import java.util.Optional;
import java.util.Set;

@Repository
public interface VolcanoRepository extends JpaRepository<Volcano, Long> {

    Optional<Volcano> findByName(String name);

    @Query(value = "FROM Volcano " +
            "WHERE isActive IS TRUE AND elevation > 3000 AND lastEruption IS NOT NULL " +
            "ORDER BY elevation DESC")
    Set<Volcano> findVolcanoesAbove3000m();
}
