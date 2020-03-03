package world.thismagical.util;
/*
  User: Alasdair
  Date: 2/29/2020
  Time: 9:46 PM                                                                                                    
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

public enum RelationClass {
    RELATION_DEPENDENT(Integer.valueOf(0).shortValue()),
    RELATION_RELATED(Integer.valueOf(1).shortValue());

    private Short relation;

    RelationClass(Short relation){
        this.relation = relation;
    }

    public Short getId(){
        return relation;
    }

    public static RelationClass getRelationClass(Short relationId){
        for (RelationClass relationClass : RelationClass.values()){
            if (relationClass.getId().equals(relationId)){
                return relationClass;
            }
        }

        throw new IllegalArgumentException("unknown relation id: "+relationId);
    }
}
