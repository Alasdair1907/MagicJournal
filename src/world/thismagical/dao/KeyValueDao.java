package world.thismagical.dao;
/*
  User: Alasdair
  Date: 4/7/2020
  Time: 12:05 PM                                                                                                    
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

import org.hibernate.Session;
import world.thismagical.entity.KeyValueEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class KeyValueDao {

    public static String getValue(String key, Session session){
        KeyValueEntity kve = session.get(KeyValueEntity.class, key);
        if (kve != null){
            return kve.getValue();
        } else {
            return null;
        }
    }

    public static void saveValue(String key, String value, Session session){
        KeyValueEntity keyValueEntity = session.get(KeyValueEntity.class, key);

        if (keyValueEntity == null){
            keyValueEntity = new KeyValueEntity();
        }

        keyValueEntity.setKey(key);
        keyValueEntity.setValue(value);

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(keyValueEntity);
        session.flush();
    }


}
