package world.thismagical.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.util.Tools;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

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
