package com.example.demo.util;

import com.example.demo.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryValidationUtilsTest {

    private final List<String> allowed = List.of("id", "price");

    @Test
    void createPageRequest_Valid() {
        PageRequest pr = QueryValidationUtils.createPageRequest(0, 10, "price,desc", allowed);
        assertEquals(0, pr.getPageNumber());
        assertEquals(10, pr.getPageSize());
        assertEquals(Sort.Direction.DESC, pr.getSort().getOrderFor("price").getDirection());
    }

    @Test
    void createPageRequest_NegativePage() {
        assertThrows(BadRequestException.class, () -> QueryValidationUtils.createPageRequest(-1, 10, "price", allowed));
    }

    @Test
    void createPageRequest_InvalidSize() {
        assertThrows(BadRequestException.class, () -> QueryValidationUtils.createPageRequest(0, 0, "price", allowed));
        assertThrows(BadRequestException.class, () -> QueryValidationUtils.createPageRequest(0, 201, "price", allowed));
    }

    @Test
    void parseSort_NullOrEmpty() {
        PageRequest pr = QueryValidationUtils.createPageRequest(0, 10, null, allowed);
        assertTrue(pr.getSort().isUnsorted());

        PageRequest pr2 = QueryValidationUtils.createPageRequest(0, 10, "   ", allowed);
        assertTrue(pr2.getSort().isUnsorted());
    }

    @Test
    void parseSort_InvalidProperty() {
        assertThrows(BadRequestException.class, () -> QueryValidationUtils.createPageRequest(0, 10, "invalidProp", allowed));
    }

    @Test
    void parseSort_InvalidDirection() {
        assertThrows(BadRequestException.class, () -> QueryValidationUtils.createPageRequest(0, 10, "price,invalid", allowed));
    }

    @Test
    void parseSort_ValidAsc() {
        PageRequest pr = QueryValidationUtils.createPageRequest(0, 10, "price,asc", allowed);
        assertEquals(Sort.Direction.ASC, pr.getSort().getOrderFor("price").getDirection());
    }
}
