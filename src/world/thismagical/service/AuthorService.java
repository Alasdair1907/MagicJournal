package world.thismagical.service;

import org.hibernate.Session;
import world.thismagical.dao.AuthorDao;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.AuthorVO;

import java.util.ArrayList;
import java.util.List;

import static world.thismagical.dao.AuthorDao.getAuthorEntityByLogin;

public class AuthorService {
    public static List<AuthorVO> getAllAuthorsVOList(Session session){
        List<AuthorEntity> authorEntities = AuthorDao.listAllAuthors(session);
        List<AuthorVO> authorVOList = new ArrayList<>();

        for (AuthorEntity authorEntity : authorEntities){
            AuthorVO authorVO = new AuthorVO(authorEntity);
            authorVOList.add(authorVO);
        }

        return authorVOList;
    }

}
