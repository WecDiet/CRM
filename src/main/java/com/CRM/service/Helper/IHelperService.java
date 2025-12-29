package com.CRM.service.Helper;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.CRM.model.BaseEntity;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface IHelperService<T extends BaseEntity, K> {
        <DTO> PagingResponse<DTO> getAll(
                        int page,
                        int limit,
                        String sortBy,
                        String direction,
                        Specification<T> specification,
                        Class<DTO> dtoClass,
                        JpaRepository<T, K> repository);

        <RES> APIResponse<RES> getById(K id, JpaRepository<T, K> repository, Class<T> entityClass,
                        Class<RES> responseClass);
}
