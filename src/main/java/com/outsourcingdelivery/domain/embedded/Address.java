package com.outsourcingdelivery.domain.embedded;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Address {

    private String city;

    private String district;

    private String neighborhood;

    protected Address() {
    }

    public Address(String city, String district, String neighborhood) {
        this.city = city;
        this.district = district;
        this.neighborhood = neighborhood;
    }
}
