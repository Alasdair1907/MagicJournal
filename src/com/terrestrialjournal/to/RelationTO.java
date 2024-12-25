package com.terrestrialjournal.to;

import com.terrestrialjournal.vo.RelationVO;

import java.util.ArrayList;
import java.util.List;

public class RelationTO {
    public PostTO postTO;
    public List<RelationVO> postsReferToThis;
    public List<RelationVO> currentPostRelatesTo;

    public static RelationTO getEmpty(PostTO postTO){
        RelationTO relationTO = new RelationTO();
        relationTO.postsReferToThis = new ArrayList<>();
        relationTO.currentPostRelatesTo = new ArrayList<>();
        return relationTO;
    }
}
