package com.project.code.Controller;

@RestController
@RequestMapping("/inventory")
@Autowired
public class InventoryController {
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to indicate that this is a REST controller, which handles HTTP requests and responses.
//    - Use `@RequestMapping("/inventory")` to set the base URL path for all methods in this controller. All endpoints related to inventory will be prefixed with `/inventory`.


// 2. Autowired Dependencies:
//    - Autowire necessary repositories and services:
//      - `ProductRepository` will be used to interact with product data (i.e., finding, updating products).
//      - `InventoryRepository` will handle CRUD operations related to the inventory.
//      - `ServiceClass` will help with the validation logic (e.g., validating product IDs and inventory data).


// 3. Define the `updateInventory` Method:
//    - This method handles HTTP PUT requests to update inventory for a product.
//    - It takes a `CombinedRequest` (containing `Product` and `Inventory`) in the request body.
//    - The product ID is validated, and if valid, the inventory is updated in the database.
//    - If the inventory exists, update it and return a success message. If not, return a message indicating no data available.

    public ResponseEntity<Map<String, String>> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Map<String, String> response = new HashMap<>();

        Product product = combinedRequest.getProduct();
        Inventory inventory = combinedRequest.getInventory();

        // Validate product ID
        if (!serviceClass.validateProductId(product.getId())) {
            response.put("message", "Invalid product ID");
            return ResponseEntity.badRequest().body(response);
        }

        // Get existing inventory
        Inventory existingInventory = serviceClass.getInventoryId(inventory);
        if (existingInventory != null) {
            // Update inventory details
            existingInventory.setQuantity(inventory.getQuantity());
            inventoryRepository.save(existingInventory);

            response.put("message", "Inventory updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No inventory data available for the given product and store");
            return ResponseEntity.badRequest().body(response);
        }
    }

// 4. Define the `saveInventory` Method:
//    - This method handles HTTP POST requests to save a new inventory entry.
//    - It accepts an `Inventory` object in the request body.
//    - It first validates whether the inventory already exists. If it exists, it returns a message stating so. If it doesn’t exist, it saves the inventory and returns a success message.

    public ResponseEntity<Map<String, String>> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();

        // Validate if inventory already exists
        if (!serviceClass.validateInventory(inventory)) {
            response.put("message", "Inventory already exists for this product in the specified store");
            return ResponseEntity.badRequest().body(response);
        }

        // Save new inventory
        inventoryRepository.save(inventory);
        response.put("message", "Inventory saved successfully");
        return ResponseEntity.ok(response);
    }

// 5. Define the `getAllProducts` Method:
//    - This method handles HTTP GET requests to retrieve products for a specific store.
//    - It uses the `storeId` as a path variable and fetches the list of products from the database for the given store.
//    - The products are returned in a `Map` with the key `"products"`.

    public ResponseEntity<Map<String, Object>> getAllProducts(@PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findByStoreId(storeId);
        response.put("products", products);
        return ResponseEntity.ok(response);
    }

// 6. Define the `getProductName` Method:
//    - This method handles HTTP GET requests to filter products by category and name.
//    - If either the category or name is `"null"`, adjust the filtering logic accordingly.
//    - Return the filtered products in the response with the key `"product"`.

    public ResponseEntity<Map<String, Object>> getProductName(@PathVariable String category, @PathVariable String name, @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if (category.equals("null") && !name.equals("null")) {
            products = productRepository.findByNameLike(storeId, name);
        } else if (!category.equals("null") && name.equals("null")) {
            products = productRepository.findByCategoryAndStoreId(category, storeId);
        } else if (!category.equals("null") && !name.equals("null")) {
            products = productRepository.findByNameLikeAndCategory(storeId, name, category);
        } else {
            products = productRepository.findByStoreId(storeId);
        }

        response.put("product", products);
        return ResponseEntity.ok(response);
    }


// 7. Define the `searchProduct` Method:
//    - This method handles HTTP GET requests to search for products by name within a specific store.
//    - It uses `name` and `storeId` as parameters and searches for products that match the `name` in the specified store.
//    - The search results are returned in the response with the key `"product"`.

    public ResponseEntity<Map<String, Object>> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findByNameLike(storeId, name);
        response.put("product", products);
        return ResponseEntity.ok(response);
    }

// 8. Define the `removeProduct` Method:
//    - This method handles HTTP DELETE requests to delete a product by its ID.
//    - It first validates if the product exists. If it does, it deletes the product from the `ProductRepository` and also removes the related inventory entry from the `InventoryRepository`.
//    - Returns a success message with the key `"message"` indicating successful deletion.

    public ResponseEntity<Map<String, String>> removeProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        // Validate product ID
        if (!serviceClass.validateProductId(id)) {
            response.put("message", "Invalid product ID");
            return ResponseEntity.badRequest().body(response);
        }

        // Delete inventory associated with the product
        inventoryRepository.deleteByProductId(id);

        // Delete the product
        productRepository.deleteById(id);

        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }


// 9. Define the `validateQuantity` Method:
//    - This method handles HTTP GET requests to validate if a specified quantity of a product is available in stock for a given store.
//    - It checks the inventory for the product in the specified store and compares it to the requested quantity.
//    - If sufficient stock is available, return `true`; otherwise, return `false`.

    public ResponseEntity<Boolean> validateQuantity(@PathVariable Long productId, @PathVariable Long storeId, @PathVariable Integer quantity) {
        Inventory inventory = inventoryRepository.findByProduct_IdAndStore_Id(productId, storeId);
        if (inventory != null && inventory.getQuantity() >= quantity) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

}
