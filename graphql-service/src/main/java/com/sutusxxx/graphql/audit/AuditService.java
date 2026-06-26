package com.sutusxxx.graphql.audit;

import com.sutusxxx.graphql.audit.model.AuditEvent;
import com.sutusxxx.graphql.audit.repository.AuditRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void log(AuditEvent event) {
        Audit audit = new Audit();
        audit.setAction(event.action());
        audit.setEntity(event.entityType());
        audit.setEntityId(event.entityId());

        auditRepository.save(audit);
    }
}
