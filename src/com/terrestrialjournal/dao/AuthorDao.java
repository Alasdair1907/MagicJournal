package com.terrestrialjournal.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import com.terrestrialjournal.entity.AuthorEntity;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class AuthorDao {

    public static AuthorEntity getArticleAuthor(Long articleId, Session session){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AuthorEntity> criteriaQuery = cb.createQuery(AuthorEntity.class);
        Root<AuthorEntity> root = criteriaQuery.from(AuthorEntity.class);
        CriteriaBuilder.In<Long> inClause = cb.in(root.get("articleId"));
        inClause.value(articleId);
        CriteriaQuery<AuthorEntity> cq = criteriaQuery.select(root).where(inClause);

        AuthorEntity articleAuthor;
        try {
            articleAuthor = session.createQuery(cq).getSingleResult();
        } catch (Exception e){
            articleAuthor = null;
        }

        return articleAuthor;
    }

    public static AuthorEntity getAuthorEntityByLogin(String login, Session session){

        if (login == null || login.isEmpty()){
            return null;
        }

        AuthorEntity author;

        Query<AuthorEntity> query = session.createQuery("from AuthorEntity  where login = :login", AuthorEntity.class);
        query.setParameter("login", login);

        try {
            author = query.getSingleResult();
        } catch (Exception e){
            author = null;
        }

        return author;
    }

    public static List<AuthorEntity> listAllAuthors(Session session){

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AuthorEntity> cq = cb.createQuery(AuthorEntity.class);
        Root<AuthorEntity> rootEntry = cq.from(AuthorEntity.class);
        cq.orderBy(cb.asc(rootEntry.get("authorId")));
        CriteriaQuery<AuthorEntity>  all = cq.select(rootEntry);
        TypedQuery<AuthorEntity> allQuery = session.createQuery(all);

        List<AuthorEntity> testEntities = allQuery.getResultList();

        return testEntities;
    }

    public static AuthorEntity getAuthorEntityById(Long id, Session session){

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AuthorEntity> criteriaQuery = cb.createQuery(AuthorEntity.class);
        Root<AuthorEntity> root = criteriaQuery.from(AuthorEntity.class);
        CriteriaBuilder.In<Long> inClause = cb.in(root.get("authorId"));
        inClause.value(id);
        CriteriaQuery<AuthorEntity> cq = criteriaQuery.select(root).where(inClause);

        AuthorEntity author;
        try {
            author = session.createQuery(cq).getSingleResult();
        } catch (Exception e){
            author = null;
        }

        return author;
    }


}
