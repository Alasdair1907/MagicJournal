package world.thismagical.entity;
/*
  User: Alasdair
  Date: 4/7/2020
  Time: 12:02 PM                                                                                                    
                                        `.------:::--...``.`                                        
                                    `-:+hmmoo+++dNNmo-.``/dh+...                                    
                                   .+/+mNmyo++/+hmmdo-.``.odmo -/`                                  
                                 `-//+ooooo++///////:---..``.````-``                                
                           `````.----:::/::::::::::::--------.....--..`````                         
           ```````````...............---:::-----::::---..------------------........```````          
        `:/+ooooooosssssssyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyysssssssssssssssssssssssssssoo+/:`       
          ``..-:/++ossyhhddddddddmmmmmarea51mbobmlazarmmmmmmmddddddddddddddhhyysoo+//:-..``         
                      ```..--:/+oyhddddmmmmmmmmmmmmmmmmmmmmmmmddddys+/::-..````                     
                                 ``.:oshddmmmmmNNNNNNNNNNNmmmhs+:.`                                 
                                       `.-/+oossssyysssoo+/-.`                                      
                                                                                                   
*/

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="keys_values")
public class KeyValueEntity implements Serializable {

    @Id
    @Column(name="key")
    String key;

    @Column(name="small_value")
    String smallValue;

    @Column(name="large_value")
    String largeValue;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSmallValue() {
        return smallValue;
    }

    public void setSmallValue(String smallValue) {
        this.smallValue = smallValue;
    }

    public String getLargeValue() {
        return largeValue;
    }

    public void setLargeValue(String largeValue) {
        this.largeValue = largeValue;
    }
}
