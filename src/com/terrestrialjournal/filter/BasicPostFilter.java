package com.terrestrialjournal.filter;
/*
  User: Alasdair
  Date: 4/23/2020
  Time: 5:54 PM                                                                                                    
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
import com.terrestrialjournal.dao.AuthorDao;
import com.terrestrialjournal.entity.AuthorEntity;
import com.terrestrialjournal.service.AuthorizationService;
import com.terrestrialjournal.to.BasicPostFilterTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BasicPostFilter {
    public static final Integer DEFAULT_GALLERY_REPRESENTATION_IMAGES = 4;

    // for listing unpublished articles
    public String userGuid;

    // for internal use only
    // also, userGuid must be translated into this BEFORE the filter goes to dao
    public boolean showUnpublished;

    public AuthorEntity authorEntity;
    public LocalDateTime fromDateTime;
    public LocalDateTime toDateTime;
    public String titleContains;

    public Integer fromCount;
    public Integer limit;

    public Boolean returnEmpty;

    public Integer galleryRepresentationImages;

    public List<Long> ids;

    public static BasicPostFilter fromTO(BasicPostFilterTO to, Session session){

        if (to == null){
            return new BasicPostFilter(); // TODO not entirely sure this is the right behaviour
        }

        String regEx = "\\d\\d\\d\\d-\\d\\d-\\d\\d";

        BasicPostFilter basicPostFilter = new BasicPostFilter();

        basicPostFilter.authorEntity = AuthorDao.getAuthorEntityByLogin(to.login, session);
        if (to.login != null && !to.login.isEmpty() && basicPostFilter.authorEntity == null){
            // in this case, no results should be shown
            basicPostFilter.returnEmpty = true;
            return basicPostFilter;
        }

        if (to.from != null && to.from.matches(regEx)) {
            basicPostFilter.fromDateTime = LocalDate.parse(to.from, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        }

        if (to.to != null && to.to.matches(regEx)) {
            basicPostFilter.toDateTime = LocalDate.parse(to.to, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        }

        basicPostFilter.titleContains = to.titleContains;
        basicPostFilter.fromCount = to.fromCount;
        basicPostFilter.limit = to.limit;

        if (to.galleryRepresentationImages == null){
            basicPostFilter.galleryRepresentationImages = DEFAULT_GALLERY_REPRESENTATION_IMAGES;
        } else {
            basicPostFilter.galleryRepresentationImages = to.galleryRepresentationImages;
        }

        basicPostFilter.ids = to.ids;
        basicPostFilter.userGuid = to.userGuid;

        return basicPostFilter;
    }

    public static BasicPostFilter fromIdList(List<Long> ids){
        BasicPostFilter basicPostFilter = new BasicPostFilter();
        basicPostFilter.ids = ids;
        return basicPostFilter;
    }

    public void verifyGuid(Session session){
        if (this.userGuid != null && !this.userGuid.isEmpty()){
            AuthorEntity requestingAuthor = AuthorizationService.getAuthorEntityBySessionGuid(this.userGuid, session);
            if (requestingAuthor != null){
                this.showUnpublished = true;
            }
        }
    }
}
