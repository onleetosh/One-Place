package org.yearup.models.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.yearup.models.Product;

import java.math.BigDecimal;

public class ShoppingCartItem
{
    /**
     * Properties of a Shopping Cart Item
     */
    private Product product = null;
    private int quantity = 1;
    private BigDecimal discountPercent = BigDecimal.ZERO;

    /**
     * Default constructor for creating a ShoppingCartItem with no predefined values.
     */
    public ShoppingCartItem()
    {
    }

    /**
     * Parameterized constructor for creating a ShoppingCartItem with specific values.
     * @param product         The product associated with this cart item.
     * @param quantity        The quantity of the product in the cart.
     * @param discountPercent The discount percentage to be applied to this item.
     *                        Defaults to 0% if null.
     */
    public ShoppingCartItem(Product product, int quantity, BigDecimal discountPercent) {
        this.product = product;
        this.quantity = quantity;
        this.discountPercent = discountPercent != null ? discountPercent : BigDecimal.ZERO;  // Default discount is 0 if null.
    }

    /**
     * Retrieves the product associated with this cart item.
     * @return The product object.
     */
    public Product getProduct()
    {
        return product;
    }

    /**
     * Sets the product associated with this cart item.
     * @param product The product to associate with this cart item.
     */
    public void setProduct(Product product)
    {
        this.product = product;
    }

    /**
     * Retrieves the quantity of the product in the cart.
     * @return The quantity of the product.
     */
    public int getQuantity()
    {
        return quantity;
    }

    /**
     * Sets the quantity of the product in the cart.
     * @param quantity The quantity to set.
     */
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    /**
     * Retrieves the discount percentage applied to this cart item.
     * @return The discount percentage as a BigDecimal.
     */
    public BigDecimal getDiscountPercent()
    {
        return discountPercent;
    }

    /**
     * Sets the discount percentage applied to this cart item.
     * @param discountPercent The discount percentage to apply.
     */
    public void setDiscountPercent(BigDecimal discountPercent)
    {
        this.discountPercent = discountPercent;
    }

    /**
     * Retrieves the product ID of the product associated with this cart item.
     * This method is annotated with @JsonIgnore to exclude it from JSON serialization.
     * @return The product ID as an integer.
     */
    @JsonIgnore
    public int getProductId()
    {
        return this.product.getProductId();
    }

    /**
     * Calculates the total cost for this cart item, considering the quantity and
     * discount percentage applied.
     * @return The total cost (subtotal after applying discount) as a BigDecimal.
     */
    public BigDecimal getLineTotal()
    {

        BigDecimal basePrice = product.getPrice();
        BigDecimal quantity = new BigDecimal(this.quantity);

        BigDecimal subTotal = basePrice.multiply(quantity);
        BigDecimal discountAmount = subTotal.multiply(discountPercent);

        return subTotal.subtract(discountAmount);
    }
}
