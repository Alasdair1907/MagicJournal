package world.thismagical.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import world.thismagical.entity.TestEntity;
import world.thismagical.util.Tools;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.List;

public class TestDao {
    public static List<TestEntity> listTestEntities(){
        Session session = Tools.getNewSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
        Root<TestEntity> rootEntry = cq.from(TestEntity.class);
        CriteriaQuery<TestEntity>  all = cq.select(rootEntry);
        TypedQuery<TestEntity> allQuery = session.createQuery(all);

        List<TestEntity> testEntities = allQuery.getResultList();

        session.close();

        return testEntities;
    }
}
