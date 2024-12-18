package org.yearup.models.cart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart
{
    /**
     * Store the ShoppingCartItems in a HashMap matched to product Id
     */
    private Map<Integer, ShoppingCartItem> items = new HashMap<>();

    /**
     * Retrieves the current items in the shopping cart.
     * @return A map of product IDs to their corresponding ShoppingCartItem objects.
     */
    public Map<Integer, ShoppingCartItem> getItems()
    {
        return items;
    }

    /**
     * Sets the items in the shopping cart.
     * @param items A map of product IDs to ShoppingCartItem objects to be set in the cart.
     */
    public void setItems(Map<Integer, ShoppingCartItem> items)
    {
        this.items = items;
    }

    /**
     * Checks if the shopping cart contains an item with the given product ID.
     *
     * @param productId The product ID to check.
     * @return true if the cart contains the item, false otherwise.
     */
    public boolean contains(int productId)
    {
        return items.containsKey(productId);
    }

    /**
     * Adds an item to the shopping cart. If an item with the same product ID
     * already exists, it will be replaced.
     * @param item The ShoppingCartItem to add to the cart.
     */
    public void add(ShoppingCartItem item)
    {
        items.put(item.getProductId(), item);
    }

    /**
     * Retrieves the ShoppingCartItem for a given product ID.
     * @param productId The product ID of the item to retrieve.
     * @return The ShoppingCartItem associated with the given product ID, or null if not found.
     */
    public ShoppingCartItem get(int productId)
    {
        return items.get(productId);
    }

    /**
     * Calculates the total cost of all items in the shopping cart, considering
     * quantities and line item totals.
     *
     * @return The total cost of all items in the cart as a BigDecimal.
     */
    public BigDecimal getTotal()
    {
        BigDecimal total = items.values()
                                .stream()
                                .map(i -> i.getLineTotal())
                                .reduce( BigDecimal.ZERO, (lineTotal, subTotal) -> subTotal.add(lineTotal));

        return total;
    }

}
