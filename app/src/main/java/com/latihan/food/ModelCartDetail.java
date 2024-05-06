package com.latihan.food;

public class ModelCartDetail {
    private int id;
    private int cart_id;
    private int menu_id;
    private int qty;
    private Double subtotal;
    private Double price_menu;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getPrice_menu() {
        return price_menu;
    }

    public void setPrice_menu(Double price_menu) {
        this.price_menu = price_menu;
    }
}
