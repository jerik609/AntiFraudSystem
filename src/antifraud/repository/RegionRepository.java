package antifraud.repository;

import antifraud.enums.RegionType;
import antifraud.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    public Optional<Region> findByRegionType(RegionType regionType);

}
