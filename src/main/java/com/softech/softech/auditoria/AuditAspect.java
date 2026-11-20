package com.softech.softech.auditoria;

import com.softech.softech.security.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {
    private final AuditLogRepository repository;
    private final HttpServletRequest request;

    public AuditAspect(AuditLogRepository repository, HttpServletRequest request) {
        this.repository = repository;
        this.request = request;
    }

    @Pointcut("@annotation(auditable)")
    public void auditable(AuditAction auditable) {}

    @AfterReturning(pointcut = "auditable(auditable)", returning = "result")
    public void registrar(JoinPoint joinPoint, AuditAction auditable, Object result) {
        Long userId = CurrentUser.id();
        if (userId == null) return;

        // La auditoría nunca debe convertir una operación exitosa en un error 500.
        try {
            AuditLog log = new AuditLog();
            log.setIdUsuario(userId);
            log.setAccion(auditable.value());
            log.setFechaHora(LocalDateTime.now());
            log.setMetodoHttp(request.getMethod());
            log.setRuta(request.getRequestURI());
            repository.save(log);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(AuditAspect.class)
                    .error("No se pudo registrar la auditoría de {}", auditable.value(), e);
        }
    }
}
