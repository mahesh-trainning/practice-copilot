package com.mahesh.practicecopilot.api.models;

import java.util.List;

public class CustomerResourceResponse {
    private List<CustomerResourceBean> customers;
    
    public CustomerResourceResponse() {
    }
    
    public CustomerResourceResponse(List<CustomerResourceBean> customers) {
        this.customers = customers;
    }

    public List<CustomerResourceBean> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerResourceBean> customers) {
        this.customers = customers;
    }
}
