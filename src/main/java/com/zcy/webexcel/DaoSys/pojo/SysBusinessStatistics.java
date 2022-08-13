package com.zcy.webexcel.DaoSys.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName customer_business_statistics
 */
@TableName(value ="customer_business_statistics")
@Data
public class SysBusinessStatistics implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField(value = "service_date")
    private Date serviceDate;

    /**
     *
     */
    @TableField(value = "problems_count")
    private Integer problemsCount;

    /**
     *
     */
    @TableField(value = "calls_time")
    private Integer callsTime;

    /**
     *
     */
    @TableField(value = "complaint_time")
    private Integer complaintTime;

    /**
     *
     */
    @TableField(value = "avg_time")
    private Integer avgTime;

    /**
     *
     */
    @TableField(value = "governed_count")
    private Integer governedCount;

    /**
     *
     */
    @TableField(value = "rectify_count")
    private Integer rectifyCount;

    /**
     *
     */
    @TableField(value = "rectify_finish_count")
    private Integer rectifyFinishCount;

    /**
     *
     */
    @TableField(value = "rectify_visit_rate")
    private BigDecimal rectifyVisitRate;

    /**
     *
     */
    @TableField(value = "mis_count")
    private Integer misCount;

    /**
     *
     */
    @TableField(value = "financial_count")
    private Integer financialCount;

    /**
     *
     */
    @TableField(value = "rvisit_count")
    private Integer rvisitCount;

    /**
     *
     */
    @TableField(value = "avg_satisfaction")
    private BigDecimal avgSatisfaction;

    /**
     *
     */
    @TableField(value = "point_count")
    private Integer pointCount;

    /**
     *
     */
    @TableField(value = "point_score")
    private BigDecimal pointScore;

    /**
     *
     */
    @TableField(value = "feedback_count")
    private Integer feedbackCount;

    /**
     *
     */
    @TableField(value = "crs_count")
    private Integer crsCount;

    /**
     *
     */
    @TableField(value = "avg_problems")
    private BigDecimal avgProblems;

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
        SysBusinessStatistics other = (SysBusinessStatistics) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getServiceDate() == null ? other.getServiceDate() == null : this.getServiceDate().equals(other.getServiceDate()))
            && (this.getProblemsCount() == null ? other.getProblemsCount() == null : this.getProblemsCount().equals(other.getProblemsCount()))
            && (this.getCallsTime() == null ? other.getCallsTime() == null : this.getCallsTime().equals(other.getCallsTime()))
            && (this.getComplaintTime() == null ? other.getComplaintTime() == null : this.getComplaintTime().equals(other.getComplaintTime()))
            && (this.getAvgTime() == null ? other.getAvgTime() == null : this.getAvgTime().equals(other.getAvgTime()))
            && (this.getGovernedCount() == null ? other.getGovernedCount() == null : this.getGovernedCount().equals(other.getGovernedCount()))
            && (this.getRectifyCount() == null ? other.getRectifyCount() == null : this.getRectifyCount().equals(other.getRectifyCount()))
            && (this.getRectifyFinishCount() == null ? other.getRectifyFinishCount() == null : this.getRectifyFinishCount().equals(other.getRectifyFinishCount()))
            && (this.getRectifyVisitRate() == null ? other.getRectifyVisitRate() == null : this.getRectifyVisitRate().equals(other.getRectifyVisitRate()))
            && (this.getMisCount() == null ? other.getMisCount() == null : this.getMisCount().equals(other.getMisCount()))
            && (this.getFinancialCount() == null ? other.getFinancialCount() == null : this.getFinancialCount().equals(other.getFinancialCount()))
            && (this.getRvisitCount() == null ? other.getRvisitCount() == null : this.getRvisitCount().equals(other.getRvisitCount()))
            && (this.getAvgSatisfaction() == null ? other.getAvgSatisfaction() == null : this.getAvgSatisfaction().equals(other.getAvgSatisfaction()))
            && (this.getPointCount() == null ? other.getPointCount() == null : this.getPointCount().equals(other.getPointCount()))
            && (this.getPointScore() == null ? other.getPointScore() == null : this.getPointScore().equals(other.getPointScore()))
            && (this.getFeedbackCount() == null ? other.getFeedbackCount() == null : this.getFeedbackCount().equals(other.getFeedbackCount()))
            && (this.getCrsCount() == null ? other.getCrsCount() == null : this.getCrsCount().equals(other.getCrsCount()))
            && (this.getAvgProblems() == null ? other.getAvgProblems() == null : this.getAvgProblems().equals(other.getAvgProblems()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getServiceDate() == null) ? 0 : getServiceDate().hashCode());
        result = prime * result + ((getProblemsCount() == null) ? 0 : getProblemsCount().hashCode());
        result = prime * result + ((getCallsTime() == null) ? 0 : getCallsTime().hashCode());
        result = prime * result + ((getComplaintTime() == null) ? 0 : getComplaintTime().hashCode());
        result = prime * result + ((getAvgTime() == null) ? 0 : getAvgTime().hashCode());
        result = prime * result + ((getGovernedCount() == null) ? 0 : getGovernedCount().hashCode());
        result = prime * result + ((getRectifyCount() == null) ? 0 : getRectifyCount().hashCode());
        result = prime * result + ((getRectifyFinishCount() == null) ? 0 : getRectifyFinishCount().hashCode());
        result = prime * result + ((getRectifyVisitRate() == null) ? 0 : getRectifyVisitRate().hashCode());
        result = prime * result + ((getMisCount() == null) ? 0 : getMisCount().hashCode());
        result = prime * result + ((getFinancialCount() == null) ? 0 : getFinancialCount().hashCode());
        result = prime * result + ((getRvisitCount() == null) ? 0 : getRvisitCount().hashCode());
        result = prime * result + ((getAvgSatisfaction() == null) ? 0 : getAvgSatisfaction().hashCode());
        result = prime * result + ((getPointCount() == null) ? 0 : getPointCount().hashCode());
        result = prime * result + ((getPointScore() == null) ? 0 : getPointScore().hashCode());
        result = prime * result + ((getFeedbackCount() == null) ? 0 : getFeedbackCount().hashCode());
        result = prime * result + ((getCrsCount() == null) ? 0 : getCrsCount().hashCode());
        result = prime * result + ((getAvgProblems() == null) ? 0 : getAvgProblems().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", serviceDate=").append(serviceDate);
        sb.append(", problemsCount=").append(problemsCount);
        sb.append(", callsTime=").append(callsTime);
        sb.append(", complaintTime=").append(complaintTime);
        sb.append(", avgTime=").append(avgTime);
        sb.append(", governedCount=").append(governedCount);
        sb.append(", rectifyCount=").append(rectifyCount);
        sb.append(", rectifyFinishCount=").append(rectifyFinishCount);
        sb.append(", rectifyVisitRate=").append(rectifyVisitRate);
        sb.append(", misCount=").append(misCount);
        sb.append(", financialCount=").append(financialCount);
        sb.append(", rvisitCount=").append(rvisitCount);
        sb.append(", avgSatisfaction=").append(avgSatisfaction);
        sb.append(", pointCount=").append(pointCount);
        sb.append(", pointScore=").append(pointScore);
        sb.append(", feedbackCount=").append(feedbackCount);
        sb.append(", crsCount=").append(crsCount);
        sb.append(", avgProblems=").append(avgProblems);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
