package com.sagar.question_service.model;

import com.sagar.question_service.model.Response;
import jakarta.persistence.Entity;
import java.util.List;


public class TotalResponse {

    private List<Response> responseList;
    private String uniqueId;

    public List<Response> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<Response> responseList) {
        this.responseList = responseList;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
