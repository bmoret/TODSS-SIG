package com.snafu.todss.sig.sessies.domain.idgenerator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

public class FilterIdentifierGenerator extends UUIDGenerator implements IdentifierGenerator {
    public Serializable generate(
            SharedSessionContractImplementor sessionImplementor,
            Object object
    ) throws HibernateException {
        Serializable id = sessionImplementor.getEntityPersister(null, object)
                .getClassMetadata().getIdentifier(object, sessionImplementor);
        return id != null ? id : super.generate(sessionImplementor, object);
    }
}