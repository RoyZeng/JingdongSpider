package net.spider;

import java.util.Date;
import java.util.List;

public class ItemData {

    private String Name;
    private double Price;
    private String Brand;
    private String Model;
    private String Date;    
    private int Rate;
    private List<String> ReviewList;
    private int ID;

    /**
     * Get the value of ID
     *
     * @return the value of ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Set the value of ID
     *
     * @param ID new value of ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Get the value of ReviewList
     *
     * @return the value of ReviewList
     */
    public List<String> getReviewList() {
        return ReviewList;
    }

    /**
     * Set the value of ReviewList
     *
     * @param ReviewList new value of ReviewList
     */
    public void setReviewList( List<String> ReviewList) {
        this.ReviewList = ReviewList;
    }


    /**
     * Get the value of Rate
     *
     * @return the value of Rate
     */
    public int getRate() {
        return Rate;
    }

    /**
     * Set the value of Rate
     *
     * @param Rate new value of Rate
     */
    public void setRate(int Rate) {
        this.Rate = Rate;
    }



    /**
     * Get the value of date
     *
     * @return the value of date
     */
    public String getDate() {
        return Date;
    }

    /**
     * Set the value of date
     *
     * @param Date new value of date
     */
    public void setDate(String Date) {
        this.Date = Date;
    }


    /**
     * Get the value of Model
     *
     * @return the value of Model
     */
    public String getModel() {
        return Model;
    }

    /**
     * Set the value of Model
     *
     * @param Model new value of Model
     */
    public void setModel(String Model) {
        this.Model = Model;
    }

    

    /**
     * Get the value of Brand
     *
     * @return the value of Brand
     */
    public String getBrand() {
        return Brand;
    }

    /**
     * Set the value of Brand
     *
     * @param Brand new value of Brand
     */
    public void setBrand(String Brand) {
        this.Brand = Brand;
    }

    /**
     * Get the value of Price
     *
     * @return the value of Price
     */
    public double getPrice() {
        return Price;
    }

    /**
     * Set the value of Price
     *
     * @param Price new value of Price
     */
    public void setPrice(double Price) {
        this.Price = Price;
    }

    /**
     * Get the value of Name
     *
     * @return the value of Name
     */
    public String getName() {
        return Name;
    }

    /**
     * Set the value of Name
     *
     * @param Name new value of Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    

}
