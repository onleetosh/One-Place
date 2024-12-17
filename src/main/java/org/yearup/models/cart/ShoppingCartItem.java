package org.yearup.models.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.yearup.models.Product;

import java.math.BigDecimal;

public class ShoppingCartItem
{
    private Product product = null;
    private int quantity = 1;
    private BigDecimal discountPercent = BigDecimal.ZERO;


    public ShoppingCartItem()
    {
    }
    public ShoppingCartItem(Product product, int quantity, BigDecimal discountPercent) {
        this.product = product;
        this.quantity = quantity;
        this.discountPercent = discountPercent != null ? discountPercent : BigDecimal.ZERO;  // Default discount is 0 if null.
    }
    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercent()
    {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent)
    {
        this.discountPercent = discountPercent;
    }

    @JsonIgnore
    public int getProductId()
    {
        return this.product.getProductId();
    }

    public BigDecimal getLineTotal()
    {

        BigDecimal basePrice = product.getPrice();
        BigDecimal quantity = new BigDecimal(this.quantity);

        BigDecimal subTotal = basePrice.multiply(quantity);
        BigDecimal discountAmount = subTotal.multiply(discountPercent);

        return subTotal.subtract(discountAmount);
    }
}
