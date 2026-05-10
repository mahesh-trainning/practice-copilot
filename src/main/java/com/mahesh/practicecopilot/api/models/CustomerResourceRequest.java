package com.mahesh.practicecopilot.api.models;

import java.time.LocalDateTime;

public class CustomerResourceRequest {
    private CustomerResourceBean customerResourceBean;

    public CustomerResourceRequest() {
    }

    public CustomerResourceRequest(CustomerResourceBean customerResourceBean) {
        this.customerResourceBean = customerResourceBean;
    }

    public CustomerResourceBean getCustomerResourceBean() {
        return customerResourceBean;
    }

    public void setCustomerResourceBean(CustomerResourceBean customerResourceBean) {
        this.customerResourceBean = customerResourceBean;
    }
}
