package com.terrestrialjournal.service;
/*
  User: Alasdair
  Date: 4/8/2020
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
import com.terrestrialjournal.dao.KeyValueDao;
import com.terrestrialjournal.entity.AuthorEntity;
import com.terrestrialjournal.to.JsonAdminResponse;
import com.terrestrialjournal.to.KeyValueTO;
import com.terrestrialjournal.util.PrivilegeLevel;

public class KeyValueService {

    public static final String OP_READ = "read";
    public static final String OP_WRITE = "write";


    /**
     * Retrieves a value for the key from the key-value table in the database.
     *
     * @param key     key for which the value should be fetched
     * @param guid    session guid of the user who is attempting to obtain the value
     * @param session hibernate session
     * @return        {@link JsonAdminResponse} with {@link KeyValueTO}
     */
    public static JsonAdminResponse<KeyValueTO> getValue(String key, String guid, Session session){

        if (key == null){
            return JsonAdminResponse.fail("key in key-value pair can not be null");
        }

        if (!hasPermissions(guid, OP_READ, session)){
            return JsonAdminResponse.fail("not authorized for this action");
        }

        KeyValueTO keyValueTO = new KeyValueTO();
        keyValueTO.value = KeyValueDao.getValue(key, session);
        keyValueTO.key = key;

        return JsonAdminResponse.success(keyValueTO);
    }

    /**
     * Sets a value for provided key in the key-value table in the database.
     * If the key already has a value assigned to it, that value will be overwritten.
     *
     * @param key     key for which the value should be saved
     * @param value   the text value to be saved
     * @param guid    session guid of the user who is attempting to save the value
     * @param session hibernate session
     * @return        {@link JsonAdminResponse} without the payload
     */
    public static JsonAdminResponse<Void> setValue(String key, String value, String guid, Session session){

        if (key == null){
            return JsonAdminResponse.fail("key in key-value pair can not be null");
        }

        if (!hasPermissions(guid, OP_WRITE, session)){
            return JsonAdminResponse.fail("not authorized for this action");
        }

        KeyValueDao.saveValue(key, value, session);
        return JsonAdminResponse.success(null);
    }

    /**
     * Superusers and users can write, all logged in users can read.
     * Server-side code for public website can access {@link KeyValueDao} directly to fetch system settings etc.
     *
     * @param guid      session guid of user performing this operation
     * @param operation operation defined in {@link KeyValueService}
     * @param session   hibernate session
     * @return          true, if user has permissions for this operation, false otherwise
     */
    public static boolean hasPermissions(String guid, String operation, Session session){

        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (authorEntity == null){
            return false;
        }

        if (operation.equals(OP_WRITE)){
            return authorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_SUPER_USER || authorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_USER;
        }

        return operation.equals(OP_READ);
    }
}
