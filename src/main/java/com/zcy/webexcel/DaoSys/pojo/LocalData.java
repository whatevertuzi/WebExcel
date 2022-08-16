package com.zcy.webexcel.DaoSys.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @TableName local_data
 */
@TableName(value ="local_data")
@Data
public class LocalData implements Serializable {
    /**
     * 
     */
    @TableId
    private Date date;

    /**
     * 
     */
    private Double daydone;

    /**
     * 
     */
    private Double people;

    /**
     * 
     */
    private Double hour;

    /**
     * 
     */
    private Double signature;

    /**
     * 每日数据  JSONArray格式
     */
    private Object datalist;


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
        LocalData other = (LocalData) that;
        return (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()))
            && (this.getDaydone() == null ? other.getDaydone() == null : this.getDaydone().equals(other.getDaydone()))
            && (this.getPeople() == null ? other.getPeople() == null : this.getPeople().equals(other.getPeople()))
            && (this.getHour() == null ? other.getHour() == null : this.getHour().equals(other.getHour()))
            && (this.getSignature() == null ? other.getSignature() == null : this.getSignature().equals(other.getSignature()))
            && (this.getDatalist() == null ? other.getDatalist() == null : this.getDatalist().equals(other.getDatalist()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((getDaydone() == null) ? 0 : getDaydone().hashCode());
        result = prime * result + ((getPeople() == null) ? 0 : getPeople().hashCode());
        result = prime * result + ((getHour() == null) ? 0 : getHour().hashCode());
        result = prime * result + ((getSignature() == null) ? 0 : getSignature().hashCode());
        result = prime * result + ((getDatalist() == null) ? 0 : getDatalist().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", date=" + date +
                ", daydone=" + daydone +
                ", people=" + people +
                ", hour=" + hour +
                ", signature=" + signature +
                ", datalist=" + datalist +
                ", serialVersionUID=" + serialVersionUID +
                "]";
    }
}