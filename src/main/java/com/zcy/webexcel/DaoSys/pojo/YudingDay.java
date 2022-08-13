package com.zcy.webexcel.DaoSys.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName yuding_day
 */
@TableName(value ="yuding_day")
@Data
public class YudingDay implements Serializable {
    /**
     * 
     */
    @TableField(value = "date")
    private Date date;

    /**
     * 
     */
    @TableField(value = "day_answer")
    private Double dayAnswer;

    /**
     * 
     */
    @TableField(value = "day_call")
    private Double dayCall;

    /**
     * 
     */
    @TableField(value = "day_business")
    private Double dayBusiness;

    /**
     * 
     */
    @TableField(value = "statisfy")
    private Double statisfy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        YudingDay other = (YudingDay) that;
        return (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()))
            && (this.getDayAnswer() == null ? other.getDayAnswer() == null : this.getDayAnswer().equals(other.getDayAnswer()))
            && (this.getDayCall() == null ? other.getDayCall() == null : this.getDayCall().equals(other.getDayCall()))
            && (this.getDayBusiness() == null ? other.getDayBusiness() == null : this.getDayBusiness().equals(other.getDayBusiness()))
            && (this.getStatisfy() == null ? other.getStatisfy() == null : this.getStatisfy().equals(other.getStatisfy()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((getDayAnswer() == null) ? 0 : getDayAnswer().hashCode());
        result = prime * result + ((getDayCall() == null) ? 0 : getDayCall().hashCode());
        result = prime * result + ((getDayBusiness() == null) ? 0 : getDayBusiness().hashCode());
        result = prime * result + ((getStatisfy() == null) ? 0 : getStatisfy().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", date=").append(date);
        sb.append(", dayAnswer=").append(dayAnswer);
        sb.append(", dayCall=").append(dayCall);
        sb.append(", dayBusiness=").append(dayBusiness);
        sb.append(", statisfy=").append(statisfy);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}