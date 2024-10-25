package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Boxes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoxRepository extends JpaRepository<Boxes, Long> {
    Boxes findBoxesByType(String boxType);

    Boxes findBoxesById(long id);
}
