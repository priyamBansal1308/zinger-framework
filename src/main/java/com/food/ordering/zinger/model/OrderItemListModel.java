package com.food.ordering.zinger.model;

import java.util.List;

public class OrderItemListModel {

    TransactionModel transactionModel;
    List<OrderItemModel> orderItemsList;

    public TransactionModel getTransactionModel() {
        return transactionModel;
    }

    public void setTransactionModel(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
    }

    public List<OrderItemModel> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItemModel> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }

    @Override
    public String toString() {
        return "OrderItemListModel{" +
                "transactionModel=" + transactionModel +
                ", orderItemsList=" + orderItemsList +
                '}';
    }
}
