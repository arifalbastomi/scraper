/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper2;

import org.jsoup.Jsoup;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.opencsv.CSVWriter;

public class Scraper2 {

    private static final String TOKOPEDIA_BASE_URL = "https://www.tokopedia.com";
    private static final String CATEGORY_URL = "/p/handphone-tablet/handphone";
    private static final String PAGE = "?page=";
    private static final String PRODUCT_CLASS = "css-16vw0vn";
    private static final String PRODUCT_LINK_SELECTOR = ".css-16vw0vn img";
    private static final String PRODUCT_PRICE_SELECTOR = ".css-4u82jy .css-o5uqvq";
    private static final String PRODUCT_MERCHANT_SELECTOR = ".css-vbihp9 .css-1kr22w3";
    
    class Product {
        
        private String name;
        private String description;
        private String img;
        private String price;
        private String rating;
        private String merchant;
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
         
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        
         public String getImg() {
            return img;
        }
        public void setImg(String img) {
            this.img = img;
        }
        
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
        
        public String getRating() {
            return rating;
        }
        public void setRating(String rating) {
            this.rating = rating;
        }
        
        public String getMerchant() {
            return merchant;
        } 
        public void setMerchant(String merchant) {
            this.merchant = merchant;
        }
      
    }
    
    public List<Product> extractProducts() {
        
        List<Product> products = new ArrayList<>();
        Document doc;
        for (int page = 1; products.size() < 100; page++) {
            
            try {
                doc = Jsoup.connect(TOKOPEDIA_BASE_URL+CATEGORY_URL+PAGE+page).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements productElements = doc.getElementsByClass(PRODUCT_CLASS);
            for (Element productElement : productElements) {
                Product product = new Product();
                
                Elements titleElements = productElement.select(PRODUCT_LINK_SELECTOR);
                if (!titleElements.isEmpty()) {
                    product.setName(titleElements.get(0).attr("alt"));
                }
                
                Elements imgElements = productElement.select(PRODUCT_LINK_SELECTOR);
                if (!imgElements.isEmpty()) {
                    product.setImg(imgElements.get(0).attr("src"));
                }

                Elements priceElements = productElement.select(PRODUCT_PRICE_SELECTOR);
                if (!priceElements.isEmpty()) {
                    product.setPrice(priceElements.get(0).text());
                }

                Elements merchantElements = productElement.select(PRODUCT_MERCHANT_SELECTOR);
                if (!merchantElements.isEmpty()) {
                    product.setMerchant(merchantElements.get(1).text());
                }
                
                products.add(product);
            }
        }
        return products;
    }
    
    public static void main(String[] args) throws IOException {
        String filename = "PRODUCT_"+ System.currentTimeMillis() + ".csv";
        Scraper2 jsoupScrapper = new Scraper2();
        
        System.out.println("Please wait downloading data...");
        List<Product> products = jsoupScrapper.extractProducts();
        
        try {
            FileWriter outputfile = new FileWriter(filename);
            CSVWriter writer = new CSVWriter(outputfile);
            String[] header = { "Name", "IMG", "Price" , "Merchant" };
            writer.writeNext(header);
            for (Product product : products) {
              String[] data = { product.getName(), product.getImg(), product.getPrice(),product.getMerchant() };
              writer.writeNext(data);
               
            }
            writer.close();
            System.out.println("Succes Download data");
        } catch (Exception e) {
             e.printStackTrace();
        }
        
       
    }
    
}
