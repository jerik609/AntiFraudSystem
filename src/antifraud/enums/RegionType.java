package antifraud.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RegionType {

    EAP("East Asia and Pacific"),
    ECA("Europe and Central Asia"),
    HIC("High-Income countries"),
    LAC("High-Income countries"),
    MENA("High-Income countries"),
    SA("High-Income countries"),
    SSA("High-Income countries");

    private final String description;

}
