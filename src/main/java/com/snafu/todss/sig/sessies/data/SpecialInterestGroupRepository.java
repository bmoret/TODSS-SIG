package com.snafu.todss.sig.sessies.data;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpecialInterestGroupRepository extends JpaRepository<SpecialInterestGroup, UUID> {
}
