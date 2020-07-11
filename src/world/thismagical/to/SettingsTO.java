package world.thismagical.to;
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

public class SettingsTO implements Cloneable {

    public String websiteName;
    public String websiteURL;
    public String about;
    public String headerInjection;
    public String bingApiKey;
    public String mapTypeIdStr;
    public Boolean allowDemoAnon;

    public String imageStoragePath;
    public String temporaryFolderPath;
    public String otherFilesStoragePath;

    public Integer previewX;
    public Integer previewY;

    public Integer thumbX;
    public Integer thumbY;

    public Boolean showCookieWarning;
    public String cookieWarningMessage;

    public Integer itemsPerPage;

    public String twitterProfile;
    public String facebookProfile;
    public String instagramProfile;
    public String pinterestProfile;

    public SettingsTO clone(){
        try {
            return (SettingsTO) super.clone();
        } catch (CloneNotSupportedException ex){
            return new SettingsTO();
        }
    }

    public SettingsTO nullNotForPublicAttributes(){
        SettingsTO settingsTO = this.clone();

        settingsTO.imageStoragePath = null;
        settingsTO.otherFilesStoragePath = null;

        return settingsTO;
    }
}
