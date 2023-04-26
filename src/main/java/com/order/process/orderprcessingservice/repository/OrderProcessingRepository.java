package com.order.process.orderprcessingservice.repository;

import com.order.process.orderprcessingservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OrderProcessingRepository extends JpaRepository<Order,Long>{
    Long countByCountry(String country);

    @Query(value = "select sum(parcel_weight) from orders where country = :country",nativeQuery = true)
    Double findTotalWeightByCountry(@Param("country") String country);

    @Query("SELECT o FROM Order o WHERE " +
            "(:id is null or o.id = :id) " +
            "and (:email is null or o.email = :email) " +
            "and (:phoneNumber is null or o.phoneNumber = :phoneNumber) " +
            "and (:parcelWeight is null or o.parcelWeight = :parcelWeight) " +
            "and (:country is null or o.country = :country) " +
            "and (cast(:creationDate as Date) is null or o.creationDate = :creationDate)")
    Page<Order> findOrderByOptionalParameter(
            @Param("id") Long id,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("parcelWeight") Double parcelWeight,
            @Param("country") String country,
            @Param("creationDate") LocalDate creationDate,
            Pageable pageable);


}
