package world.thismagical.vo;
/*
  User: Alasdair
  Date: 3/4/2020
  Time: 12:00 AM                                                                                                    
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

import world.thismagical.util.PostAttribution;
import world.thismagical.util.RelationClass;

public class RelationVO {
    public PostAttribution srcAttributionClass;
    public Short srcAttributionClassShort;
    public Long srcObjectId;
    public String srcObjectTitle;

    public PostAttribution dstAttributionClass;
    public Short dstAttributionClassShort;
    public Long dstObjectId;
    public String dstObjectTitle;

    public RelationClass relationClass;
    public Short relationClassShort;
    public Boolean isAuto;
}
