package com.snafu.todss.sig.sessies.data;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpecialInterestGroupRepository extends JpaRepository<SpecialInterestGroup, UUID> {
}
