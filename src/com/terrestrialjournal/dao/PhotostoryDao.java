package com.terrestrialjournal.dao;

import org.hibernate.Session;

import javax.persistence.Query;

public class PhotostoryDao extends PostDao {

    public static void updatePhotostoryTitleImageId(Long photostoryId, Long newImageId, Session session){

        if (photostoryId == null || newImageId == null){
            throw new IllegalArgumentException("updatePhotostoryTitleImageId: null argument");
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query updateQuery = session.createQuery("update PhotostoryEntity set titleImageId = :newImageId where id = :photostoryId");
        updateQuery.setParameter("newImageId", newImageId);
        updateQuery.setParameter("photostoryId", photostoryId);
        updateQuery.executeUpdate();

        session.flush();
    }
}
