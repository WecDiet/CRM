package com.CRM.service.HelperService;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.CRM.model.EntityBase;
import com.CRM.response.Pagination.PageResponse;

public interface IHelperService<T extends EntityBase, K> {
    <DTO> PageResponse<DTO> getAll(
            int page,
            int limit,
            String sortBy,
            String direction,
            Specification<T> specification,
            Class<DTO> dtoClass,
            JpaRepository<T, K> repository);
}
