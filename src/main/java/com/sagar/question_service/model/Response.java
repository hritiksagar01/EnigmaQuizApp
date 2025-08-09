package com.sagar.question_service.model;


public class Response {
    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", response='" + response + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Response(Integer id, String response) {
        this.id = id;
        this.response = response;
    }

    public Response() {
    }

    private Integer id;
    private  String response;
}
