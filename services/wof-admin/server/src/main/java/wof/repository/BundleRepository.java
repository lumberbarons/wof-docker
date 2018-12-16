package wof.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wof.repository.model.BundleEntity;

@Repository
public interface BundleRepository extends JpaRepository<BundleEntity, String> {

}
