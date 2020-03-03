package world.thismagical.util;
/*
  User: Alasdair
  Date: 12/23/2019
  Time: 7:02 PM                                                                                                    
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

public enum PrivilegeLevel {
    PRIVILEGE_TEST(0, "test","Can not modify or create any content or users."),
    PRIVILEGE_USER(1,"user","Can create new content, modify own content and own user."),
    PRIVILEGE_SUPER_USER(2,"superuser", "Can create new content and users, or modify any content and users.");

    private Short id;
    private String name;
    private String description;


    PrivilegeLevel(Integer id, String name, String description){

        this.id = id.shortValue();
        this.name = name;
        this.description = description;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public Short getId(){
        return id;
    }

    public static PrivilegeLevel getPrivilegeLevel(Short id){
        for (PrivilegeLevel privilegeLevel : PrivilegeLevel.values()){
            if (privilegeLevel.id.equals(id)){
                return privilegeLevel;
            }
        }

        throw new IllegalArgumentException("unknown privilege level: "+id);
    }
}
