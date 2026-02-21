package com.CRM.repository.Specification;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.CRM.model.Voucher;
import com.CRM.request.Voucher.VoucherFilterRequest;

public class VoucherSpecification {
    public static Specification<Voucher> getAllVoucherFilter(VoucherFilterRequest filter, boolean active){
        return (root, query, criteriaBuilder) ->{

            List<Predicate> predicates = new ArrayList<>();
            LocalDateTime startDate = null;
            LocalDateTime expirationDate = null;

            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filter.getName().trim().toLowerCase() + "%"));
            }

            if(filter.getCode() != null && !filter.getCode().trim().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + filter.getCode().trim() + "%"));
            }

            if (filter.getDiscount() != null) {
                predicates.add(criteriaBuilder.equal(root.get("discount"), filter.getDiscount()));
            }

            if (filter.getDiscountType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("discountType"), filter.getDiscountType()));
            }

            if (filter.getStartDay() != null || filter.getStartMonth() != null || filter.getStartYear() != null) {
                if (isValidDate(filter.getStartDay(), filter.getStartMonth(), filter.getStartYear())) {
                    startDate = startOfDate(filter.getStartDay(), filter.getStartMonth(), filter.getStartYear());

                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate));
                }
            }

            if (filter.getEndDay() != null || filter.getEndMonth() != null || filter.getEndYear() != null) {
                if (isValidDate(filter.getEndDay(), filter.getEndMonth(), filter.getEndYear())) {
                    expirationDate = expirationOfDate(filter.getEndDay(), filter.getEndMonth(), filter.getEndYear());

                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expirationDate"), expirationDate));
                }
            }
            
            predicates.add(
                criteriaBuilder.equal(root.get("isGlobal"), filter.getIsGlobal())
            );

            predicates.add(criteriaBuilder.equal(root.get("inActive"), active));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deletedAt"), 0L));

            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }

    private static LocalDateTime startOfDate(Integer startDay, Integer startMonth, Integer startYear) {
        return LocalDateTime.of(startYear, startMonth, startDay, 0, 0, 0);
    }

    private static LocalDateTime expirationOfDate(Integer endDay, Integer endMonth, Integer endYear){
        return LocalDateTime.of(endYear, endMonth, endDay, 23, 59, 59);
    }

    private static boolean isValidDate(Integer day, Integer month, Integer year){
        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
