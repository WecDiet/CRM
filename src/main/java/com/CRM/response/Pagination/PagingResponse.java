package com.CRM.response.Pagination;

import java.util.List;

import com.CRM.response.Pagination.Info.PaginationInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponse<T> {
    private boolean success;
    private String message;
    private List<T> data;
    private PaginationInfo pagination;
}