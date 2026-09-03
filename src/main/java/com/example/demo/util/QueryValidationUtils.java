package com.example.demo.util;

import com.example.demo.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;

public class QueryValidationUtils {

    public static PageRequest createPageRequest(int page, int size, String sort, List<String> allowedSortProperties) {
        if (page < 0) {
            throw new BadRequestException("'page' must be >= 0");
        }
        if (size <= 0 || size > 200) {
            throw new BadRequestException("'size' must be > 0 and <= 200");
        }
        
        Sort sortObj = parseSort(sort, allowedSortProperties);
        return PageRequest.of(page, size, sortObj);
    }

    private static Sort parseSort(String sort, List<String> allowedSortProperties) {
        if (sort == null || sort.trim().isEmpty()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        
        if (!allowedSortProperties.contains(property)) {
            throw new BadRequestException("Invalid sort property: " + property + ". Allowed: " + allowedSortProperties);
        }

        if (parts.length >= 2) {
            String dirStr = parts[1].trim().toLowerCase();
            if (!dirStr.equals("asc") && !dirStr.equals("desc")) {
                throw new BadRequestException("Invalid sort direction: " + parts[1] + "; expected 'asc' or 'desc'");
            }
            return Sort.by(Sort.Direction.fromString(dirStr), property);
        }
        return Sort.by(property);
    }
}
