//package com.peterrose.peterrose.config;
//
//import com.peterrose.peterrose.model.*;
//import com.peterrose.peterrose.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Data loader for initial sample data
// * Uses constructor injection
// */
//@Component
//@RequiredArgsConstructor
//public class DataLoader implements CommandLineRunner {
//
//    // Constructor injection for all repositories
//    private final ProductRepository productRepository;
//    private final ReviewRepository reviewRepository;
//    private final CollectionPointRepository collectionPointRepository;
//    private final CategoryRepository categoryRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Load categories first (products depend on them)
//        loadCategories();
//
//        // Load sample products
//        loadProducts();
//
//        // Load sample reviews
//        loadReviews();
//
//        // Load collection points
//        loadCollectionPoints();
//
//        System.out.println("✅ Sample data loaded successfully!");
//    }
//
//    private void loadCategories() {
//        if (categoryRepository.count() > 0) {
//            return; // Data already exists
//        }
//
//        Category roses = new Category();
//        roses.setName("roses");
//        roses.setDisplayName("Roses");
//        roses.setDescription("Beautiful fresh roses in various colors and arrangements");
//        roses.setImageUrl("/images/category-roses.jpg");
//        roses.setActive(true);
//        roses.setDisplayOrder(1);
//
//        Category bouquets = new Category();
//        bouquets.setName("bouquets");
//        bouquets.setDisplayName("Bouquets");
//        bouquets.setDescription("Stunning mixed flower bouquets for any occasion");
//        bouquets.setImageUrl("/images/category-bouquets.jpg");
//        bouquets.setActive(true);
//        bouquets.setDisplayOrder(2);
//
//        Category boxed = new Category();
//        boxed.setName("boxed");
//        boxed.setDisplayName("Boxed Arrangements");
//        boxed.setDescription("Luxury flowers in elegant boxes");
//        boxed.setImageUrl("/images/category-boxed.jpg");
//        boxed.setActive(true);
//        boxed.setDisplayOrder(3);
//
//        Category treats = new Category();
//        treats.setName("treats");
//        treats.setDisplayName("Flowers & Treats");
//        treats.setDescription("Flowers combined with chocolates, wine, and gifts");
//        treats.setImageUrl("/images/category-treats.jpg");
//        treats.setActive(true);
//        treats.setDisplayOrder(4);
//
//        Category valentines = new Category();
//        valentines.setName("valentines");
//        valentines.setDisplayName("Valentine's Special");
//        valentines.setDescription("Romantic arrangements for your loved one");
//        valentines.setImageUrl("/images/category-valentines.jpg");
//        valentines.setActive(true);
//        valentines.setDisplayOrder(5);
//
//        categoryRepository.saveAll(Arrays.asList(roses, bouquets, boxed, treats, valentines));
//    }
//
//    private void loadProducts() {
//        if (productRepository.count() > 0) {
//            return; // Data already exists
//        }
//
//        // Get category references
//        Category rosesCategory = categoryRepository.findByName("roses").orElseThrow();
//        Category bouquetsCategory = categoryRepository.findByName("bouquets").orElseThrow();
//        Category boxedCategory = categoryRepository.findByName("boxed").orElseThrow();
//        Category treatsCategory = categoryRepository.findByName("treats").orElseThrow();
//        Category valentinesCategory = categoryRepository.findByName("valentines").orElseThrow();
//
//        Product p1 = new Product();
//        p1.setName("Petite Rose Box");
//        p1.setDescription("A beautiful small round hat box filled with soft pink and white roses arranged in a dome shape. Perfect for a thoughtful gesture or to brighten someone's day.");
//        p1.setPrice(650.0);
//        p1.setImage("/images/product-petite-box.jpg");
//        p1.setCategory(boxedCategory);
//        p1.setStock(15);
//        p1.setFeatured(true);
//        p1.setBestSeller(true);
//        p1.setRating(4.8);
//        p1.setReviewCount(42);
//
//        Product p2 = new Product();
//        p2.setName("40 Red Roses");
//        p2.setDescription("A stunning bouquet of 40 fresh red roses wrapped in premium kraft paper with a satin ribbon. The classic romantic gesture, perfect for anniversaries and special occasions.");
//        p2.setPrice(600.0);
//        p2.setImage("/images/product-roses.jpg");
//        p2.setCategory(rosesCategory);
//        p2.setStock(20);
//        p2.setFeatured(true);
//        p2.setBestSeller(false);
//        p2.setRating(4.9);
//        p2.setReviewCount(67);
//
//        Product p3 = new Product();
//        p3.setName("Heart Boxed Roses");
//        p3.setDescription("A luxury heart-shaped box filled with perfectly arranged red roses. The ultimate romantic gift for your loved one.");
//        p3.setPrice(2650.0);
//        p3.setImage("/images/product-heart-box.jpg");
//        p3.setCategory(boxedCategory);
//        p3.setStock(8);
//        p3.setFeatured(false);
//        p3.setBestSeller(true);
//        p3.setRating(5.0);
//        p3.setReviewCount(28);
//
//        Product p4 = new Product();
//        p4.setName("100 White Roses");
//        p4.setDescription("An extravagant bouquet of 100 fresh pure white roses. A breathtaking display of elegance and sophistication.");
//        p4.setPrice(2000.0);
//        p4.setImage("/images/product-white-roses.jpg");
//        p4.setCategory(rosesCategory);
//        p4.setStock(5);
//        p4.setFeatured(false);
//        p4.setBestSeller(true);
//        p4.setRating(4.7);
//        p4.setReviewCount(19);
//
//        Product p5 = new Product();
//        p5.setName("Mixed Garden Bouquet");
//        p5.setDescription("A gorgeous mixed flower bouquet with pink peonies, white lilies, blush roses, and green foliage. Beautifully arranged by our expert florists.");
//        p5.setPrice(850.0);
//        p5.setImage("/images/product-mixed-bouquet.jpg");
//        p5.setCategory(bouquetsCategory);
//        p5.setStock(12);
//        p5.setFeatured(true);
//        p5.setBestSeller(false);
//        p5.setRating(4.6);
//        p5.setReviewCount(35);
//
//        Product p6 = new Product();
//        p6.setName("Sunshine Sunflowers");
//        p6.setDescription("A cheerful bouquet of bright yellow sunflowers mixed with white daisies and greenery. Perfect to brighten any room.");
//        p6.setPrice(450.0);
//        p6.setImage("/images/product-sunflowers.jpg");
//        p6.setCategory(bouquetsCategory);
//        p6.setStock(18);
//        p6.setFeatured(true);
//        p6.setBestSeller(false);
//        p6.setRating(4.5);
//        p6.setReviewCount(23);
//
//        Product p7 = new Product();
//        p7.setName("Infinity Love Box");
//        p7.setDescription("Our signature luxury arrangement featuring preserved roses in a premium black box. These roses last up to a year with no water needed.");
//        p7.setPrice(4000.0);
//        p7.setImage("/images/product-heart-box.jpg");
//        p7.setCategory(valentinesCategory);
//        p7.setStock(3);
//        p7.setFeatured(false);
//        p7.setBestSeller(true);
//        p7.setRating(5.0);
//        p7.setReviewCount(15);
//
//        Product p8 = new Product();
//        p8.setName("Chocolate & Roses Hamper");
//        p8.setDescription("Elegant gift hamper combining beautiful roses with premium chocolates and a bottle of sparkling wine. The perfect celebration package.");
//        p8.setPrice(1250.0);
//        p8.setImage("/images/category-treats.jpg");
//        p8.setCategory(treatsCategory);
//        p8.setStock(10);
//        p8.setFeatured(false);
//        p8.setBestSeller(false);
//        p8.setRating(4.8);
//        p8.setReviewCount(31);
//
//        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8));
//    }
//
//    private void loadReviews() {
//        if (reviewRepository.count() > 0) {
//            return;
//        }
//
//        // Get product IDs after saving
//        Product p1 = productRepository.findByNameContainingIgnoreCase("Petite Rose Box").get(0);
//
//        Review r1 = new Review();
//        r1.setProductId(p1.getId());
//        r1.setUserName("Thandi M.");
//        r1.setRating(5);
//        r1.setComment("Absolutely stunning arrangement! The roses were fresh and beautifully arranged. My wife loved it. Will definitely order again.");
//        r1.setDate(LocalDate.of(2026, 1, 15));
//
//        Review r2 = new Review();
//        r2.setProductId(p1.getId());
//        r2.setUserName("Lerato B.");
//        r2.setRating(5);
//        r2.setComment("I've ordered from Peter Rose three times now and every single time, the flowers are impeccable. Best florist in SA!");
//        r2.setDate(LocalDate.of(2026, 2, 5));
//
//        reviewRepository.saveAll(Arrays.asList(r1, r2));
//    }
//
//    private void loadCollectionPoints() {
//        if (collectionPointRepository.count() > 0) {
//            return;
//        }
//
//        CollectionPoint cp1 = new CollectionPoint();
//        cp1.setName("Sandton City");
//        cp1.setAddress("163 Rivonia Road, Sandton, 2196");
//        cp1.setProvince("Gauteng");
//        cp1.setHours("Mon-Sat: 9:00 - 17:00");
//        cp1.setActive(true);
//
//        CollectionPoint cp2 = new CollectionPoint();
//        cp2.setName("Rosebank Mall");
//        cp2.setAddress("50 Bath Avenue, Rosebank, 2196");
//        cp2.setProvince("Gauteng");
//        cp2.setHours("Mon-Sat: 9:00 - 17:00");
//        cp2.setActive(true);
//
//        CollectionPoint cp3 = new CollectionPoint();
//        cp3.setName("Menlyn Park");
//        cp3.setAddress("Atterbury Road, Menlyn, 0181");
//        cp3.setProvince("Gauteng");
//        cp3.setHours("Mon-Sat: 9:00 - 17:00");
//        cp3.setActive(true);
//
//        CollectionPoint cp4 = new CollectionPoint();
//        cp4.setName("V&A Waterfront");
//        cp4.setAddress("19 Dock Road, V&A Waterfront, 8001");
//        cp4.setProvince("Western Cape");
//        cp4.setHours("Mon-Sat: 9:00 - 18:00");
//        cp4.setActive(true);
//
//        CollectionPoint cp5 = new CollectionPoint();
//        cp5.setName("Gateway Theatre");
//        cp5.setAddress("1 Palm Boulevard, Umhlanga, 4319");
//        cp5.setProvince("KwaZulu-Natal");
//        cp5.setHours("Mon-Sat: 9:00 - 17:00");
//        cp5.setActive(true);
//
//        collectionPointRepository.saveAll(Arrays.asList(cp1, cp2, cp3, cp4, cp5));
//    }
//}
