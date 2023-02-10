package antifraud.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VerificationResult {
    ALLOWED("ALLOWED"),
    MANUAL_PROCESSING("MANUAL_PROCESSING"),
    PROHIBITED("PROHIBITED");

    private final String name;
}
