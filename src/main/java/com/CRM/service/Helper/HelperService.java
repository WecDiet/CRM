package com.CRM.service.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CRM.model.BaseEntity;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Pagination.Info.PaginationInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class HelperService<T extends BaseEntity, K> implements IHelperService<T, K> {

        @Autowired
        private ModelMapper modelMapper;

        // Repository cụ thể sẽ được inject bởi class con
        @Autowired
        protected JpaRepository<T, K> repository;

        // Cache cho Sort objects để tránh tạo mới mỗi lần
        private static final Map<String, Sort> SORT_CACHE = new ConcurrentHashMap<>();

        @Override
        public <DTO> PagingResponse<DTO> getAll(
                        int page,
                        int limit,
                        String sortBy,
                        String direction,
                        Specification<T> specification,
                        Class<DTO> dtoClass,
                        JpaRepository<T, K> repository) {

                validateInputs(page, limit, sortBy);
                Sort sort = getCachedSort(sortBy, direction);
                Pageable pageable = PageRequest.of(page, limit, sort);
                Page<T> entityPage;
                if (specification != null) {
                        if (repository instanceof JpaSpecificationExecutor) {
                                entityPage = ((JpaSpecificationExecutor<T>) repository).findAll(
                                                specification,
                                                pageable);
                        } else {
                                throw new IllegalArgumentException(
                                                "Repository must extend JpaSpecificationExecutor to use Specification");

                        }
                } else {
                        entityPage = repository.findAll(pageable);
                }
                // Lazy mapping - chỉ map khi cần
                List<DTO> dtoList = mapEntitiesToDtos(entityPage.getContent(), dtoClass);
                // Build PaginationInfo
                PaginationInfo paginationInfo = new PaginationInfo(
                                entityPage.getNumber(), // currentPage
                                entityPage.getSize(), // pageSize
                                entityPage.getTotalElements(), // totalElements
                                entityPage.getTotalPages(), // totalPages
                                entityPage.isFirst(), // first
                                entityPage.isLast(), // last
                                entityPage.hasNext(), // hasNext
                                entityPage.hasPrevious() // hasPrevious
                );
                return new PagingResponse<>(
                                true, // success
                                "Get data successfully", // message
                                dtoList, // data
                                paginationInfo // pagination
                );
        }

        /**
         * Cache Sort objects
         */
        private Sort getCachedSort(String sortBy, String direction) {
                String key = sortBy + "_" + direction;
                return SORT_CACHE.computeIfAbsent(key, k -> "desc".equalsIgnoreCase(direction)
                                ? Sort.by(sortBy).descending()
                                : Sort.by(sortBy).ascending());
        }

        /**
         * Validate inputs để fail fast
         */
        private void validateInputs(int page, int limit, String sortBy) {
                if (page < 0)
                        throw new IllegalArgumentException("Page must be >= 0");
                if (limit <= 0 || limit > 1000)
                        throw new IllegalArgumentException("Limit must be 1-1000");
                if (sortBy == null || sortBy.trim().isEmpty()) {
                        throw new IllegalArgumentException("SortBy cannot be empty");
                }
        }

        /**
         * Batch mapping với size optimization
         */
        private <DTO> List<DTO> mapEntitiesToDtos(List<T> entities, Class<DTO> dtoClass) {
                if (entities.isEmpty()) {
                        return Collections.emptyList();
                }

                int size = entities.size();

                // Parallel chỉ khi > 50 items và ModelMapper thread-safe
                if (size > 50) {
                        return entities.parallelStream()
                                        .map(entity -> mapEntityToDto(entity, dtoClass))
                                        .collect(Collectors.toCollection(() -> new ArrayList<>(size)));
                }

                // Sequential với pre-allocated list
                List<DTO> result = new ArrayList<>(size);
                entities.forEach(entity -> result.add(mapEntityToDto(entity, dtoClass)));
                return result;
        }

        /**
         * Single mapping với error handling
         */
        private <DTO> DTO mapEntityToDto(T entity, Class<DTO> dtoClass) {
                try {
                        return modelMapper.map(entity, dtoClass);
                } catch (Exception e) {
                        throw new RuntimeException("Mapping failed for entity: " + entity.getClass(), e);
                }
        }

        @Override
        public <RES> APIResponse<RES> getById(K id, JpaRepository<T, K> repository, Class<T> entityClass,
                        Class<RES> responseClass) {
                T entity = repository.findById(id).orElse(null);
                if (entity == null) {
                        return new APIResponse<>(null, List.of("Entity not found"));
                }
                RES response = modelMapper.map(entity, responseClass);
                return new APIResponse<>(response, List.of("Get data successfully"));
        }
}
