package com.CRM.response.Pagination.Info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationInfo {
    private int currentPage; // Trang hiện tại (0-based)
    private int pageSize; // Số item mỗi trang
    private long totalElements; // Tổng số phần tử
    private int totalPages; // Tổng số trang
    private boolean first; // Có phải trang đầu không
    private boolean last; // Có phải trang cuối không
    private boolean hasNext; // Có trang tiếp theo không
    private boolean hasPrevious; // Có trang trước không
}
