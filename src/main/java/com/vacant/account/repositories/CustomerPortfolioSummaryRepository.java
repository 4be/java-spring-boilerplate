package com.vacant.account.repositories;

import com.vacant.account.model.entity.CustomerPortfolioSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerPortfolioSummaryRepository extends JpaRepository<CustomerPortfolioSummary, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM dm_analytics.V_GET_CUST_PORTFOLIO_SUMMARY WHERE REPORT_DATE = (SELECT MAX(REPORT_DATE) FROM dm_analytics.V_GET_CUST_PORTFOLIO_SUMMARY WHERE CIF_NUMBER = :cifNumber) AND CIF_NUMBER = :cifNumber")
    List<CustomerPortfolioSummary> findVCustomerPortfolioEODSummaryByCifNumber(String cifNumber);

    @Query(nativeQuery = true, value = "SELECT * FROM dm_analytics.V_GET_CUST_PORTFOLIO_SUMMARY WHERE REPORT_DATE = (SELECT MIN(REPORT_DATE) FROM dm_analytics.V_GET_CUST_PORTFOLIO_SUMMARY WHERE CIF_NUMBER = :cifNumber) AND CIF_NUMBER = :cifNumber")
    List<CustomerPortfolioSummary> findVCustomerPortfolioEOMSummaryByCifNumber(String cifNumber);
}
