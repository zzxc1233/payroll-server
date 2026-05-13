package com.myoffice.payroll_system.config;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import com.myoffice.payroll_system.repository.EmployeeRepository;

@Component("securityAccessService")
public class SecurityAccessService {

    private final EmployeeRepository employeeRepository;

    public SecurityAccessService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public boolean canAccessEmployee(Authentication authentication, Long employeeId) {
        if (isAdmin(authentication)) {
            return true;
        }
        return Objects.equals(extractCurrentEmployeeId(authentication), employeeId);
    }

    public boolean canAccessCurrentEmployeeOnly(Authentication authentication) {
        return isAdmin(authentication) || extractCurrentEmployeeId(authentication) != null;
    }

    public boolean canAccessPayroll(Authentication authentication, Long employeeId) {
        return canAccessEmployee(authentication, employeeId);
    }

    public boolean canAccessShiftAssignment(Authentication authentication, Long employeeId) {
        return canAccessEmployee(authentication, employeeId);
    }

    public Long extractCurrentEmployeeId(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }

        Jwt jwt = jwtAuthenticationToken.getToken();
        Object employeeIdClaim = jwt.getClaim("employeeId");
        if (employeeIdClaim instanceof Number number) {
            return number.longValue();
        }
        if (employeeIdClaim instanceof String value && !value.isBlank()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            Optional<Long> employeeId = employeeRepository.findByEmail(email)
                    .map(employee -> employee.getId());
            if (employeeId.isPresent()) {
                return employeeId.get();
            }
        }

        return null;
    }

    public Long requireCurrentEmployeeId(Authentication authentication) {
        Long employeeId = extractCurrentEmployeeId(authentication);
        if (employeeId == null) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Token must contain a valid employeeId claim or an email that matches an employee record.");
        }
        return employeeId;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch("ROLE_ADMIN"::equals);
    }
}
