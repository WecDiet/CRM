package com.CRM.Util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
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
        protected ModelMapper modelMapper;

        // Repository cụ thể sẽ được inject bởi class con
        @Autowired
        protected JpaRepository<T, K> repository;

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
                Sort sort = getSort(sortBy, direction);
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
                // PaginationInfo paginationInfo = new PaginationInfo(
                // entityPage.getNumber(), // currentPage
                // entityPage.getSize(), // pageSize
                // entityPage.getTotalElements(), // totalElements
                // entityPage.getTotalPages(), // totalPages
                // entityPage.isFirst(), // first
                // entityPage.isLast(), // last
                // entityPage.hasNext(), // hasNext
                // entityPage.hasPrevious() // hasPrevious
                // );

                PaginationInfo paginationInfo = PaginationInfo.builder()
                                .currentPage(entityPage.getNumber())
                                .pageSize(entityPage.getSize())
                                .totalElements(entityPage.getTotalElements())
                                .totalPages(entityPage.getTotalPages())
                                .first(entityPage.isFirst())
                                .last(entityPage.isLast())
                                .hasNext(entityPage.hasNext())
                                .hasPrevious(entityPage.hasPrevious())
                                .build();

                return PagingResponse.<DTO>builder()
                                .success(true)
                                .message("Get data successfully")
                                .data(dtoList)
                                .pagination(paginationInfo)
                                .build();
        }

        private Sort getSort(String sortBy, String direction) {
                if (sortBy == null || sortBy.isBlank()) {
                        return Sort.unsorted(); // Trả về trạng thái không sắp xếp nếu không có tham số
                }
                Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                return Sort.by(sortDirection, sortBy);
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
                        return APIResponse.<RES>builder()
                                        .message("Entity not found")
                                        .data(null)
                                        .build();
                }
                RES response = modelMapper.map(entity, responseClass);
                return APIResponse.<RES>builder()
                                .message("Get data successfully")
                                .data(response)
                                .build();
        }

        @Override
        public String randomCode() {
                long timestamp = System.currentTimeMillis() / 1000;
                int code = ThreadLocalRandom.current().nextInt(100, 999);
                return timestamp + String.valueOf(code);
        }

        // Clean dữ liệu rác nằm
        @Override
        public <T, K> void cleanTrash(
                        JpaRepository<T, K> repository,
                        Specification<T> warningSpec, // Spec cho cảnh báo
                        Specification<T> deleteSpec, // Spec cho xóa vĩnh viễn
                        int warningMinutes,
                        String entityName,
                        Consumer<T> actionFunction) {

                JpaSpecificationExecutor<T> executor = (JpaSpecificationExecutor<T>) repository;
                long warningCount = executor.count(warningSpec);
                if (warningCount > 0) {
                        System.out.println("ALERT [" + entityName + "]: " + warningCount
                                        + " items will be PERMANENTLY DELETED in " + warningMinutes + " minutes!");
                }

                // // Công thức: Hiện tại - 30 ngày
                // long deleteThreshold = currentTime - duration;
                List<T> expiredItems = executor.findAll(deleteSpec, PageRequest.of(0,
                                2000)).getContent();

                if (!expiredItems.isEmpty()) {
                        if (actionFunction != null) {
                                for (T item : expiredItems) {
                                        try {
                                                actionFunction.accept(item);
                                        } catch (Exception e) {
                                                // Log lỗi nhưng không chặn luồng xóa DB (hoặc tùy logic của bạn)
                                                System.err.println("LOG [" + entityName
                                                                + "]: Error cleaning up external resource for item: "
                                                                + e.getMessage());
                                        }
                                }
                        }
                        repository.deleteAll(expiredItems);
                        System.out.println("LOG [" + entityName + "]: Successfully purged "
                                        + expiredItems.size() + " expired entries.");
                }

        }

}
