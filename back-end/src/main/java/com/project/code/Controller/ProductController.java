package com.project.code.Controller;

@Autowired
@RestController
@RequestMapping("/product")
public class ProductController {
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to designate it as a REST controller for handling HTTP requests.
//    - Map the class to the `/product` URL using `@RequestMapping("/product")`.


// 2. Autowired Dependencies:
//    - Inject the following dependencies via `@Autowired`:
//        - `ProductRepository` for CRUD operations on products.
//        - `ServiceClass` for product validation and business logic.
//        - `InventoryRepository` for managing the inventory linked to products.


// 3. Define the `addProduct` Method:
//    - Annotate with `@PostMapping` to handle POST requests for adding a new product.
//    - Accept `Product` object in the request body.
//    - Validate product existence using `validateProduct()` in `ServiceClass`.
//    - Save the valid product using `save()` method of `ProductRepository`.
//    - Catch exceptions (e.g., `DataIntegrityViolationException`) and return appropriate error message.

@PostMapping
public ResponseEntity<Map<String, String>> addProduct(@RequestBody Product product) {
    Map<String, String> response = new HashMap<>();

    // Validate product
    if (!serviceClass.validateProduct(product)) {
        response.put("message", "Product already exists");
        return ResponseEntity.badRequest().body(response);
    }

    try {
        // Save product
        productRepository.save(product);
        response.put("message", "Product added successfully");
        return ResponseEntity.ok(response);
    } catch (DataIntegrityViolationException e) {
        response.put("message", "Data integrity violation: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}

// 4. Define the `getProductbyId` Method:
//    - Annotate with `@GetMapping("/product/{id}")` to handle GET requests for retrieving a product by ID.
//    - Accept product ID via `@PathVariable`.
//    - Use `findById(id)` method from `ProductRepository` to fetch the product.
//    - Return the product in a `Map<String, Object>` with key `products`.

    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProductbyId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            response.put("products", product.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

 // 5. Define the `updateProduct` Method:
//    - Annotate with `@PutMapping` to handle PUT requests for updating an existing product.
//    - Accept updated `Product` object in the request body.
//    - Use `save()` method from `ProductRepository` to update the product.
//    - Return a success message with key `message` after updating the product.

    @PutMapping
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();

        // Update product
        productRepository.save(product);
        response.put("message", "Product updated successfully");
        return ResponseEntity.ok(response);
    }


// 6. Define the `filterbyCategoryProduct` Method:
//    - Annotate with `@GetMapping("/category/{name}/{category}")` to handle GET requests for filtering products by `name` and `category`.
//    - Use conditional filtering logic if `name` or `category` is `"null"`.
//    - Fetch products based on category using methods like `findByCategory()` or `findProductBySubNameAndCategory()`.
//    - Return filtered products in a `Map<String, Object>` with key `products`.

    @GetMapping("/category/{name}/{category}")
    public ResponseEntity<Map<String, Object>> filterbyCategoryProduct(@PathVariable String name, @PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if (category.equals("null") && !name.equals("null")) {
            products = productRepository.findByNameLike(name);
        } else if (!category.equals("null") && name.equals("null")) {
            products = productRepository.findByCategory(category);
        } else if (!category.equals("null") && !name.equals("null")) {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        } else {
            products = productRepository.findAll();
        }

        response.put("products", products);
        return ResponseEntity.ok(response);
    }

 // 7. Define the `listProduct` Method:
//    - Annotate with `@GetMapping` to handle GET requests to fetch all products.
//    - Fetch all products using `findAll()` method from `ProductRepository`.
//    - Return all products in a `Map<String, Object>` with key `products`.

    @GetMapping
    public ResponseEntity<Map<String, Object>> listProduct() {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findAll();
        response.put("products", products);
        return ResponseEntity.ok(response);
    }

// 8. Define the `getProductbyCategoryAndStoreId` Method:
//    - Annotate with `@GetMapping("filter/{category}/{storeid}")` to filter products by `category` and `storeId`.
//    - Use `findProductByCategory()` method from `ProductRepository` to retrieve products.
//    - Return filtered products in a `Map<String, Object>` with key `product`.

    @GetMapping("filter/{category}/{storeid}")
    public ResponseEntity<Map<String, Object>> getProductbyCategoryAndStoreId(@PathVariable String category, @PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductByCategoryAndStoreId(category, storeid);
        response.put("product", products);
        return ResponseEntity.ok(response);
    }


// 9. Define the `deleteProduct` Method:
//    - Annotate with `@DeleteMapping("/{id}")` to handle DELETE requests for removing a product by its ID.
//    - Validate product existence using `ValidateProductId()` in `ServiceClass`.
//    - Remove product from `Inventory` first using `deleteByProductId(id)` in `InventoryRepository`.
//    - Remove product from `Product` using `deleteById(id)` in `ProductRepository`.
//    - Return a success message with key `message` indicating product deletion.

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
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

 // 10. Define the `searchProduct` Method:
//    - Annotate with `@GetMapping("/searchProduct/{name}")` to search for products by `name`.
//    - Use `findProductBySubName()` method from `ProductRepository` to search products by name.
//    - Return search results in a `Map<String, Object>` with key `products`.

    @GetMapping("/searchProduct/{name}")
    public ResponseEntity<Map<String, Object>> searchProduct(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductBySubName(name);
        response.put("products", products);
        return ResponseEntity.ok(response);
    }
  
    
}
