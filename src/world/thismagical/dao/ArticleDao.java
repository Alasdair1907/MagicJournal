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

public class ArticleDao {

    public static List<ArticleEntity> listAllArticles(SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        List<ArticleEntity> list = null;

        try {
            list = listAllArticles(null, session);
        } finally {
            session.close();
        }

        return list;
    }

    public static void deleteArticle(Long articleId, Session session){

        if (articleId == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query delQuery = session.createQuery("delete from ArticleEntity where id= :pid");
        delQuery.setParameter("pid", articleId);
        delQuery.executeUpdate();

        session.flush();
    }

    public static List<ArticleEntity> listAllArticles(AuthorEntity authorFilter, Session session){
        List<ArticleEntity> list = null;

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ArticleEntity> cq = cb.createQuery(ArticleEntity.class);
        Root<ArticleEntity> root = cq.from(ArticleEntity.class);
        cq.orderBy(cb.asc(root.get("creationDate")));

        if (authorFilter != null){
            Predicate authorPredicate = cb.equal(root.get("author"), authorFilter);
            cq.select(root).where(authorPredicate);
        } else {
            cq.select(root);
        }

        list = session.createQuery(cq).list();

        return list;
    }

    public static ArticleEntity getArticleEntityById(Long id, Session session){
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<ArticleEntity> cq = criteriaBuilder.createQuery(ArticleEntity.class);
        Root<ArticleEntity> articleEntityRoot = cq.from(ArticleEntity.class);

        Predicate predicateId = criteriaBuilder.equal(articleEntityRoot.get("id"), id);
        cq.where(predicateId);

        ArticleEntity articleEntity = null;
        try {
            articleEntity = session.createQuery(cq).getSingleResult();
        } catch (Exception ex){
            Tools.log("[WARN] Can't get ArticleEntity with id "+id);
        }

        return articleEntity;
    }

    public static List<ArticleEntity> getArticleEntitiesByIds(List<Long> ids, Session session){

        if (ids == null || ids.isEmpty()){
            return null;
        }

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<ArticleEntity> cq = criteriaBuilder.createQuery(ArticleEntity.class);
        Root<ArticleEntity> articleEntityRoot = cq.from(ArticleEntity.class);

        CriteriaBuilder.In<Long> in = criteriaBuilder.in(articleEntityRoot.get("id"));
        for (Long id: ids){
            in.value(id);
        }

        cq.where(in);

        List<ArticleEntity> articleEntityList = null;
        try {
            articleEntityList = session.createQuery(cq).getResultList();
        } catch (Exception ex){
            Tools.log("[WARN] getArticleEntitiesByIds: "+ex.getMessage());
            Tools.log(Tools.getStackTraceStr(ex));
        }

        return articleEntityList;
    }

    public static void toggleArticlePublish(Long id, Session session){
        ArticleEntity articleEntity = getArticleEntityById(id, session);

        if (Boolean.TRUE.equals(articleEntity.getPublished())){
            articleEntity.setPublished(Boolean.FALSE);
        } else {
            articleEntity.setPublished(Boolean.TRUE);
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(articleEntity);

        session.flush();
    }

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
