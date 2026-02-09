package com.uamishop.checkout.domain.policy;

import com.uamishop.checkout.domain.dto.StockLine;
import java.util.List;

/**
 * Policy interface for stock validation.
 * Port to Inventario bounded context.
 */
public interface StockValidationPolicy {

    /**
     * Validates that all items have sufficient stock.
     * 
     * @param items the items to validate
     * @return true if all items have sufficient stock, false otherwise
     */
    boolean validate(List<StockLine> items);
}
