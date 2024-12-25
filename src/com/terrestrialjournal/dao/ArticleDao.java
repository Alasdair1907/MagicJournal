package com.terrestrialjournal.dao;

import org.hibernate.Session;

import javax.persistence.Query;

public class ArticleDao extends PostDao {

    public static void updateArticleTitleImageId(Long articleId, Long newImageId, Session session){

        if (articleId == null || newImageId == null){
            throw new IllegalArgumentException("updateArticleTitleImageId: null argument");
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query updateQuery = session.createQuery("update ArticleEntity set titleImageId = :newImageId where id = :articleId");
        updateQuery.setParameter("newImageId", newImageId);
        updateQuery.setParameter("articleId", articleId);
        updateQuery.executeUpdate();

        session.flush();
    }
}
