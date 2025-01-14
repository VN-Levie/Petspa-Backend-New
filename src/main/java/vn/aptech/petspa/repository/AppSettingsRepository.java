package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.aptech.petspa.entity.AppSettings;
import vn.aptech.petspa.entity.SpaServiceSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
    Optional<AppSettings> findByKey(String key);
}
