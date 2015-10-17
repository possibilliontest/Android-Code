package com.possibillion.example;

/**
 * Created by Chekhra on 10/17/15.
 */
public class ListRequest {
    private int start_value;
    private int pagination;


    public ListRequest(int start_value, int pagination) {
        this.start_value = start_value;
        this.pagination = pagination;
    }

    public int getStart_value() {
        return start_value;
    }

    public void setStart_value(int start_value) {
        this.start_value = start_value;
    }

    public int getPagination() {
        return pagination;
    }

    public void setPagination(int pagination) {
        this.pagination = pagination;
    }
}
