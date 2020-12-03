package work.cxlm.model.enums;



public enum TimeState implements ValueEnum<Integer> {

    /*
     * 时间段空闲
     *
     */
    Time_IDLE(0),

    /*
     * 时间段已经被预定
     *
     */
    Time_ORDER(1),

    /*
     * 时间段已经被预定且被关注
     *
     */
    Time_ATT(2),

    /*
     * 时间段已经被管理员禁用
     *
     */
    Time_STOP(3);


    private Integer value;

    TimeState(Integer value) {
        this.value = value;
    }



    /**
     * 判断当前时间段是否被预约
     */
    public boolean isOrder() {

        return this == Time_ORDER;
    }

    /**
     * 判断当前活动室是否空闲
     */
    public boolean isIdle(){

        return this == Time_IDLE;

    }

    /**
     * 判断当前活动室是否空闲
     */
    public boolean isAtt(){

        return this == Time_ATT || this == Time_IDLE;

    }

    /**
     * 判断当前活动室是否被禁止使用
     */
    public boolean isStop(){
        return this == Time_STOP;
    }

    /*
    * 将时间段设置为预约状态
    * */
    public void setOrder(){
        this.value=Time_ORDER.value;
    }

    /*
    * 将时间段设置空闲
    * */
    public void setTime_IDLE(){
        this.value=Time_IDLE.value;
    }
    /*
     * 将时间段设置禁用
     * */
    public void setBan(){
        this.value=Time_STOP.value;
    }

    @Override
    public Integer getValue() {
        return null;
    }

}
