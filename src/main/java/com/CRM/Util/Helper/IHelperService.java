package com.CRM.Util.Helper;

import java.util.function.Consumer;

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

        String randomCode();

        <T, K> void cleanTrash(JpaRepository<T, K> repository,
                        Specification<T> warningSpec, // Spec cho cảnh báo
                        Specification<T> deleteSpec, // Spec cho xóa vĩnh viễn
                        int warningMinutes, // thời gian thông báo trước
                        String entityName,
                        Consumer<T> actionFunction);
}
