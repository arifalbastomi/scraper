/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Scraper {

    private static final String TOKOPEDIA_BASE_URL = "https://www.tokopedia.com";
    private static final String CATEGORY_URL = "/p/handphone-tablet/handphone";
    private static final String PAGE = "?page=";
    private static final String ADSENSE_URL = "https://ta.tokopedia.com/promo";
    private static final String PRODUCT_MAIN = "//div[@data-testid='lstCL2ProductList']/div";
    private static final String PRODUCT_LINK = "a[@data-testid='lnkProductContainer']";
    private static final String PRODUCT_NAME = "//h1[@data-testid='lblPDPDetailProductName']";
    private static final String PRODUCT_DESCRIPTION = "//*[@data-testid='lblPDPDescriptionProduk']";
    private static final String PRODUCT_IMG_LINK = "//*[@data-testid='PDPImageMain']//img";
    private static final String PRODUCT_PRICE = "//*[@data-testid='lblPDPDetailProductPrice']";
    private static final String PRODUCT_RATING = "//*[@data-testid='lblPDPDetailProductRatingNumber']";
    private static final String MERCHANT_NAME = "//*[@data-testid='llbPDPFooterShopName']//h2";

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
        int total=100;
        final WebDrivers webDriver = new WebDrivers();
        final List<Product> products = new ArrayList<>(2);
 
        try {
            List<String> tabs = webDriver.prepareTwoTabs();
            for (int page = 1; products.size() < total; page++) {
                String url = TOKOPEDIA_BASE_URL+CATEGORY_URL + PAGE + page;
                final List<WebElement> items = webDriver.getElementListByScrollingDown(url,
                        PRODUCT_MAIN, tabs.get(0));

                for (WebElement item : items) {
                    Product product = new Product();
                    String path = item.findElement(By.xpath(PRODUCT_LINK)).getAttribute("href");
                    if(path.contains(ADSENSE_URL)) {
                        path =URLDecoder.decode(path.substring(path.indexOf("r=") + 2).split("&")[0],
                StandardCharsets.UTF_8.name()); 
                               
                    }

                    webDriver.getWebpage(path, tabs.get(1)); 

      
                    webDriver.scrollDownSmall();
                    webDriver.waitOnElement(MERCHANT_NAME);

                    String name = webDriver.getText(PRODUCT_NAME);
                    String desc = webDriver.getText(PRODUCT_DESCRIPTION);
                    System.out.println("sss"+desc);
                    String imageLink = webDriver.getText(PRODUCT_IMG_LINK, "SRC");
                    String price = webDriver.getText(PRODUCT_PRICE)
                            .split("Rp")[1].replace(".", "");
                    String merchant = webDriver.getText(MERCHANT_NAME);
                    String rating = webDriver.getText(PRODUCT_RATING);
                    
                    product.setName(name);
                    product.setDescription(desc);
                    product.setImg(imageLink);
                    product.setPrice(price);
                    product.setMerchant(merchant);
                    product.setRating(rating);
                    
                    products.add(product);
                    if (products.size() == total) {
                        break;
                    }
                    webDriver.switchTab(tabs.get(0)); 
                   
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }

        return products;
    }
   
    public static void main(String[] args) throws IOException {
        
        String filename = "PRODUCT_"+ System.currentTimeMillis() + ".csv";
        Scraper jsoupScrapper = new Scraper();
        System.out.println("Please wait downloading data...");
        List<Product> products = jsoupScrapper.extractProducts();
       
        try {
            FileWriter outputfile = new FileWriter(filename);
            CSVWriter writer = new CSVWriter(outputfile);
            String[] header = { "Name","Link IMG", "Price" , "Merchant","Rating","Description" };
            writer.writeNext(header);
            
            for (Product product : products) {
               String[] data = { product.getName(), product.getImg(), product.getPrice(),product.getMerchant(),product.getRating(),product.getDescription() };
                writer.writeNext(data);
               /*System.out.println(
                    String.format("Product:\n%s\n%s\n%s\n%s\n%s\n%s\n\n", product.getName(), product.getPrice(), product.getImg(),product.getMerchant(),product.getRating(),product.getDescription())
                );
               System.out.println(
                    product.getDescription()
                );*/
            }
            writer.close();
            System.out.println("Succes Download data");
        } catch (Exception e) {
             e.printStackTrace();
        }
        
       
    }
    
    
}
