package world.thismagical.service;
/*
  User: Alasdair
  Date: 4/10/2020
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import world.thismagical.dao.KeyValueDao;
import world.thismagical.service.KeyValueService;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.SettingsTO;
import world.thismagical.util.Tools;

public class SettingsService {

    public static final String SETTINGS_KEY = "settings";

    public static JsonAdminResponse<Void> saveSettings(String guid, SettingsTO settingsTO, Session session){
        if (!KeyValueService.hasPermissions(guid, KeyValueService.OP_WRITE, session)){
            return JsonAdminResponse.fail("not authorized for this action");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String settingsTOStr = null;
        try {
            settingsTOStr = objectMapper.writeValueAsString(settingsTO);
        } catch (Exception ex){
            Tools.handleException(ex);
            return JsonAdminResponse.fail("unknown error occured while saving settings");
        }

        KeyValueDao.saveValue(SETTINGS_KEY, settingsTOStr, session);

        return JsonAdminResponse.success(null);
    }

    public static JsonAdminResponse<SettingsTO> getSettingsAuthed(String guid, Session session){
        if (!KeyValueService.hasPermissions(guid, KeyValueService.OP_READ, session)){
            return JsonAdminResponse.fail("not authorized for this action");
        }

        return JsonAdminResponse.success(getSettings(session));
    }

    public static JsonAdminResponse<SettingsTO> getSettingsNoAuth(Session session){
        SettingsTO settingsTO = getSettings(session);
        settingsTO.imageStoragePath = null;
        settingsTO.temporaryFolderPath = null;
        settingsTO.otherFilesStoragePath = null;

        return JsonAdminResponse.success(settingsTO);
    }

    public static SettingsTO getSettings(Session session){
        String settingsTOStr = KeyValueDao.getValue(SETTINGS_KEY, session);

        if (settingsTOStr == null || settingsTOStr.isEmpty()){
            return new SettingsTO();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        SettingsTO settingsTO = null;
        try {
            settingsTO = objectMapper.readValue(settingsTOStr, SettingsTO.class);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return settingsTO;
    }
}
