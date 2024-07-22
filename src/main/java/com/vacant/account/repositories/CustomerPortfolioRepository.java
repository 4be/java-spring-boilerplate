package com.vacant.account.repositories;

import com.vacant.account.model.entity.EDWCustomerPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CustomerPortfolioRepository extends JpaRepository<EDWCustomerPortfolio, Long> {
    @Query(nativeQuery = true, value = "select * from DM_ANALYTICS.V_GET_CUST_PORTFOLIO where cif_number = :cif and portfolio_flag in ('Bancassurance', 'Reksadana')")
    List<EDWCustomerPortfolio> findVCustomerPortfolioByCifNumberInBancassuranceAndOrReksadana(String cif);
}

