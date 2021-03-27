package com.snafu.todss.sig.sessies.data;

import com.snafu.todss.sig.sessies.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
}
