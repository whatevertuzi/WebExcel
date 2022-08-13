package com.zcy.webexcel.DaoSys.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName customer_service
 */
@TableName(value ="customer_service")
@Data
public class CustomerService implements Serializable {
    /**
     * 日期
     */
    @TableId(value = "date")
    private Date date;

    /**
     * 
     */
    @TableField(value = "title")
    private String title;

    /**
     * 客户投诉问题处理条数计划量
     */
    @TableField(value = "complaint_plan")
    private Integer complaintPlan;

    /**
     * 客户投诉问题处理条数完成量
     */
    @TableField(value = "complaint_done")
    private Integer complaintDone;

    /**
     * 客户投诉问题处理条数完成率
     */
    @TableField(value = "complaint_rate")
    private Double complaintRate;

    /**
     * 客户投诉问题平均处理时长计划量
     */
    @TableField(value = "time_plan")
    private Double timePlan;

    /**
     * 客户投诉问题平均处理时长完成量
     */
    @TableField(value = "time_done")
    private Double timeDone;

    /**
     * 客户投诉问题平均处理时长完成情况
     */
    @TableField(value = "time_status")
    private String timeStatus;

    /**
     * 酒店责任客诉治理条数计划量
     */
    @TableField(value = "on_hotel_plan")
    private Integer onHotelPlan;

    /**
     * 酒店责任客诉治理条数处理量
     */
    @TableField(value = "on_hotel_done")
    private Integer onHotelDone;

    /**
     * 酒店责任客诉治理条数完成率
     */
    @TableField(value = "on_hotel_rate")
    private Double onHotelRate;

    /**
     * 酒店责任客诉整改单回复率计划量
     */
    @TableField(value = "on_hotel_res_plan")
    private Double onHotelResPlan;

    /**
     * 酒店责任客诉整改单回复率处理量
     */
    @TableField(value = "on_hotel_res_done")
    private Double onHotelResDone;

    /**
     * 酒店责任客诉整改单回复率完成率
     */
    @TableField(value = "on_hotel_res_rate")
    private Double onHotelResRate;

    /**
     * 酒店MIS单处理计划量
     */
    @TableField(value = "hotel_mis_plan")
    private Integer hotelMisPlan;

    /**
     * 酒店MIS单处理完成量
     */
    @TableField(value = "hotel_mis_done")
    private Integer hotelMisDone;

    /**
     * 酒店MIS单处理完成率
     */
    @TableField(value = "hotel_mis_rate")
    private Double hotelMisRate;

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
        CustomerService other = (CustomerService) that;
        return (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getComplaintPlan() == null ? other.getComplaintPlan() == null : this.getComplaintPlan().equals(other.getComplaintPlan()))
            && (this.getComplaintDone() == null ? other.getComplaintDone() == null : this.getComplaintDone().equals(other.getComplaintDone()))
            && (this.getComplaintRate() == null ? other.getComplaintRate() == null : this.getComplaintRate().equals(other.getComplaintRate()))
            && (this.getTimePlan() == null ? other.getTimePlan() == null : this.getTimePlan().equals(other.getTimePlan()))
            && (this.getTimeDone() == null ? other.getTimeDone() == null : this.getTimeDone().equals(other.getTimeDone()))
            && (this.getTimeStatus() == null ? other.getTimeStatus() == null : this.getTimeStatus().equals(other.getTimeStatus()))
            && (this.getOnHotelPlan() == null ? other.getOnHotelPlan() == null : this.getOnHotelPlan().equals(other.getOnHotelPlan()))
            && (this.getOnHotelDone() == null ? other.getOnHotelDone() == null : this.getOnHotelDone().equals(other.getOnHotelDone()))
            && (this.getOnHotelRate() == null ? other.getOnHotelRate() == null : this.getOnHotelRate().equals(other.getOnHotelRate()))
            && (this.getOnHotelResPlan() == null ? other.getOnHotelResPlan() == null : this.getOnHotelResPlan().equals(other.getOnHotelResPlan()))
            && (this.getOnHotelResDone() == null ? other.getOnHotelResDone() == null : this.getOnHotelResDone().equals(other.getOnHotelResDone()))
            && (this.getOnHotelResRate() == null ? other.getOnHotelResRate() == null : this.getOnHotelResRate().equals(other.getOnHotelResRate()))
            && (this.getHotelMisPlan() == null ? other.getHotelMisPlan() == null : this.getHotelMisPlan().equals(other.getHotelMisPlan()))
            && (this.getHotelMisDone() == null ? other.getHotelMisDone() == null : this.getHotelMisDone().equals(other.getHotelMisDone()))
            && (this.getHotelMisRate() == null ? other.getHotelMisRate() == null : this.getHotelMisRate().equals(other.getHotelMisRate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getComplaintPlan() == null) ? 0 : getComplaintPlan().hashCode());
        result = prime * result + ((getComplaintDone() == null) ? 0 : getComplaintDone().hashCode());
        result = prime * result + ((getComplaintRate() == null) ? 0 : getComplaintRate().hashCode());
        result = prime * result + ((getTimePlan() == null) ? 0 : getTimePlan().hashCode());
        result = prime * result + ((getTimeDone() == null) ? 0 : getTimeDone().hashCode());
        result = prime * result + ((getTimeStatus() == null) ? 0 : getTimeStatus().hashCode());
        result = prime * result + ((getOnHotelPlan() == null) ? 0 : getOnHotelPlan().hashCode());
        result = prime * result + ((getOnHotelDone() == null) ? 0 : getOnHotelDone().hashCode());
        result = prime * result + ((getOnHotelRate() == null) ? 0 : getOnHotelRate().hashCode());
        result = prime * result + ((getOnHotelResPlan() == null) ? 0 : getOnHotelResPlan().hashCode());
        result = prime * result + ((getOnHotelResDone() == null) ? 0 : getOnHotelResDone().hashCode());
        result = prime * result + ((getOnHotelResRate() == null) ? 0 : getOnHotelResRate().hashCode());
        result = prime * result + ((getHotelMisPlan() == null) ? 0 : getHotelMisPlan().hashCode());
        result = prime * result + ((getHotelMisDone() == null) ? 0 : getHotelMisDone().hashCode());
        result = prime * result + ((getHotelMisRate() == null) ? 0 : getHotelMisRate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", date=").append(date);
        sb.append(", title=").append(title);
        sb.append(", complaintPlan=").append(complaintPlan);
        sb.append(", complaintDone=").append(complaintDone);
        sb.append(", complaintRate=").append(complaintRate);
        sb.append(", timePlan=").append(timePlan);
        sb.append(", timeDone=").append(timeDone);
        sb.append(", timeStatus=").append(timeStatus);
        sb.append(", onHotelPlan=").append(onHotelPlan);
        sb.append(", onHotelDone=").append(onHotelDone);
        sb.append(", onHotelRate=").append(onHotelRate);
        sb.append(", onHotelResPlan=").append(onHotelResPlan);
        sb.append(", onHotelResDone=").append(onHotelResDone);
        sb.append(", onHotelResRate=").append(onHotelResRate);
        sb.append(", hotelMisPlan=").append(hotelMisPlan);
        sb.append(", hotelMisDone=").append(hotelMisDone);
        sb.append(", hotelMisRate=").append(hotelMisRate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}