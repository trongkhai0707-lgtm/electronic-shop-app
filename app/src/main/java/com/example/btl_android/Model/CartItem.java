package com.example.btl_android.Model;

public class CartItem {
    private String name;
    private long price; // Đã thay đổi từ String thành long
    private int quantity;
    private String imageUrl;

    // Constructor mặc định (cần thiết cho Firebase)
    public CartItem() {
    }

    // Constructor với các tham số
    public CartItem(String name, long price, int quantity, String imageUrl) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
