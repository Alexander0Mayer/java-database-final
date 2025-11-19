package com.project.code.Service;


public class OrderService {
// 1. **saveOrder Method**:
//    - Processes a customer's order, including saving the order details and associated items.
//    - Parameters: `PlaceOrderRequestDTO placeOrderRequest` (Request data for placing an order)
//    - Return Type: `void` (This method doesn't return anything, it just processes the order)

    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        // Implementation steps:
        
    }

// 2. **Retrieve or Create the Customer**:
//    - Check if the customer exists by their email using `findByEmail`.
//    - If the customer exists, use the existing customer; otherwise, create and save a new customer using `customerRepository.save()`.

    public void retrieveOrCreateCustomer(String email) {
        // Implementation steps:
        if (customerRepository.findByEmail(email) != null) {
            return Customer existingCustomer = customerRepository.findByEmail(email);
        } else {
            // Create and save new customer
            Customer newCustomer = new Customer();
            newCustomer.setEmail(email);
            customerRepository.save(newCustomer);
        }
        
    }
// 3. **Retrieve the Store**:
//    - Fetch the store by ID from `storeRepository`.
//    - If the store doesn't exist, throw an exception. Use `storeRepository.findById()`.

    public Store retrieveStore(Long storeId) {
        // Implementation steps:
        Store store = storeRepository.findById(storeId);
        if (store == null) {
            throw new RuntimeException("Store not found");
        }
        return store;
    }

// 4. **Create OrderDetails**:
//    - Create a new `OrderDetails` object and set customer, store, total price, and the current timestamp.
//    - Set the order date using `java.time.LocalDateTime.now()` and save the order with `orderDetailsRepository.save()`.

    public OrderDetails createOrderDetails(Customer customer, Store store, Double totalPrice) {
        // Implementation steps:
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(totalPrice);
        orderDetails.setDate(LocalDateTime.now());
        orderDetailsRepository.save(orderDetails);
        return orderDetails;
    }

// 5. **Create and Save OrderItems**:
//    - For each product purchased, find the corresponding inventory, update stock levels, and save the changes using `inventoryRepository.save()`.
//    - Create and save `OrderItem` for each product and associate it with the `OrderDetails` using `orderItemRepository.save()`.

    public void createAndSaveOrderItems(OrderDetails orderDetails, List<PlaceOrderRequestDTO.OrderProduct> products) {
        // Implementation steps:
        for (PlaceOrderRequestDTO.OrderProduct op : products) {
            Inventory inventory = inventoryRepository.findByStoreIdAndProductId(orderDetails.getStore().getId(), op.getProductId());
            if (inventory == null || inventory.getStock() < op.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product ID: " + op.getProductId());
            }
            // Update stock
            inventory.setStock(inventory.getStock() - op.getQuantity());
            inventoryRepository.save(inventory);
            
            // Create and save OrderItem
            Product product = productRepository.findById(op.getProductId());
            OrderItem orderItem = new OrderItem(orderDetails, product, op.getQuantity(), product.getPrice());
            orderItemRepository.save(orderItem);
        }
    }
   
}
