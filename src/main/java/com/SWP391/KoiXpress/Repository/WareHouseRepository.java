package com.SWP391.KoiXpress.Repository;
import com.SWP391.KoiXpress.Entity.WareHouses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouses, Long> {
    WareHouses findWaresHouseById(long id);

    @Query("SELECT w.location FROM WareHouses w WHERE w.isAvailable = true")
    List<String> findAllAvailableLocations();

    @Query("SELECT w.location FROM WareHouses w WHERE w.isAvailable = true AND w.id <> :wareHouseId")
    List<String> findAllAvailableLocationsExcludingCurrent(long wareHouseId);


    WareHouses findWareHousesByLocation(String nearWareHouse);

    List<WareHouses> findByIsAvailableTrue();

    List<WareHouses> findByIsAvailableFalse();
}
