package work.cxlm.model.enums;

/**
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-21 16:56
 * @application :
 * @Version 1.0
 * @deprecated FIXME：确保房间只存在两个状态后使用 Boolean 代替
 **/
@Deprecated
public enum  RoomState implements ValueEnum<Integer>{
    BAN(0),

    ALLOW(1);

    private Integer value;

    RoomState(Integer value) {
        this.value = value;
    }


    @Override
    public Integer getValue() {
        return null;
    }

    public boolean isBan(){
        return this == BAN;
    }

    public boolean isAllow(){
        return this == ALLOW;
    }
}
