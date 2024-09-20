package com.example.store.dto.stats;

import com.example.store.dto.category.CategoryUserExpenseDTO;

import java.math.BigDecimal;
import java.util.List;

public record UserSpendingStats(List<AmountDate2> spending,
                                BigDecimal avgMonthlySpend,
                                BigDecimal last6MonthTotal,
                                BigDecimal lastYearTotal,
                                List<CategoryUserExpenseDTO> expenseByCategory) {

}
