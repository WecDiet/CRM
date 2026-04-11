package com.CRM.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CRM.model.PatternColor;

public interface IPatternColorRepository extends JpaRepository<PatternColor, UUID> {
    @Query("SELECT pc FROM PatternColor pc WHERE CONCAT(pc.lensColor, '|', COALESCE(pc.frameColor, '')) IN :keys")
    List<PatternColor> findByLensFrameColorKeys(@Param("keys") Set<String> keys);

    @Query("""
        SELECT COUNT(pd) FROM ProductDetail pd
        JOIN pd.colors pc
        WHERE pc.id = :colorId
        AND pd.id NOT IN :excludedProductDetailIds
    """)
    long countByColorIdExcludingProductDetails(
        @Param("colorId") UUID colorId,
        @Param("excludedProductDetailIds") Set<UUID> excludedProductDetailIds
    );

}
