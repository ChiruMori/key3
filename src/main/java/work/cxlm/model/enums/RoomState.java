package work.cxlm.model.enums;

/**
 * @author beizi
 * create: 2020-11-21 16:56
 * @deprecated 房间只存在两个状态，可以使用 Boolean 代替
 **/
@Deprecated
public enum  RoomState implements ValueEnum<Integer>{
    // 禁用
    BAN(0),

    // 开放
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
