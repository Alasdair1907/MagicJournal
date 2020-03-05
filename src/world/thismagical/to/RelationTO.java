package world.thismagical.to;

import world.thismagical.vo.RelationVO;

import java.util.List;

public class RelationTO {
    public PostTO postTO;
    public List<RelationVO> postsReferToThis;
    public List<RelationVO> currentPostRelatesTo;
}
