package com.example.cards.repository;

import com.example.cards.model.Card;
import com.example.cards.model.StatusEnum;
import com.example.cards.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {
    @Query("SELECT c FROM Card c " +
            "WHERE (:name IS NULL OR c.name = :name) " +
            "AND (:color IS NULL OR c.color = :color) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:createdDate IS NULL OR c.createdDate = :createdDate) " +
            "AND c.insertedBy = :user")
    Page<List<Card>> findByInsertedByAndFilters(User user, String name, String color, StatusEnum status, String createdDate, Pageable pageable);
    Page<List<Card>> findByInsertedBy(User user, Pageable pageable);
//    @Query(value = "SELECT * FROM cards c " +
//            "WHERE (c.name LIKE CONCAT('%', :name, '%') OR :name IS NULL) " +
//            "AND (c.color LIKE CONCAT('%', :color, '%') OR :color IS NULL) " +
//            "AND (c.status LIKE CONCAT('%', :status, '%') OR :status IS NULL) " +
//            "AND (c.createdDate LIKE CONCAT('%', :createdDate, '%') OR :createdDate IS NULL) " +
//            "ORDER BY c.name DESC " +
//            "LIMIT :size", nativeQuery = true)

    @Query("SELECT c FROM Card c " +
            "WHERE (:name IS NULL OR c.name = :name) " +
            "AND (:color IS NULL OR c.color = :color) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:createdDate IS NULL OR c.createdDate = :createdDate)")
    Page<List<Card>> findAllByNameAndColorAndStatusAndCreatedDate(String name, String color, StatusEnum status, String createdDate,
                                                                  Pageable pageable);

    boolean existsByCardCode(String uniqueCode);

    boolean existsByCardCodeAndCardIDNot(String cardCode, Long cardID);
}
