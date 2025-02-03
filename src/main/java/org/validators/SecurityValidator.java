package org.validators;

public class SecurityValidator {
    public boolean isValid(String authorization) {
        return authorization != null && authorization.equals("1234567890qawsedrftgthyujkiol");
    }
}
