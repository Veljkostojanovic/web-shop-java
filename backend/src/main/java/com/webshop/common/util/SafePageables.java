package com.webshop.common.util;

import com.webshop.common.exceptions.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class SafePageables {

    private SafePageables() {
    }

    public static Pageable withSafeSort(Pageable pageable, Set<String> allowedFields, Sort defaultSort) {
        for (Sort.Order order : pageable.getSort()) {
            if (!allowedFields.contains(order.getProperty())) {
                throw new BadRequestException("Invalid sort field: " + order.getProperty());
            }
        }

        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : defaultSort;
        if (sort.getOrderFor("id") == null) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "id"));
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}